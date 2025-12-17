<template>
  <NuxtLayout>
    <template #actions>
      <!-- Editor actions -->
      <template v-if="
        authStore.user?.role == UserResponseRoleEnum.Editor && 
        authStore.user?.id == article?.editor?.id &&
        article?.articleState == ArticleDetailResponseArticleStateEnum.InReview
        ">
        <UButton color="info" @click="onEdit">Editovat</UButton>
      </template>

      <!-- Chief actions -->
      <template v-if="
        authStore.user?.role == UserResponseRoleEnum.ChiefEditor &&
        authStore.user?.id == article?.author?.id
        ">
        <UButton v-if="article?.articleState == ArticleDetailResponseArticleStateEnum.Published" color="info" @click="setReview">Koncept</UButton>
        <template v-if="article?.articleState == ArticleDetailResponseArticleStateEnum.InReview">
          <UButton color="primary" @click="onPublish">Publikace</UButton>
        </template>
        <UButton color="error" @click="onDelete">Smazat</UButton>
      </template>

      <!-- Admin action -->
      <template v-if="authStore.user?.role == UserResponseRoleEnum.Admin">
        <UButton color="info" @click="onEdit">Editovat</UButton>
        <UButton color="error" @click="onDelete">Smazat</UButton>
      </template>
    </template>
    <div class="p-6 space-y-6">
      <!-- Loading skeleton -->
      <div v-if="loading" class="space-y-3">
        <USkeleton class="h-7 w-48"/>
        <USkeleton class="h-4 w-full"/>
        <USkeleton class="h-4 w-2/3"/>
        <USkeleton class="h-3 w-20"/>
      </div>

      <!-- Error -->
      <div v-else-if="error" class="text-red-600">
        {{ error }}
      </div>

      <!-- Article detail / edit mode -->
      <div v-else-if="article" class="rounded-lg border border-gray-200 bg-white p-4 space-y-4 shadow-sm">

        <!-- Header + edit button -->
        <div class="flex items-start justify-between gap-2">
          <div class="flex-1">
            <!-- View mode title -->
            <h1 v-if="!isEditing" class="text-3xl font-bold">
              {{ article.title }}
            </h1>

            <!-- Edit mode title -->
            <UInput
                v-else
                v-model="form.title"
                placeholder="Název článku"
                class="w-full font-bold text-3xl"
                :ui="{
                base: 'text-2xl font-semibold',
                root: 'py-0'
              }"
            />
          </div>

          <UButton
              icon="i-heroicons-pencil-square"
              variant="ghost"
              size="sm"
              @click="toggleEdit"
          >
            {{ isEditing ? 'Zrušit' : 'Upravit' }}
          </UButton>
        </div>

        <!-- Meta info -->
        <div class="grid grid-cols-2 gap-4 text-gray-600">
          <div >
            <h3 class="font-semibold text-gray-500">Autor</h3>
            <p>{{ article.author?.fullName ?? '—' }}</p>
          </div>

          <div>
            <h3 class="font-semibold text-gray-500">Editor</h3>
            <p v-if="!isEditing">{{ article.editor?.fullName ?? '—' }}</p>
            <user-select v-else v-model="form.editor"/>
          </div>

          <div>
            <h3 class="font-semibold text-gray-500">Publikováno</h3>
            <p>{{ article.publishedAt ? new Date(article.publishedAt).toLocaleString('cs-CZ') : '—' }}</p>
          </div>

          <div>
            <h3 class="font-semibold text-gray-500">Verze</h3>
            <p>{{ article.currentVersion }}</p>
          </div>
        </div>

        <!-- Categories -->
        <div>
          <h3 class="font-semibold text-gray-500 mb-2">Kategorie</h3>
          <div v-if="!isEditing">
            <div v-if="article.categories" class="flex gap-2 flex-wrap">
              <UBadge v-for="cat in Array.from(article.categories)" :key="cat.id" color="gray">
                {{ cat.name }}
              </UBadge>
            </div>
            <p v-else class="text-gray-400">Žádné kategorie</p>
          </div>
          <dashboard-categories-select v-else v-model="form.categories" :preselected-ids="Array.from(article.categories).map(c=>c.id)" label=""/>
        </div>

        <!-- Content -->
        <div>
          <h2 class="text-xl font-semibold mb-2">Obsah článku</h2>

          <div v-if="!isEditing" class="prose dark:prose-invert max-w-none">
            <p v-if="!article.content" class="text-gray-400">Bez obsahu</p>
            <html-render :html="article.content" />
          </div>

          <div v-else class="space-y-3">
            <div class="flex flex-col gap-2 sm:flex-row sm:items-end">
              <div class="flex-1">
                <USelect
                    v-model="selectedVersion"
                    :items="versionOptions"
                    placeholder="Vyberte verzi"
                    class="w-full"
                    :disabled="versionsLoading"
                />
                <p v-if="versionsError" class="text-xs text-red-600 mt-1">{{ versionsError }}</p>
              </div>
              <UButton
                  variant="outline"
                  :disabled="!selectedVersion || versionLoading"
                  :loading="versionLoading"
                  @click="loadSelectedVersion"
              >
                Načíst verzi
              </UButton>
            </div>

            <TiptapEditor v-model="form.content"/>
          </div>

          <!--        <UTextarea-->
          <!--            v-else-->
          <!--            v-model="form.content"-->
          <!--            placeholder="Obsah článku"-->
          <!--            :rows="8"-->
          <!--            class="w-full text-base"-->
          <!--        />-->
        </div>

        <!-- Save button in edit mode -->
        <div v-if="isEditing" class="flex justify-end gap-2 pt-2">
          <UButton
              icon="i-heroicons-check"
              color="primary"
              :loading="saving"
              @click="saveArticle"
          >
            Uložit změny
          </UButton>
        </div>

        <!-- Actions -->
        <div v-else class="flex gap-4 justify-end pt-2">
          <UButton color="info" @click="onEdit">Editovat</UButton>
          <UButton color="error" @click="onDelete">Smazat</UButton>
          <UButton color="primary" @click="onPublish">Publikovat</UButton>
        </div>
      </div>

      <!-- Not found -->
      <div v-else class="text-gray-500">
        Článek nebyl nalezen.
      </div>
    </div>

  </NuxtLayout>
