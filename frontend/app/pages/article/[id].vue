<template>
  <div class="p-4 min-h-screen bg-gray-50">
    <!-- Loading skeleton -->
    <div v-if="loading" class="flex items-center justify-center">
      <div class="space-y-3 max-w-3xl w-full">
        <USkeleton class="h-8 w-2/3" />
        <USkeleton class="h-4 w-full" />
        <USkeleton class="h-4 w-5/6" />
        <USkeleton class="h-4 w-4/6" />
      </div>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="text-red-600 max-w-3xl mx-auto">
      {{ error }}
    </div>

    <!-- Článek -->
    <div
        v-else-if="article"
        class="max-w-5xl mx-auto flex gap-8"
    >
      <!-- Pravý action bar (na desktopu) -->
      <aside class="hidden lg:flex flex-col gap-3 pt-12 sticky top-24">
        <UButton
            icon="i-heroicons-share"
            variant="ghost"
            size="sm"
            @click="copyLink"
            :ui="{ rounded: 'rounded-full' }"
        >
          Sdílet
        </UButton>

        <UTooltip text="Vytisknout">
          <UButton
              icon="i-heroicons-printer"
              variant="ghost"
              size="sm"
              @click="printArticle"
              :ui="{ rounded: 'rounded-full' }"
          />
        </UTooltip>

        <div class="h-px bg-gray-200 my-2" />

        <UTooltip text="Sdílet na Facebooku">
          <UButton
              icon="i-simple-icons-facebook"
              variant="ghost"
              size="sm"
              @click="shareOnFacebook"
              :ui="{ rounded: 'rounded-full' }"
          />
        </UTooltip>

        <UTooltip text="Sdílet na X (Twitter)">
          <UButton
              icon="i-simple-icons-x"
              variant="ghost"
              size="sm"
              @click="shareOnTwitter"
              :ui="{ rounded: 'rounded-full' }"
          />
        </UTooltip>
      </aside>

      <!-- Hlavní obsah článku -->
      <main class="flex-1 bg-white rounded-lg shadow-sm p-6">
        <!-- Titulek / meta -->
        <header class="border-b border-gray-100 pb-4 mb-6">
          <h1 class="text-3xl font-semibold mb-2">
            {{ article.title }}
          </h1>

          <p class="text-sm text-gray-500 flex flex-wrap items-center gap-x-2 gap-y-1">
            <span v-if="article.publishedAt">
              Publikováno:
              {{ new Date(article.publishedAt).toLocaleDateString('cs-CZ') }}
            </span>
            <span v-if="article.author">
              · Autor:
              {{ article.author.fullName || article.author.username || ('#' + article.author.id) }}
            </span>
          </p>
        </header>

        <!-- HTML obsah článku -->
        <HtmlRender :html="articleHtml" />
      </main>
    </div>

    <!-- Nic nenašlo -->
    <div v-else class="text-gray-500 max-w-3xl mx-auto">
      Článek nebyl nalezen.
    </div>

    <!-- Spodní action bar na mobilu -->
    <div
        v-if="article && !loading && !error"
        class="fixed inset-x-0 bottom-0 bg-white border-t border-gray-200 px-4 py-2 flex items-center justify-between lg:hidden"
    >
      <div class="text-xs text-gray-500 truncate">
        Sdílet článek
      </div>
      <div class="flex items-center gap-2">
        <UButton
            icon="i-heroicons-share"
            variant="ghost"
            size="xs"
            @click="copyLink"
        />
        <UButton
            icon="i-heroicons-printer"
            variant="ghost"
            size="xs"
            @click="printArticle"
        />
        <UButton
            icon="i-simple-icons-facebook"
            variant="ghost"
            size="xs"
            @click="shareOnFacebook"
        />
        <UButton
            icon="i-simple-icons-x"
            variant="ghost"
            size="xs"
            @click="shareOnTwitter"
        />
      </div>
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

    const toast = useToast()

    return { toast }
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
        console.log(res);
        this.article = res || null
        console.log(this.article)
      } catch (e: any) {
        console.error(e)
        this.error = e?.message || 'Nepodařilo se načíst článek.'
      } finally {
        this.loading = false
      }
    },

    getCurrentUrl(): string {
      if (typeof window === 'undefined') return ''
      return window.location.href
    },
    copyLink() {
      const url = this.getCurrentUrl()
      if (!url) return

      // Pokud zařízení podporuje nativní sdílení
      if (navigator.share) {
        navigator
            .share({
              title: this.article?.title ?? 'Sdílet článek',
              text: 'Podívej se na tento článek:',
              url
            })
            .then(() => {
              this.toast?.add({
                title: 'Sdíleno',
                description: 'Článek byl úspěšně sdílen.',
                color: 'primary'
              })
            })
            .catch(() => {
              this.toast?.add({
                title: 'Sdílení zrušeno',
                description: 'Sdílení článku bylo přerušeno.',
                color: 'error'
              })
            })

        return
      }

      // Fallback — kopírování do schránky
      if (navigator.clipboard?.writeText) {
        navigator.clipboard
            .writeText(url)
            .then(() => {
              this.toast?.add({
                title: 'Odkaz zkopírován',
                description: 'Odkaz na článek je ve schránce.',
                color: 'primary'
              })
            })
            .catch(() => {
              this.toast?.add({
                title: 'Chyba při kopírování',
                description: 'Odkaz se nepodařilo zkopírovat.',
                color: 'error'
              })
            })
      }
    },

    printArticle() {
      if (typeof window === 'undefined') return
      window.print()
    },

    shareOnFacebook() {
      const url = encodeURIComponent(this.getCurrentUrl())
      if (!url) return
      window.open(
          `https://www.facebook.com/sharer/sharer.php?u=${url}`,
          '_blank',
          'noopener,noreferrer'
      )
    },

    shareOnTwitter() {
      const url = encodeURIComponent(this.getCurrentUrl())
      const text = encodeURIComponent(this.article?.title || '')
      if (!url) return
      window.open(
          `https://twitter.com/intent/tweet?url=${url}&text=${text}`,
          '_blank',
          'noopener,noreferrer'
      )
    }
  },

  created() {
    this.fetchArticle()
  }
})
</script>
