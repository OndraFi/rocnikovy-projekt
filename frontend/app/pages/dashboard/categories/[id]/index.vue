<template>
  <div class="p-4 space-y-4">

    <!-- Loading -->
    <div v-if="loading" class="space-y-3">
      <USkeleton class="h-7 w-48" />
      <USkeleton class="h-4 w-full" />
      <USkeleton class="h-4 w-2/3" />
      <USkeleton class="h-3 w-20" />
    </div>

    <!-- Error -->
    <div v-else-if="error" class="text-red-600">
      {{ error }}
    </div>

    <!-- Detail kategorie -->
    <div
        v-else-if="category"
        class="rounded-lg border border-gray-200 bg-white p-4 space-y-3 shadow-sm"
    >
      <h1 class="text-2xl font-semibold">
        {{ category.name }}
      </h1>

      <p class="text-gray-600">
        {{ category.description || 'Bez popisu.' }}
      </p>

      <div class="text-xs text-gray-400">
        ID: {{ category.id }}
      </div>
    </div>

    <!-- Nic nenašlo -->
    <div v-else class="text-gray-500">
      Kategorie nebyla nalezena.
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import type { CategoryResponse, GetCategoryRequest } from '~~/api'

export default defineComponent({
  name: 'CategoryDetailPage',

  data() {
    return {
      category: null as CategoryResponse | null,
      loading: false,
      error: '' as string
    }
  },

  setup() {
    // layout dashboard, stejně jako u listu
    definePageMeta({
      layout: 'dashboard'
    })
  },

  methods: {
    async fetchCategory() {
      const idParam = this.$route.params.id
      const id = Number(idParam)

      if (!id || Number.isNaN(id)) {
        this.error = 'Neplatné ID kategorie.'
        return
      }

      this.loading = true
      this.error = ''

      try {
        const request: GetCategoryRequest = { id }

        const res = await this.$categoriesApi.getCategory(request)
        this.category = res || null
      } catch (e: any) {
        console.error(e)
        this.error = e?.message || 'Nepodařilo se načíst kategorii.'
      } finally {
        this.loading = false
      }
    }
  },

  created() {
    this.fetchCategory()
  }
})
</script>
