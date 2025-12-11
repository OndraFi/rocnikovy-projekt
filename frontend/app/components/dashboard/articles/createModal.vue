<template>
  <UModal title="nový článek">
    <UButton label="Přidat článek" color="primary" variant="solid" />

    <template #body>

        <form @submit.prevent="onSubmit" class="space-y-5">
          <!-- title -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Název článku
            </label>
            <UInput
                v-model="form.title"
                placeholder="Např. Fotbalové novinky"
                class="w-full"
            />
            <p v-if="errors.title" class="text-xs text-error mt-1">
              {{ errors.title }}
            </p>
          </div>

          <!-- article state -->
<!--          <div>-->
<!--            <label class="block text-sm font-medium text-gray-700 mb-1">-->
<!--              Stav článku-->
<!--            </label>-->
<!--            <USelectMenu-->
<!--                v-model="form.articleState"-->
<!--                :items="articleStateItems"-->
<!--                value-key="value"-->
<!--                class="w-full"-->
<!--            />-->
<!--            <p v-if="errors.articleState" class="text-xs text-error mt-1">-->
<!--              {{ errors.articleState }}-->
<!--            </p>-->
<!--          </div>-->

          <!-- publishedAt -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Datum a čas publikace
            </label>
            <input
                v-model="publishedAtInput"
                type="datetime-local"
                class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring focus:ring-blue-200 focus:border-blue-500"
            />
            <p v-if="errors.publishedAt" class="text-xs text-error mt-1">
              {{ errors.publishedAt }}
            </p>
          </div>

          <!-- content -->
<!--          <div>-->
<!--            <label class="block text-sm font-medium text-gray-700 mb-1">-->
<!--              Obsah-->
<!--            </label>-->
<!--            <UTextarea-->
<!--                v-model="form.content"-->
<!--                :rows="6"-->
<!--                placeholder="Napište obsah článku…"-->
<!--                class="w-full"-->
<!--            />-->
<!--            <p v-if="errors.content" class="text-xs text-error mt-1">-->
<!--              {{ errors.content }}-->
<!--            </p>-->
<!--          </div>-->

          <!-- category IDs -->
          <div>
            <dashboard-categories-select v-model="categories" label="Kategorie (ID, oddělené čárkou)"/>

            <p v-if="errors.categoryIds" class="text-xs text-error mt-1">
              {{ errors.categoryIds }}
            </p>
          </div>

          <!-- editor username (optional) -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Editor (uživatelské jméno, volitelné)
            </label>
            <dashboard-user-select v-model="selectedUser" />
          </div>

          <div class="pt-2 flex justify-end">
            <UButton
                type="submit"
                color="primary"
                :loading="loading"
            >
              Vytvořit článek
            </UButton>
          </div>
        </form>

    </template>
  </UModal>

</template>

<script lang="ts">
import { useToast } from '#imports';
import type {
  CreateArticleOperationRequest,
  CreateArticleRequest,
  CreateArticleRequestArticleStateEnum,
  ArticleResponse, UserResponse
} from '~~/api';

export default {
  name: 'CreateArticleForm',

  emits: ['created'],

  setup() {
    const toast = useToast();
    return { toast };
  },

  data() {
    return {
      form: {
        title: '',
        articleState: 'DRAFT' as CreateArticleRequestArticleStateEnum,
        content: '<h1>Nadpis</h1>',
        categoryIds: [],
        editorUsername: ''
      } as unknown as CreateArticleRequest,
      categories: [],
      selectedUser: null as null | UserResponse,
      publishedAtInput: '',
      categoryIdsInput: '',
      loading: false,
      errors: {
        title: '',
        articleState: '',
        publishedAt: '',
        content: '',
        categoryIds: ''
      } as Record<string, string>
    };
  },

  computed: {
    articleStateItems(): { label: string; value: CreateArticleRequestArticleStateEnum }[] {
      return [
        { label: 'Draft', value: 'DRAFT' },
        { label: 'V recenzi', value: 'IN_REVIEW' },
        { label: 'Publikovaný', value: 'PUBLISHED' },
        { label: 'Smazaný', value: 'DELETED' }
      ];
    }
  },

  methods: {
    resetErrors() {
      this.errors = {
        title: '',
        articleState: '',
        publishedAt: '',
        content: '',
        categoryIds: ''
      };
    },

    validate(): boolean {
      let valid = true;
      this.resetErrors();

      if (!this.form.title.trim()) {
        this.errors.title = 'Vyplňte název článku.';
        valid = false;
      }

      if (!this.form.articleState) {
        this.errors.articleState = 'Vyberte stav článku.';
        valid = false;
      }

      if (!this.publishedAtInput) {
        this.errors.publishedAt = 'Zadejte datum a čas publikace.';
        valid = false;
      } else if (isNaN(new Date(this.publishedAtInput).getTime())) {
        this.errors.publishedAt = 'Zadejte platné datum a čas.';
        valid = false;
      }

      if (!this.form.content.trim()) {
        this.errors.content = 'Vyplňte obsah článku.';
        valid = false;
      }

      if (this.form.categoryIds.size === 0) {
        this.errors.categoryIds = 'Zadejte alespoň jednu kategorii (ID).';
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
      this.form.editorUsername = this.selectedUser?.username;
      this.form.categoryIds = new Set<number>(this.categories);
      console.log(this.form);
      if (!this.validate()) return;

      this.loading = true;

      // publikace
      const publishedAt = new Date(this.publishedAtInput);


      const payload: CreateArticleOperationRequest = {
        createArticleRequest: {
          title: this.form.title,
          articleState: this.form.articleState,
          publishedAt,
          content: this.form.content,
          categoryIds: this.form.categoryIds, // TS Set vs JSON array – generátor si to přebere
          editorUsername: this.form.editorUsername || undefined
        }
      };

      try {
        const res: ArticleResponse = await (this as any).$articlesApi.createArticle(
            payload
        );

        this.toast.add({
          title: 'Článek byl úspěšně vytvořen',
          color: 'success'
        });

        this.$emit('created', res);

        // případný reset
        // this.form.title = '';
        // this.form.articleState = 'DRAFT';
        // this.publishedAtInput = '';
        // this.form.content = '';
        // this.categoryIdsInput = '';
        // this.form.editorUsername = '';
      } catch (e: any) {
        console.error(e);
        this.toast.add({
          title:
              e?.response?.data?.message ||
              e?.message ||
              'Vytvoření článku se nezdařilo',
          color: 'error'
        });
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
</style>
