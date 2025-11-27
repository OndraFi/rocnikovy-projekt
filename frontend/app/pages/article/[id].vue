<template>
  <div class="p-4 space-y-4 flex items-center justify-center">
    <!-- Loading skeleton -->
    <div v-if="loading" class="space-y-3">
      <USkeleton class="h-8 w-2/3" />
      <USkeleton class="h-4 w-full" />
      <USkeleton class="h-4 w-5/6" />
      <USkeleton class="h-4 w-4/6" />
    </div>

    <!-- Error -->
    <div v-else-if="error" class="text-red-600">
      {{ error }}
    </div>

    <!-- Článek -->
    <div v-else-if="article">
      <!-- Titulek / meta (volitelné, můžeš klidně zahodit) -->
      <h1 class="text-3xl font-semibold mb-2">
        {{ article.title }}
      </h1>

      <p class="text-sm text-gray-500 mb-4">
        <span v-if="article.publishedAt">
          Publikováno:
          {{ new Date(article.publishedAt).toLocaleDateString('cs-CZ') }}
        </span>
        <span v-if="article.author">
          · Autor:
          {{ article.author.fullName || article.author.username || ('#' + article.author.id) }}
        </span>
      </p>

      <!-- Vykreslení HTML obsahu -->
        <HtmlRender :html="articleHtml" />
      <!-- pokud používáš kebab-case: <html-render :html="articleHtml" /> -->
    </div>

    <!-- Nic nenašlo -->
    <div v-else class="text-gray-500">
      Článek nebyl nalezen.
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import type {
  ArticleDetailResponse,
  GetArticleRequest
} from '~~/api'

export default defineComponent({
  name: 'ArticleDetailPage',

  data() {
    return {
      article: null as ArticleDetailResponse | null,
      loading: false,
      error: '' as string
    }
  },

  setup() {
    definePageMeta({
      layout: 'default'
    })
  },

  computed: {
    articleHtml(): string {
      return this.article?.content || ''
    }
  },

  methods: {
    async fetchArticle() {
      const idParam = this.$route.params.id
      const id = Number(idParam)

      if (!id || Number.isNaN(id)) {
        this.error = 'Neplatné ID článku.'
        return
      }

      this.loading = true
      this.error = ''

      try {
        const request: GetArticleRequest = { id }
        const res = await this.$articlesApi.getArticle(request)
        this.article = res || null
      } catch (e: any) {
        console.error(e)
        this.error = e?.message || 'Nepodařilo se načíst článek.'
      } finally {
        this.loading = false
      }
    }
  },

  created() {
    this.fetchArticle()
  }
})
</script>
