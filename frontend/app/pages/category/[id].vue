<template>
  <section class="py-10 px-4 sm:px-6 lg:px-8">
    <div class="max-w-5xl mx-auto space-y-6">
      <!-- Header -->
      <header class="space-y-1">
        <h1 class="text-2xl font-semibold">
          Články v kategorii
        </h1>
        <p class="text-sm text-gray-500">
          <span v-if="!loading && !error">
            {{ totalElements }} článků • stránka {{ page + 1 }} / {{ totalPages }}
          </span>
        </p>
      </header>

      <!-- Loading -->
      <div v-if="loading" class="space-y-3">
        <USkeleton class="h-12 w-full" />
        <USkeleton class="h-12 w-full" />
        <USkeleton class="h-12 w-full" />
      </div>

      <!-- Error -->
      <div v-else-if="error" class="text-sm text-red-600">
        {{ error }}
      </div>

      <!-- Empty -->
      <div v-else-if="articles.length === 0" class="text-sm text-gray-500">
        V této kategorii nejsou žádné články.
      </div>

      <!-- List -->
<!--      <div v-else class="grid gap-3">-->
<!--        <NuxtLink-->
<!--            v-for="a in articles"-->
<!--            :key="a.id"-->
<!--            :to="a.id ? `/article/${a.id}` : undefined"-->
<!--            class="block rounded-xl border border-gray-200 bg-white p-4 hover:shadow-sm transition"-->
<!--        >-->
<!--          <div class="flex items-start justify-between gap-4">-->
<!--            <div class="min-w-0">-->
<!--              <div class="flex items-center gap-2 mb-1">-->
<!--                <UBadge v-if="a.articleState" color="gray" class="uppercase">-->
<!--                  {{ a.articleState }}-->
<!--                </UBadge>-->
<!--                <span v-if="a.publishedAt" class="text-xs text-gray-500">-->
<!--                  {{ formatDate(a.publishedAt) }}-->
<!--                </span>-->
<!--              </div>-->

<!--              <h2 class="text-lg font-semibold truncate">-->
<!--                {{ a.title || 'Bez názvu' }}-->
<!--              </h2>-->

<!--              <p class="mt-1 text-sm text-gray-600">-->
<!--                Autor: {{ a.author?.fullName || a.author?.username || '—' }}-->
<!--              </p>-->
<!--            </div>-->

<!--            <span class="text-xs text-gray-400 whitespace-nowrap">-->
<!--              Otevřít →-->
<!--            </span>-->
<!--          </div>-->
<!--        </NuxtLink>-->
<!--      </div>-->

      <div
          v-for="article in articles"
          :key="article.id"
          class="group cursor-pointer bg-white rounded-xl overflow-hidden border border-gray-200 hover:shadow-xl transition-all"
      >
        <NuxtLink :to="`/article/${article.id}`" class="flex gap-4">
          <!-- místo obrázku jen „avatar“ s prvním písmenem -->
          <div class="relative w-16 h-16 sm:w-22 sm:h-22 flex-shrink-0 flex items-center justify-center rounded-lg bg-gradient-to-br from-slate-200 to-slate-100">
                  <span class="text-xl font-semibold text-slate-700">
                    {{ article?.title?.charAt(0) }}
                  </span>
          </div>

          <div class="flex-1 py-4 pr-4">
            <div class="flex items-center gap-2 mb-2">
                    <span class="px-2 py-1 bg-slate-900 text-white rounded text-xs">
                      {{ mainCategory(article) }}
                    </span>
              <span class="text-xs text-gray-500">
                      {{ formatDate(article?.publishedAt?.toString()) }}
                    </span>
            </div>
            <h3 class="text-lg leading-tight group-hover:text-blue-600 transition-colors line-clamp-2">
              {{ article.title }}
            </h3>
          </div>
        </NuxtLink>
      </div>

      <!-- Pagination -->
      <div v-if="!loading && !error" class="flex items-center justify-between pt-2">
        <button
            class="text-sm px-3 py-2 rounded-lg border border-gray-200 disabled:opacity-50"
            :disabled="page <= 0"
            @click="prevPage"
        >
          Předchozí
        </button>

        <span class="text-sm text-gray-500">
          {{ page + 1 }} / {{ totalPages }}
        </span>

        <button
            class="text-sm px-3 py-2 rounded-lg border border-gray-200 disabled:opacity-50"
            :disabled="page >= totalPages - 1"
            @click="nextPage"
        >
          Další
        </button>
      </div>
    </div>
  </section>
</template>

<script lang="ts">
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
</style>
