<template>
  <section class="py-12 px-4 sm:px-6 lg:px-8 bg-gray-50 min-h-screen">
    <div class="max-w-4xl mx-auto space-y-8">

      <header class="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4 border-b border-gray-200 pb-6">
        <div class="space-y-1">
          <h1 class="text-3xl font-bold text-gray-900 tracking-tight">
            Články v kategorii
          </h1>
          <p class="text-sm text-gray-500">
            Procházet nejnovější příspěvky a aktuality
          </p>
        </div>

        <div class="text-sm font-medium text-gray-500 bg-white px-3 py-1 rounded-full border border-gray-200 shadow-sm">
          <span v-if="!loading && !error">
            {{ totalElements }} článků • Strana {{ page + 1 }} z {{ totalPages }}
          </span>
          <span v-else>Načítání...</span>
        </div>
      </header>

      <div v-if="loading" class="space-y-4">
        <div v-for="i in 3" :key="i" class="flex gap-4 p-4 bg-white rounded-2xl border border-gray-100">
          <USkeleton class="h-24 w-24 rounded-xl flex-shrink-0" />
          <div class="space-y-3 w-full py-1">
            <USkeleton class="h-4 w-20" />
            <USkeleton class="h-6 w-3/4" />
            <USkeleton class="h-4 w-1/4" />
          </div>
        </div>
      </div>

      <div v-else-if="error" class="rounded-xl bg-red-50 p-4 border border-red-100 text-center">
        <p class="text-sm text-red-600 font-medium">{{ error }}</p>
      </div>

      <div v-else-if="articles.length === 0" class="text-center py-12 bg-white rounded-2xl border border-dashed border-gray-300">
        <p class="text-gray-500">V této kategorii zatím nejsou žádné články.</p>
      </div>

      <div v-else class="space-y-4">
        <article
            v-for="article in articles"
            :key="article.id"
            class="group relative bg-white rounded-2xl border border-gray-100 p-5 hover:shadow-xl hover:-translate-y-1 transition-all duration-300 ease-in-out"
        >
          <NuxtLink :to="`/article/${article.id}`" class="flex flex-col sm:flex-row gap-6">

            <div class="relative w-full sm:w-32 sm:h-32 h-48 flex-shrink-0 flex items-center justify-center rounded-xl bg-gradient-to-br from-blue-50 to-indigo-50 border border-blue-100/50 overflow-hidden group-hover:border-blue-200 transition-colors">
              <span class="text-4xl font-bold text-blue-200 select-none group-hover:scale-110 transition-transform duration-500">
                {{ article?.title?.charAt(0) || '?' }}
              </span>
            </div>

            <div class="flex-1 flex flex-col justify-center min-w-0">
              <div class="flex items-center flex-wrap gap-3 mb-2">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-slate-100 text-slate-700 border border-slate-200">
                  {{ mainCategory(article) }}
                </span>

                <span class="flex items-center text-xs text-gray-400">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-3.5 w-3.5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  {{ formatDate(article?.publishedAt?.toString()) }}
                </span>
              </div>

              <h3 class="text-xl font-bold text-gray-900 group-hover:text-blue-600 transition-colors line-clamp-2 leading-snug mb-2">
                {{ article.title }}
              </h3>

              <p class="text-sm text-gray-500 truncate">
                Autor: <span class="text-gray-700 font-medium">{{ article.author?.fullName || article.author?.username || 'Redakce' }}</span>
              </p>
            </div>

            <div class="hidden sm:flex flex-col justify-center text-gray-300 group-hover:text-blue-500 transition-colors pr-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 group-hover:translate-x-1 transition-transform" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </div>

          </NuxtLink>
        </article>
      </div>

      <div v-if="!loading && !error" class="flex items-center justify-center gap-2 pt-6 border-t border-gray-200">
        <button
            class="flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            :disabled="page <= 0"
            @click="prevPage"
        >
          ← Předchozí
        </button>

        <span class="text-sm font-medium text-gray-500 px-4">
          {{ page + 1 }} / {{ totalPages }}
        </span>

        <button
            class="flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            :disabled="page >= totalPages - 1"
            @click="nextPage"
        >
          Další →
        </button>
      </div>
    </div>
  </section>
</template>

<script lang="ts">
// Script zůstává beze změn, jak bylo požadováno.
import type {
  ArticleResponse,
  ListArticlesRequest,
  PaginatedArticleResponse
} from '~~/api'

export default {
  name: '[id]',

  setup() {
    // default layout => nic nenastavujeme
  },

  data() {
    return {
      categoryId: null as number | null,
      articles: [] as ArticleResponse[],
      loading: false,
      error: '',
      page: 0,
      size: 20,
      totalPages: 1,
      totalElements: 0
    }
  },

  methods: {
    mainCategory(article: ArticleResponse): string {
      const anyArticle = article as any;
      if (Array.isArray(anyArticle.categories) && anyArticle.categories.length > 0) {
        return anyArticle.categories[0]?.value.name || 'Článek';
      }
      return 'Článek';
    },
    formatDate(date: any) {
      const d = new Date(date)
      if (Number.isNaN(d.getTime())) return ''
      return d.toLocaleString('cs-CZ')
    },

    async fetchArticles() {
      const rawId = Number(this.$route.params.id)
      if (!rawId || Number.isNaN(rawId)) {
        this.error = 'Neplatné ID kategorie.'
        return
      }

      this.categoryId = rawId
      this.loading = true
      this.error = ''

      const req: ListArticlesRequest = {
        pageable: {
          page: this.page,
          size: this.size,
          sort: ['publishedAt,desc']
        },
        categoryIds: [this.categoryId]
      }

      try {
        const res: PaginatedArticleResponse = await (this as any).$articlesApi.listArticles(req)
        this.articles = res.articles || []
        this.totalPages = res.totalPages ?? 1
        this.totalElements = res.totalElements ?? this.articles.length
      } catch (e: any) {
        console.error(e)
        this.error = e?.message || 'Nepodařilo se načíst články.'
      } finally {
        this.loading = false
      }
    },

    nextPage() {
      if (this.page >= this.totalPages - 1) return
      this.page++
      this.fetchArticles()
    },

    prevPage() {
      if (this.page <= 0) return
      this.page--
      this.fetchArticles()
    }
  },

  watch: {
    '$route.params.id': {
      immediate: true,
      handler() {
        this.page = 0
        this.fetchArticles()
      }
    }
  }
}
</script>

<style scoped>
/* Styly jsou řešeny přes Tailwind, zde není nic potřeba */
</style>