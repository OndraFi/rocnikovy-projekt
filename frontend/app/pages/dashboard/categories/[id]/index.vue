<template>
  <div class="p-4 space-y-4">

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

    <!-- Detail kategorie / edit mode -->
    <div
        v-else-if="category"
        class="rounded-lg border border-gray-200 bg-white p-4 space-y-4 shadow-sm"
    >
      <!-- Header + edit button -->
      <div class="flex items-start justify-between gap-2">
        <div class="flex-1">
          <!-- View mode title -->
          <h1 v-if="!isEditing" class="text-2xl font-semibold">
            {{ category.name }}
          </h1>

          <!-- Edit mode title -->
          <UInput
              v-else
              v-model="form.name"
              placeholder="Název kategorie"
              class="w-full font-semibold"
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

      <!-- Popis -->
      <div>
        <!-- View mode description -->
        <p v-if="!isEditing" class="text-gray-600">
          {{ category.description || 'Bez popisu.' }}
        </p>

        <!-- Edit mode description -->
        <UTextarea
            v-else
            v-model="form.description"
            placeholder="Popis kategorie"
            :rows="4"
            class="w-full text-base"
            :ui="{
              base: 'text-base text-gray-600',
              root: 'py-0'
            }"
        />
      </div>

      <!-- ID -->
      <div class="text-xs text-gray-400">
        ID: {{ category.id }}
      </div>

      <!-- Save button in edit mode -->
      <div v-if="isEditing" class="flex justify-end pt-2">
        <UButton
            icon="i-heroicons-check"
            color="primary"
            :loading="saving"
            @click="saveCategory"
        >
          Uložit změny
        </UButton>
      </div>
    </div>

    <!-- Nic nenašlo -->
    <div v-else class="text-gray-500">
      Kategorie nebyla nalezena.
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import type {
  CategoryResponse,
  GetCategoryRequest,
  UpdateCategoryOperationRequest,
  UpdateCategoryRequest
} from '~~/api'

export default defineComponent({
  name: 'CategoryDetailPage',

  data() {
    return {
      category: null as CategoryResponse | null,
      loading: false,
      saving: false,
      error: '' as string,

      isEditing: false,
      form: {
        name: '' as string,
        description: '' as string | undefined
      }
    }
  },

  setup() {
    definePageMeta({
      layout: 'dashboard'
    })

    // Nuxt UI toast (useToast) – bude dostupný jako this.toast
    const toast = useToast()

    return {toast}
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
        const request: GetCategoryRequest = {id}

        const res = await this.$categoriesApi.getCategory(request)
        this.category = res || null

        // Naplníme form z načtených dat
        if (this.category) {
          this.form.name = this.category.name || ''
          this.form.description = this.category.description || ''
        }
      } catch (e: any) {
        console.error(e)
        this.error = e?.message || 'Nepodařilo se načíst kategorii.'
      } finally {
        this.loading = false
      }
    },

    toggleEdit() {
      // když vypínám edit, resetnu form na aktuální hodnoty
      if (this.isEditing && this.category) {
        this.form.name = this.category.name || ''
        this.form.description = this.category.description || ''
      }
      this.isEditing = !this.isEditing
    },

    async saveCategory() {
      if (!this.category || !this.category.id) return

      if (!this.form.name.trim()) {
        this.toast.add({
          title: 'Název je povinný',
          description: 'Kategorie musí mít název.',
          color: 'red'
        })
        return
      }

      this.saving = true

      try {
        const payload: UpdateCategoryRequest = {
          name: this.form.name.trim(),
          description: this.form.description?.trim() || undefined
        }

        const request: UpdateCategoryOperationRequest = {
          id: this.category.id,
          updateCategoryRequest: payload
        }

        const updated = await this.$categoriesApi.updateCategory(request)

        this.category = updated
        this.isEditing = false

        // aktualizovat form podle odpovědi
        if (this.category) {
          this.form.name = this.category.name || ''
          this.form.description = this.category.description || ''
        }

        this.toast.add({
          title: 'Kategorie upravena',
          description: 'Změny byly úspěšně uloženy.',
          color: 'green'
        })
      } catch (e: any) {
        console.error(e)
        this.toast.add({
          title: 'Chyba při ukládání',
          description: e?.message || 'Nepodařilo se upravit kategorii.',
          color: 'red'
        })
      } finally {
        this.saving = false
      }
    }
  },

  created() {
    this.fetchCategory()
  }
})
</script>
