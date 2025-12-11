<template>
  <NuxtLayout>
    <template #actions>
      <UButton
          :color="isEditing ? 'gray' : 'primary'"
          :variant="isEditing ? 'solid' : 'ghost'"
          :icon="isEditing ? 'i-heroicons-x-mark' : 'i-heroicons-pencil-square'"
          size="sm"
          @click="toggleEdit"
      >
        {{ isEditing ? 'Zrušit úpravy' : 'Upravit' }}
      </UButton>
    </template>

    <div class="min-h-screen p-4 md:p-8 font-sans text-slate-800">

      <div v-if="loadingTicket" class="max-w-7xl mx-auto space-y-8">
        <div class="flex gap-4">
          <USkeleton class="h-8 w-24" />
          <USkeleton class="h-8 w-full max-w-lg" />
        </div>
        <div class="grid lg:grid-cols-[1fr_320px] gap-8">
          <USkeleton class="h-96 w-full rounded-xl" />
          <USkeleton class="h-64 w-full rounded-xl" />
        </div>
      </div>

      <div v-else-if="error" class="max-w-2xl mx-auto mt-10 p-4 bg-red-50 border border-red-100 text-red-600 rounded-lg text-center">
        <span class="font-bold">Chyba:</span> {{ error }}
      </div>

      <div
          v-else-if="ticket"
          class="max-w-7xl mx-auto grid gap-8 lg:grid-cols-[minmax(0,1fr)_320px] items-start"
      >
        <div class="space-y-8">

          <div class="bg-white rounded-xl shadow-sm border border-slate-200/60 overflow-hidden">

            <div class="p-6 md:p-8 pb-4">
              <div class="flex items-center gap-3 text-xs font-medium text-slate-400 mb-4 font-mono uppercase tracking-wider">
                 <span class="bg-slate-100 text-slate-600 px-2 py-1 rounded">
                   #{{ ticket.id }}
                 </span>
                <span v-if="ticket.createdAt">
                    {{ formatDate(ticket.createdAt) }}
                 </span>
                <span v-if="ticket.updatedAt" class="hidden sm:inline">
                    • Aktualizováno {{ formatDate(ticket.updatedAt) }}
                 </span>
              </div>

              <div class="mb-6">
                <h1
                    v-if="!isEditing"
                    class="text-3xl md:text-4xl font-bold text-slate-900 leading-tight tracking-tight"
                >
                  {{ ticket.title || 'Bez názvu' }}
                </h1>
                <UInput
                    v-else
                    v-model="form.title"
                    size="xl"
                    variant="none"
                    placeholder="Zadejte název úkolu..."
                    :ui="{
                    base: 'text-3xl md:text-4xl font-bold text-slate-900 p-0',
                    placeholder: 'placeholder-slate-300'
                  }"
                    class="w-full border-b border-dashed border-slate-300 focus-within:border-primary-500 transition-colors"
                />
              </div>

              <div class="lg:hidden mb-6">
                <UBadge :color="stateColor(ticket.state)" variant="subtle" size="md">
                  {{ ticket.state ?? 'UNKNOWN' }}
                </UBadge>
              </div>

              <UDivider class="mb-6 border-slate-100" />

              <div class="min-h-[150px]">
                <h2 class="text-xs font-bold text-slate-400 uppercase tracking-wide mb-3 flex items-center gap-2">
                  <span class="i-heroicons-bars-3-bottom-left w-4 h-4"></span>
                  Popis
                </h2>

                <div v-if="!isEditing" class="prose prose-slate prose-lg max-w-none text-slate-700">
                  <p v-if="!ticket.description" class="text-slate-400 italic text-sm">
                    Žádný popis nebyl zadán.
                  </p>
                  <p v-else class="whitespace-pre-line leading-relaxed">
                    {{ ticket.description }}
                  </p>
                </div>

                <UTextarea
                    v-else
                    v-model="form.description"
                    :rows="10"
                    variant="outline"
                    color="gray"
                    placeholder="Detailně popište problém..."
                    class="w-full font-normal text-base bg-slate-50"
                />
              </div>

              <div v-if="isEditing" class="flex justify-end gap-3 mt-6 pt-4 border-t border-slate-100">
                <UButton variant="ghost" color="gray" @click="toggleEdit">Zrušit</UButton>
                <UButton
                    icon="i-heroicons-check"
                    color="primary"
                    :loading="savingTicket"
                    @click="saveTicket"
                >
                  Uložit změny
                </UButton>
              </div>
            </div>
          </div>

          <div class="space-y-6 pt-2">
            <div class="flex items-center justify-between px-1">
              <h2 class="text-lg font-semibold text-slate-800 flex items-center gap-2">
                <span class="i-heroicons-chat-bubble-left-right w-5 h-5 text-slate-400"></span>
                Aktivita a komentáře
              </h2>
            </div>

            <div class="flex gap-4 items-start bg-white p-4 rounded-xl border border-slate-200 shadow-sm transition-shadow focus-within:shadow-md focus-within:border-primary-200">
              <div class="h-8 w-8 rounded-full bg-primary-50 text-primary-600 flex items-center justify-center text-xs font-bold shrink-0">
                {{ authStore.user?.username?.charAt(0).toUpperCase() || 'JA' }}
              </div>
              <div class="flex-1 space-y-3">
                <UTextarea
                    v-model="newComment"
                    :rows="2"
                    variant="none"
                    :ui="{ padding: { sm: 'p-0' } }"
                    placeholder="Napište komentář..."
                    class="w-full text-sm"
                    autoresize
                />
                <div class="flex justify-end pt-2 border-t border-slate-100" v-if="newComment.trim() || addingComment">
                  <UButton
                      size="sm"
                      color="primary"
                      :loading="addingComment"
                      :disabled="!newComment.trim()"
                      @click="addComment"
                  >
                    Odeslat
                  </UButton>
                </div>
              </div>
            </div>

            <div class="space-y-6 relative">
              <div v-if="loadingComments" class="space-y-4 pl-12">
                <USkeleton class="h-16 w-full rounded-lg" />
                <USkeleton class="h-16 w-2/3 rounded-lg" />
              </div>

              <div
                  v-else-if="comments.length === 0"
                  class="text-center py-8 text-slate-400 text-sm"
              >
                Zatím žádná aktivita. Buďte první!
              </div>

              <div v-else v-for="comment in comments" :key="commentKey(comment)" class="group flex gap-4">

                <div class="shrink-0 mt-1">
                  <div class="h-8 w-8 rounded-full bg-white border border-slate-200 text-slate-600 flex items-center justify-center text-xs font-bold shadow-sm">
                    {{ (comment.author?.fullName || comment.author?.username || '?').charAt(0).toUpperCase() }}
                  </div>
                </div>

                <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-2 mb-1">
                     <span class="text-sm font-semibold text-slate-800">
                        {{ comment.author?.fullName || comment.author?.username || 'Uživatel' }}
                     </span>
                    <span class="text-xs text-slate-400">
                        {{ formatDate(comment.createdAt) }}
                     </span>
                  </div>

                  <div v-if="editingCommentKey !== commentKey(comment)" class="text-sm text-slate-700 leading-relaxed whitespace-pre-line shadow rounded-b-xl p-4">
                    {{ comment.content }}
                  </div>

                  <div v-else class="mt-2 space-y-2 bg-white p-3 rounded-lg border border-primary-200 shadow-sm">
                    <UTextarea
                        v-model="editCommentContent"
                        :rows="3"
                        autoresize
                        class="w-full"
                    />
                    <div class="flex justify-end gap-2">
                      <UButton size="xs" variant="ghost" @click="cancelEditComment">Zrušit</UButton>
                      <UButton size="xs" color="primary" :loading="savingComment" @click="saveComment(comment)">Uložit</UButton>
                    </div>
                  </div>

                  <div
                      v-if="comment.author.id === authStore.user.id && editingCommentKey !== commentKey(comment)"
                      class="mt-1 flex gap-3 opacity-0 group-hover:opacity-100 transition-opacity"
                  >
                    <button class="text-xs text-slate-400 hover:text-primary-600 font-medium" @click="startEditComment(comment)">Upravit</button>
                    <button class="text-xs text-slate-400 hover:text-red-600 font-medium" @click="deleteComment(comment)">
                      <span v-if="deletingCommentKey === commentKey(comment)">Mazání...</span>
                      <span v-else>Smazat</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <aside class="hidden lg:block sticky top-6 space-y-6">

          <div class="bg-white rounded-xl shadow-sm border border-slate-200/60 overflow-hidden">
            <div class="bg-slate-50/50 px-4 py-3 border-b border-slate-100">
              <h3 class="text-xs font-bold text-slate-500 uppercase tracking-wider">Detaily</h3>
            </div>

            <div class="divide-y divide-slate-100">

              <div class="p-4 space-y-3 group">
                <div class="flex items-center justify-between">
                    <span class="text-xs font-medium text-slate-500 flex items-center gap-2">
                        <span class="w-2 h-2 rounded-full" :class="{
                            'bg-gray-400': !ticket.state,
                            'bg-blue-500': ticket.state === 'OPEN',
                            'bg-yellow-500': ticket.state === 'FOR_REVIEW',
                            'bg-green-500': ticket.state === 'APPROVED' || ticket.state === 'PUBLISHED',
                        }"></span>
                        Stav
                    </span>
                  <span v-if="changingState" class="text-xs text-primary-600 animate-pulse">Ukládám...</span>
                </div>

                <USelect
                    v-model="stateValue"
                    :items="stateOptions"
                    variant="outline"
                    size="sm"
                    class="w-full"
                    :disabled="changingState || !ticket?.id"
                />
              </div>

              <div class="p-4 space-y-3">
                <span class="text-xs font-medium text-slate-500 block">Přiřazeno</span>

                <div v-if="!isEditing" class="flex items-center gap-3 p-1 rounded-md transition-colors hover:bg-slate-50 cursor-default">
                  <div class="h-8 w-8 rounded-full bg-slate-800 text-white flex items-center justify-center text-xs font-medium ring-2 ring-white">
                    {{ (ticket.assignee?.username || '?').charAt(0).toUpperCase() }}
                  </div>
                  <div class="flex flex-col overflow-hidden">
                      <span class="text-sm font-medium text-slate-900 truncate">
                        {{ ticket.assignee?.fullName || ticket.assignee?.username || 'Nepřiřazeno' }}
                      </span>
                  </div>
                </div>

                <UserSelect
                    v-else
                    v-model="form.assignee"
                    :placeholder="'Vyberte řešitele'"
                    class="w-full"
                />
              </div>

              <div class="p-4 flex items-center justify-between text-sm">
                <span class="text-slate-500 text-xs font-medium">Autor</span>
                <span class="text-slate-700 font-medium">
                    {{ ticket.author?.fullName || ticket.author?.username || '—' }}
                </span>
              </div>

              <div class="p-4 flex items-center justify-between text-sm">
                <span class="text-slate-500 text-xs font-medium">Vazba</span>
                <div v-if="ticket.articleId">
                  <UButton
                      size="2xs"
                      variant="soft"
                      color="primary"
                      icon="i-heroicons-document-text"
                      @click="goToArticle"
                  >
                    Článek #{{ ticket.articleId }}
                  </UButton>
                </div>
                <span v-else class="text-slate-300 text-xs">—</span>
              </div>

            </div>
          </div>

          <div class="text-xs text-slate-400 px-2 space-y-1">
            <p>Vytvořeno: {{ formatDate(ticket.createdAt) }}</p>
            <p v-if="ticket.updatedAt">Upraveno: {{ formatDate(ticket.updatedAt) }}</p>
          </div>

        </aside>
      </div>

      <div v-else class="flex flex-col items-center justify-center min-h-[50vh] text-slate-400">
        <span class="i-heroicons-document-magnifying-glass w-12 h-12 mb-4 opacity-50"></span>
        <p class="text-lg font-medium">Ticket nebyl nalezen</p>
      </div>
    </div>
  </NuxtLayout>
