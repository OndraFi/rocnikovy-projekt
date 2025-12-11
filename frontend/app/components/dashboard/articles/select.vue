<template>
  <div class="space-y-1">
    <label v-if="label" class="block text-sm font-medium text-gray-700">
      {{ label }}
    </label>

    <USelectMenu
        v-model="innerValue"
        value-key="value"
        :items="items"
        :placeholder="placeholder"
        :loading="loading && articles.length === 0"
        class="w-full"
        :search-input="false"
    />

    <p v-if="error" class="text-xs text-error mt-1">
      {{ error }}
    </p>
  </div>
</template>

<script lang="ts">
import type {
  ArticleResponse,
  ListArticlesRequest,
  PaginatedArticleResponse
} from '~~/api';
import { useToast } from '#imports';

export default {
  name: 'ArticleSelect',

  props: {
    modelValue: {
      type: Object as () => ArticleResponse | null,
      default: null
    },
    label: {
      type: String,
      default: 'Článek'
    },
    placeholder: {
      type: String,
      default: 'Vyberte článek'
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },

  emits: ['update:modelValue', 'selected'],

  setup() {
    const toast = useToast();
    return { toast };
  },

  data() {
    return {
      innerValue: null as ArticleResponse | null,
      articles: [] as ArticleResponse[],
      page: 0,
      size: 20,
      totalPages: 1,
      loading: false,
      loadingMore: false,
      error: ''
    };
  },

  computed: {
    hasMore(): boolean {
      return this.page < this.totalPages - 1;
    },

    items(): any[] {
      const base = this.articles.map((a) => ({
        label: a.title || `Článek #${a.id}`,
        value: a
      }));

      // „Načíst další…“ na konci seznamu pokud jsou další stránky
      if (this.hasMore) {
        base.push({
          label: this.loadingMore ? 'Načítám…' : 'Načíst další…',
          type: 'item',
          disabled: this.loadingMore,
          // Nuxt UI SelectMenu umí onSelect na itemu
          onSelect: (e: Event) => {
            e.preventDefault();
            this.loadMore();
          }
        });
      }

      return base;
    }
  },

  watch: {
    // sync zvenku -> dovnitř
    modelValue: {
      immediate: true,
      handler(newVal: ArticleResponse | null) {
        this.innerValue = newVal;
      }
    },

    // sync ze selectu -> ven
    innerValue(newVal: ArticleResponse | null) {
      this.$emit('update:modelValue', newVal);
      this.$emit('selected', newVal);
    }
  },

  mounted() {
    this.fetchPage(0, true);
  },

  methods: {
    async fetchPage(page: number, reset = false) {
      if (reset) {
        this.articles = [];
        this.page = 0;
      }

      this.error = '';
      if (reset) {
        this.loading = true;
      } else {
        this.loadingMore = true;
      }

      const request: ListArticlesRequest = {
        pageable: {
          page,
          size: this.size,
          sort: ['publishedAt,desc']
        }
      };

      try {
        const res: PaginatedArticleResponse = await (this as any).$articlesApi.listArticles(
            request
        );

        const list = res.articles ?? [];

        if (reset) {
          this.articles = list;
        } else {
          this.articles = [...this.articles, ...list];
        }

        this.page = res.page ?? page;
        this.totalPages = res.totalPages ?? this.totalPages;

      } catch (e: any) {
        console.error(e);
        this.error = 'Nepodařilo se načíst články.';
        this.toast.add({
          title:
              e?.response?.data?.message ||
              e?.message ||
              'Nepodařilo se načíst články',
          color: 'error'
        });
      } finally {
        this.loading = false;
        this.loadingMore = false;
      }
    },

    loadMore() {
      if (!this.hasMore || this.loadingMore) return;
      this.fetchPage(this.page + 1);
    }
  }
};
</script>

<style scoped>
</style>