</template>


<script lang="ts">
import { defineComponent } from 'vue';
import {
  ArticleDetailResponseArticleStateEnum,
  UpdateArticleRequestArticleStateEnum,
  UserResponseRoleEnum,
  type ArticleResponse,
  type ArticleVersionResponse,
  type GetArticleRequest,
  type GetArticleVersionRequest,
  type ListArticleVersionsRequest,
  type UpdateArticleRequest
} from '~~/api';
import UserSelect from "~/components/dashboard/userSelect.vue";


export default defineComponent({
  name: 'ArticleDetailPage',
  components: {UserSelect},

  data() {
    return {
      article: null as ArticleResponse | null,
      loading: true,
      saving: false,
      error: '',
      isEditing: false,
      form: {
        title: '',
        content: '',
        author: null,
        editor: null,
        categories: [] as Array<number>,
      },
      versions: [] as ArticleVersionResponse[],
      versionsLoading: false,
      versionsError: '',
      selectedVersion: null as number | null,
      versionLoading: false,
      authStore: useAuthStore(),
      UserResponseRoleEnum,
      ArticleDetailResponseArticleStateEnum
    }
  },

  setup() {
    definePageMeta({ layout: 'dashboard' })
    const toast = useToast()
    return { toast }
  },

  computed: {
    versionOptions(): { label: string; value: number }[] {
      return (this.versions || [])
          .filter((v) => v?.versionNumber != null)
          .map((v) => ({
            value: v.versionNumber as number,
            label: this.formatVersionLabel(v)
          }))
    }
  },

  methods: {
    async fetchArticle() {
      const id = Number(this.$route.params.id)
      if (!id || Number.isNaN(id)) {
        this.error = 'Neplatné ID článku.'
        this.loading = false
        return
      }

      this.loading = true
      this.error = ''
      try {
        const request: GetArticleRequest = { id }
        const res = await this.$articlesApi.getArticle(request)
        this.article = res || null
        if (this.article) {
          this.form.title = this.article.title || ''
          this.form.content = this.article.content || ''
          this.form.author = this.article.author || null
          this.form.editor = this.article.editor || null
          this.form.categories = Array.from(this.article.categories).map(c=>c.id)
        }
      } catch (e: any) {
        console.error(e)
        this.error = e?.message || 'Nepodařilo se načíst článek.'
      } finally {
        this.loading = false
      }
    },

    toggleEdit() {
      if (this.isEditing && this.article) {
          this.form.title = this.article.title || ''
          this.form.content = this.article.content || ''
          this.form.author = this.article.author || null
          this.form.editor = this.article.editor || null
          this.form.categories = Array.from(this.article.categories).map(c=>c.id)
      }
      this.isEditing = !this.isEditing
      if (this.isEditing) {
        this.selectedVersion = null
        this.fetchVersions()
      }
    },

    async fetchVersions() {
      if (!this.article?.id) return
      this.versionsLoading = true
      this.versionsError = ''
      try {
        const request: ListArticleVersionsRequest = {
          articleId: this.article.id,
          pageable: {
            page: 0,
            size: 50,
            sort: ['versionNumber,desc']
          }
        }
        const res = await this.$articlesVersionsApi.listArticleVersions(request)
        this.versions = res?.versions || []
      } catch (e: any) {
        console.error(e)
        this.versionsError = e?.message || 'Nepodařilo se načíst verze.'
      } finally {
        this.versionsLoading = false
      }
    },

    async loadSelectedVersion() {
      if (!this.article?.id || !this.selectedVersion) return
      this.versionLoading = true
      try {
        const request: GetArticleVersionRequest = {
          articleId: this.article.id,
          versionNumber: this.selectedVersion
        }
        const res = await this.$articlesVersionsApi.getArticleVersion(request)
        this.form.content = res?.content || ''
        this.toast.add({ title: `Načtena verze ${this.selectedVersion}`, color: 'primary' })
      } catch (e: any) {
        console.error(e)
        this.toast.add({
          title: 'Chyba při načítání verze',
          description: e?.message || 'Nepodařilo se načíst verzi.',
          color: 'error'
        })
      } finally {
        this.versionLoading = false
      }
    },

    formatVersionLabel(version: ArticleVersionResponse): string {
      const number = version?.versionNumber ?? '?'
      const createdAt = version?.createdAt
          ? new Date(version.createdAt as any).toLocaleString('cs-CZ')
          : ''
      const author = version?.createdBy?.fullName || version?.createdBy?.username || ''
      if (createdAt && author) return `v${number} · ${createdAt} · ${author}`
      if (createdAt) return `v${number} · ${createdAt}`
      return `v${number}`
    },

    async saveArticle() {
      if (!this.article || !this.article.id) return
      if (!this.form.title.trim()) {
        this.toast.add({ title: 'Název je povinný', description: 'Článek musí mít název.', color: 'error' })
        return
      }
      console.log(this.form.categories)
      if(this.form.categories.length === 0) {
        this.toast.add({ title: 'Kategorie jsou povinné', description: 'článek musí mít kategorii.', color: 'error' })
        return
      }

      this.saving = true
      try {
        const payload: UpdateArticleRequest = {
          title: this.form.title.trim(),
          content: this.form.content,
          articleState: this.article.articleState,
          publishedAt: this.article.publishedAt,
          categoryIds: this.form.categories,
          editorUsername: this.form.editor?.username
        }
        const request = { id: this.article.id, updateArticleRequest: payload }
        const updated = await this.$articlesApi.updateArticle(request)
        this.article = updated
        this.isEditing = false
        this.form.title = updated.title || ''
        this.form.content = updated.content || ''
        this.toast.add({ title: 'Článek upraven', description: 'Změny byly úspěšně uloženy.', color: 'primary' })
      } catch (e: any) {
        console.error(e)
        this.toast.add({ title: 'Chyba při ukládání', description: e?.message || 'Nepodařilo se upravit článek.', color: 'error' })
      } finally {
        this.saving = false
      }
    },

    // --- Actions ---
    onEdit() {
      this.toggleEdit()
    },
    async onDelete() {
      if (!this.article?.id) return
      try {
        await this.$articlesApi.deleteArticle({ id: this.article.id })
        this.toast.add({ title: 'Článek smazán', color: 'primary' })
        this.$router.push('/dashboard/articles')
      } catch (e: any) {
        console.error(e)
        this.toast.add({ title: 'Chyba při mazání', description: e?.message || 'Nepodařilo se smazat článek.', color: 'error' })
      }
    },
    async onPublish() {
      if (!this.article?.id) return
      try {
        const categories = [...this.article?.categories?.values()];
        const payload: UpdateArticleRequest = {
          title: this.article.title || '',
          content: this.article.content || '',
          articleState: 'PUBLISHED',
          publishedAt: this.article.publishedAt || new Date().toISOString(),
          categoryIds: categories.map(c => c.id) || [],
          editorUsername: this.article.editor?.username
        }
        const request = { id: this.article.id, updateArticleRequest: payload }
        const updated = await this.$articlesApi.updateArticle(request)
        this.article = updated
        this.toast.add({ title: 'Článek publikován', color: 'primary' })
      } catch (e: any) {
        console.error(e)
        this.toast.add({ title: 'Chyba při publikaci', description: e?.message || 'Nepodařilo se publikovat článek.', color: 'error' })
      }
    },
    async setReview() {
      if (!this.article?.id) return
      try {
        const categories = [...this.article?.categories?.values()];
        const payload: UpdateArticleRequest = {
          title: this.article.title || '',
          content: this.article.content || '',
          articleState: UpdateArticleRequestArticleStateEnum.InReview,
          publishedAt: this.article.publishedAt || new Date().toISOString(),
          categoryIds: categories.map(c => c.id) || [],
          editorUsername: this.article.editor?.username
        }
        const request = { id: this.article.id, updateArticleRequest: payload }
        const updated = await this.$articlesApi.updateArticle(request)
        this.article = updated
        this.toast.add({ title: 'Článek nastaven ke kontrole', color: 'primary' })
      } catch (e: any) {
        console.error(e)
        this.toast.add({ title: 'Chyba při změně stavu', description: e?.message || 'Nepodařilo se nastavit článek.', color: 'error' })
      }
    }
  },

  created() {
    this.fetchArticle()
  }
})
</script>