</template>

<script lang="ts">
// ... zbytek scriptu zůstává beze změny, jak bylo požadováno ...
import { defineComponent } from 'vue';
import {
  useRoute,
  useRouter,
  useToast,
  definePageMeta
} from '#imports';
import UserSelect from '~/components/dashboard/userSelect.vue';
import type {
  TicketResponse,
  GetTicketRequest,
  PaginatedTicketCommentResponse,
  TicketCommentResponse,
  UpdateTicketRequest,
  UpdateTicketOperationRequest,
  ChangeTicketStateRequest,
  TransitionTicketRequestTargetStateEnum
} from '~~/api';

export default defineComponent({
  name: 'TicketDetailPage',
  components: { UserSelect },

  data() {
    return {
      authStore: useAuthStore(),
      ticket: null as TicketResponse | null,
      loadingTicket: true,
      savingTicket: false,
      error: '',
      isEditing: false,
      form: {
        title: '',
        description: '',
        assignee: null as any
      },
      stateValue: null as TransitionTicketRequestTargetStateEnum | null,
      changingState: false,
      stateInitializing: true,
      comments: [] as TicketCommentResponse[],
      loadingComments: false,
      newComment: '',
      addingComment: false,
      editingCommentKey: null as string | null,
      editCommentContent: '',
      savingComment: false,
      deletingCommentKey: null as string | null
    };
  },
  // ... zbytek metod je stejný ...
  setup() {
    definePageMeta({ layout: 'dashboard' });
    const toast = useToast();
    const route = useRoute();
    const router = useRouter();
    return { toast, route, router };
  },

  computed: {
    stateOptions(): { label: string; value: TransitionTicketRequestTargetStateEnum; }[] {
      return [
        { label: 'Open', value: 'OPEN' },
        { label: 'In progress', value: 'IN_PROGRESS' },
        { label: 'For review', value: 'FOR_REVIEW' },
        { label: 'Approved', value: 'APPROVED' },
        { label: 'Published', value: 'PUBLISHED' }
      ];
    }
  },

  watch: {
    stateValue(newVal) {
      if (this.stateInitializing) return;
      if (!newVal || !this.ticket?.id) return;
      if (this.ticket.state === newVal) return;
      this.changeState(newVal as TransitionTicketRequestTargetStateEnum);
    }
  },

  methods: {
    formatDate(date: any): string {
      const d = new Date(date);
      if (Number.isNaN(d.getTime())) return '';
      return d.toLocaleString('cs-CZ');
    },

    stateColor(state?: string) {
      if (!state) return 'gray';
      const s = state.toUpperCase();
      if (s === 'OPEN') return 'primary';
      if (s === 'IN_PROGRESS') return 'info';
      if (s === 'FOR_REVIEW') return 'warning';
      if (s === 'APPROVED') return 'success';
      if (s === 'PUBLISHED') return 'primary';
      return 'gray';
    },

    async fetchTicket() {
      const id = Number(this.route.params.id);
      if (!id || Number.isNaN(id)) {
        this.error = 'Neplatné ID ticketu.';
        this.loadingTicket = false;
        return;
      }

      this.loadingTicket = true;
      this.error = '';
      try {
        const req: GetTicketRequest = { ticketId: id };
        const res = await (this as any).$ticketsApi.getTicket(req);
        this.ticket = res || null;

        if (this.ticket) {
          this.form.title = this.ticket.title || '';
          this.form.description = this.ticket.description || '';
          this.form.assignee = this.ticket.assignee || null;

          this.stateInitializing = true;
          this.stateValue = (this.ticket.state as TransitionTicketRequestTargetStateEnum) || null;
          this.$nextTick(() => {
            this.stateInitializing = false;
          });
        }

        this.fetchComments();
      } catch (e: any) {
        console.error(e);
        this.error = e?.message || 'Nepodařilo se načíst ticket.';
      } finally {
        this.loadingTicket = false;
      }
    },

    async fetchComments() {
      if (!this.ticket?.id) return;
      this.loadingComments = true;
      try {
        const req = {
          ticketId: this.ticket.id,
          pageable: { page: 0, size: 100, sort: ['createdAt,asc'] }
        };
        const res: PaginatedTicketCommentResponse = await (this as any).$ticketCommentsApi.listTicketComments(req);
        const list = (res as any).comments || (res as any).ticketComments || [];
        this.comments = list;
      } catch (e: any) {
        console.error(e);
        this.toast.add({ title: 'Nepodařilo se načíst komentáře', color: 'error' });
      } finally {
        this.loadingComments = false;
      }
    },

    toggleEdit() {
      if (this.isEditing && this.ticket) {
        this.form.title = this.ticket.title || '';
        this.form.description = this.ticket.description || '';
        this.form.assignee = this.ticket.assignee || null;
      }
      this.isEditing = !this.isEditing;
    },

    async saveTicket() {
      if (!this.ticket || !this.ticket.id) return;

      if (!this.form.title.trim()) {
        this.toast.add({ title: 'Název je povinný', description: 'Ticket musí mít název.', color: 'error' });
        return;
      }
      if (!this.form.assignee || !(this.form.assignee as any).username) {
        this.toast.add({ title: 'Přiřazený uživatel je povinný', color: 'error' });
        return;
      }

      this.savingTicket = true;
      try {
        const payload: UpdateTicketRequest = {
          title: this.form.title.trim(),
          description: this.form.description?.trim() || undefined,
          assigneeUsername: (this.form.assignee as any).username
        } as any;

        const req: UpdateTicketOperationRequest = {
          ticketId: this.ticket.id,
          updateTicketRequest: payload
        } as any;

        const updated: TicketResponse = await (this as any).$ticketsApi.updateTicket(req);

        this.ticket = updated;
        this.isEditing = false;
        this.form.title = updated.title || '';
        this.form.description = updated.description || '';
        this.form.assignee = updated.assignee || null;

        this.toast.add({ title: 'Ticket upraven', description: 'Změny byly úspěšně uloženy.', color: 'primary' });
      } catch (e: any) {
        console.error(e);
        this.toast.add({ title: 'Chyba při ukládání', description: e?.message || 'Nepodařilo se upravit ticket.', color: 'error' });
      } finally {
        this.savingTicket = false;
      }
    },

    async changeState(targetState: TransitionTicketRequestTargetStateEnum) {
      if (!this.ticket?.id) return;
      this.changingState = true;
      try {
        const req: ChangeTicketStateRequest = {
          ticketId: this.ticket.id,
          transitionTicketRequest: { targetState }
        };
        const updated: TicketResponse = await (this as any).$ticketsApi.changeTicketState(req);
        this.ticket = updated;
        this.stateValue = (updated.state as TransitionTicketRequestTargetStateEnum) || targetState;
        this.toast.add({ title: 'Stav ticketu změněn', color: 'success' });
      } catch (e: any) {
        console.error(e);
        this.stateValue = (this.ticket?.state as TransitionTicketRequestTargetStateEnum) || null;
        this.toast.add({ title: 'Chyba při změně stavu', description: e?.message || 'Nepodařilo se změnit stav ticketu.', color: 'error' });
      } finally {
        this.changingState = false;
      }
    },

    goToArticle() {
      if (!this.ticket?.articleId) return;
      this.router.push(`/dashboard/articles/${this.ticket.articleId}`);
    },

    commentKey(comment: any): string {
      return `${comment.commentNumber ?? comment.id ?? ''}`;
    },

    async addComment() {
      if (!this.ticket?.id) return;
      const content = this.newComment.trim();
      if (!content) return;
      this.addingComment = true;
      try {
        const req = {
          ticketId: this.ticket.id,
          createTicketCommentRequest: { content }
        };
        const created: TicketCommentResponse = await (this as any).$ticketCommentsApi.createTicketComment(req);
        const newComments = [];
        newComments.push(created);

        this.comments = newComments.concat(this.comments);
        this.newComment = '';
        this.toast.add({ title: 'Komentář přidán', color: 'success' });
      } catch (e: any) {
        console.error(e);
        this.toast.add({ title: 'Chyba při přidávání komentáře', description: e?.message || 'Nepodařilo se přidat komentář.', color: 'error' });
      } finally {
        this.addingComment = false;
      }
    },

    startEditComment(comment: any) {
      this.editingCommentKey = this.commentKey(comment);
      this.editCommentContent = comment.content || '';
    },
    cancelEditComment() {
      this.editingCommentKey = null;
      this.editCommentContent = '';
    },
    async saveComment(comment: any) {
      if (!this.ticket?.id || !this.editingCommentKey) return;
      const content = this.editCommentContent.trim();
      if (!content) return;
      this.savingComment = true;
      try {
        const req = {
          ticketId: this.ticket.id,
          commentNumber: comment.commentNumber,
          updateTicketCommentRequest: { content }
        };
        const updated: TicketCommentResponse = await (this as any).$ticketCommentsApi.updateTicketComment(req);
        const idx = this.comments.findIndex((c) => this.commentKey(c) === this.commentKey(comment));
        if (idx !== -1) {
          this.comments.splice(idx, 1, updated);
        }
        this.toast.add({ title: 'Komentář upraven', color: 'primary' });
        this.cancelEditComment();
      } catch (e: any) {
        console.error(e);
        this.toast.add({ title: 'Chyba při úpravě komentáře', description: e?.message || 'Nepodařilo se upravit komentář.', color: 'error' });
      } finally {
        this.savingComment = false;
      }
    },
    async deleteComment(comment: any) {
      if (!this.ticket?.id) return;
      const key = this.commentKey(comment);
      this.deletingCommentKey = key;
      try {
        const req = {
          ticketId: this.ticket.id,
          commentNumber: comment.commentNumber
        };
        await (this as any).$ticketCommentsApi.deleteTicketComment(req);
        this.comments = this.comments.filter((c) => this.commentKey(c) !== key);
        this.toast.add({ title: 'Komentář smazán', color: 'primary' });
      } catch (e: any) {
        console.error(e);
        this.toast.add({ title: 'Chyba při mazání komentáře', description: e?.message || 'Nepodařilo se smazat komentář.', color: 'error' });
      } finally {
        this.deletingCommentKey = null;
      }
    }
  },

  created() {
    this.fetchTicket();
  }
});
</script>