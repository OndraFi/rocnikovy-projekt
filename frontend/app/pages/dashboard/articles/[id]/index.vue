<template>
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
        <div>
          <h3 class="font-semibold text-gray-500">Autor</h3>
          <p>{{ article.author?.fullName ?? '—' }}</p>
        </div>

        <div>
          <h3 class="font-semibold text-gray-500">Editor</h3>
          <p>{{ article.editor?.fullName ?? '—' }}</p>
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
        <div v-if="article.categories?.length" class="flex gap-2 flex-wrap">
          <UBadge v-for="cat in article.categories" :key="cat.id" color="gray">
            {{ cat.name }}
          </UBadge>
        </div>
        <p v-else class="text-gray-400">Žádné kategorie</p>
      </div>

      <!-- Content -->
      <div>
        <h2 class="text-xl font-semibold mb-2">Obsah článku</h2>

        <div v-if="!isEditing" class="prose dark:prose-invert max-w-none">
          <p v-if="!article.content" class="text-gray-400">Bez obsahu</p>
          <div v-else v-html="article.content"></div>
        </div>

        <UTextarea
            v-else
            v-model="form.content"
            placeholder="Obsah článku"
            :rows="8"
            class="w-full text-base"
        />
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
        <UButton color="blue" @click="onEdit">Editovat</UButton>
        <UButton color="red" @click="onDelete">Smazat</UButton>
        <UButton color="green" @click="onPublish">Publikovat</UButton>
      </div>
    </div>

    <!-- Not found -->
    <div v-else class="text-gray-500">
      Článek nebyl nalezen.
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import type { ArticleResponse, GetArticleRequest, UpdateArticleRequest } from '~~/api';

export default defineComponent({
  name: 'ArticleDetailPage',

  data() {
    return {
      article: null as ArticleResponse | null,
      loading: true,
      saving: false,
      error: '',
      isEditing: false,
      form: {
        title: '',
        content: ''
      }
    }
  },

  setup() {
    definePageMeta({ layout: 'dashboard' })
    const toast = useToast()
    return { toast }
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
      }
      this.isEditing = !this.isEditing
    },

    async saveArticle() {
      if (!this.article || !this.article.id) return
      if (!this.form.title.trim()) {
        this.toast.add({ title: 'Název je povinný', description: 'Článek musí mít název.', color: 'red' })
        return
      }

      this.saving = true
      try {
        const payload: UpdateArticleRequest = {
          title: this.form.title.trim(),
          content: this.form.content,
          articleState: this.article.articleState,
          publishedAt: this.article.publishedAt,
          categoryIds: this.article.categories?.map(c => c.id) || [],
          editorUsername: this.article.editor?.username
        }
        const request = { id: this.article.id, updateArticleRequest: payload }
        const updated = await this.$articlesApi.updateArticle(request)
        this.article = updated
        this.isEditing = false
        this.form.title = updated.title || ''
        this.form.content = updated.content || ''
        this.toast.add({ title: 'Článek upraven', description: 'Změny byly úspěšně uloženy.', color: 'green' })
      } catch (e: any) {
        console.error(e)
        this.toast.add({ title: 'Chyba při ukládání', description: e?.message || 'Nepodařilo se upravit článek.', color: 'red' })
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
        this.toast.add({ title: 'Článek smazán', color: 'green' })
        this.$router.push('/dashboard/articles')
      } catch (e: any) {
        console.error(e)
        this.toast.add({ title: 'Chyba při mazání', description: e?.message || 'Nepodařilo se smazat článek.', color: 'red' })
      }
    },
    async onPublish() {
      if (!this.article?.id) return
      try {
        const payload: UpdateArticleRequest = {
          title: this.article.title || '',
          content: this.article.content || '',
          articleState: 'PUBLISHED',
          publishedAt: this.article.publishedAt || new Date().toISOString(),
          categoryIds: this.article.categories?.map(c => c.id) || [],
          editorUsername: this.article.editor?.username
        }
        const request = { id: this.article.id, updateArticleRequest: payload }
        const updated = await this.$articlesApi.updateArticle(request)
        this.article = updated
        this.toast.add({ title: 'Článek publikován', color: 'green' })
      } catch (e: any) {
        console.error(e)
        this.toast.add({ title: 'Chyba při publikaci', description: e?.message || 'Nepodařilo se publikovat článek.', color: 'red' })
      }
    }
  },

  created() {
    this.fetchArticle()
  }
})
</script>
