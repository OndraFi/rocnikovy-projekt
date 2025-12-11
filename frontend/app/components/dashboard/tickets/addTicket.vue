<template>
  <div>
    <form @submit.prevent="onSubmit" class="space-y-5">
      <!-- title -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          Název ticketu
        </label>
        <UInput
            v-model="form.title"
            placeholder="Např. Chyba v článku"
            class="w-full"
        />
        <p v-if="errors.title" class="text-xs text-error mt-1">
          {{ errors.title }}
        </p>
      </div>

      <!-- description -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          Popis (volitelné)
        </label>
        <UTextarea
            v-model="form.description"
            :rows="4"
            placeholder="Popište problém nebo požadavek..."
            class="w-full"
        />
      </div>

      <!-- assignee -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          Přiřazený uživatel
        </label>
        <dashboard-user-select
            v-model="assignee"
            :placeholder="'Vyberte přiřazeného uživatele'"
        />
        <p v-if="errors.assignee" class="text-xs text-error mt-1">
          {{ errors.assignee }}
        </p>
      </div>

      <!-- article -->
      <div>
        <dashboard-articles-select
            v-model="article"
            :placeholder="'Vyberte článek'"
        />
        <p v-if="errors.article" class="text-xs text-error mt-1">
          {{ errors.article }}
        </p>
      </div>

      <div class="pt-2 flex justify-end">
        <UButton
            type="submit"
            color="primary"
            :loading="loading"
        >
          Vytvořit ticket
        </UButton>
      </div>
    </form>
  </div>
</template>

<script lang="ts">
import { useToast } from '#imports';
import type {
  CreateTicketOperationRequest,
  CreateTicketRequest,
  TicketResponse,
  UserResponse,
  ArticleResponse
} from '~~/api';

// pokud nemáš auto-import komponent, odkomentuj:
// import UserSelect from '@/components/UserSelect.vue';
// import ArticleSelect from '@/components/ArticleSelect.vue';

export default {
  name: 'AddTicketForm',

  // components: { UserSelect, ArticleSelect },

  emits: ['created'],

  setup() {
    const toast = useToast();
    return { toast };
  },

  data() {
    return {
      form: {
        title: '',
        description: ''
      } as CreateTicketRequest,
      assignee: null as UserResponse | null,
      article: null as ArticleResponse | null,
      loading: false,
      errors: {
        title: '',
        assignee: '',
        article: ''
      } as Record<string, string>
    };
  },

  methods: {
    resetErrors() {
      this.errors = {
        title: '',
        assignee: '',
        article: ''
      };
    },

    validate(): boolean {
      let valid = true;
      this.resetErrors();

      if (!this.form.title.trim()) {
        this.errors.title = 'Vyplňte název ticketu.';
        valid = false;
      }

      if (!this.assignee || !(this.assignee as any).username) {
        this.errors.assignee = 'Vyberte přiřazeného uživatele.';
        valid = false;
      }

      if (!this.article || (this.article as any).id == null) {
        this.errors.article = 'Vyberte článek.';
        valid = false;
      }

      if (!valid) {
        this.toast.add({
          title: 'Formulář obsahuje chyby',
          color: 'error'
        });
      }

      return valid;
    },

    async onSubmit() {
      if (!this.validate()) return;

      this.loading = true;

      const payload: CreateTicketOperationRequest = {
        createTicketRequest: {
          title: this.form.title,
          description: this.form.description?.trim() || undefined,
          assigneeUsername: (this.assignee as any).username,
          articleId: (this.article as any).id
        }
      };

      try {
        const res: TicketResponse = await (this as any).$ticketsApi.createTicket(
            payload
        );

        this.toast.add({
          title: 'Ticket byl úspěšně vytvořen',
          color: 'success'
        });

        this.$emit('created', res);

        // případný reset:
        // this.form.title = '';
        // this.form.description = '';
        // this.assignee = null;
        // this.article = null;
      } catch (e: any) {
        console.error(e);
        this.toast.add({
          title:
              e?.response?.data?.message ||
              e?.message ||
              'Vytvoření ticketu se nezdařilo',
          color: 'error'
        });
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

