<template>
  <nav class="sticky top-0 bg-white/95 backdrop-blur-md z-50 border-b border-gray-200">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Horní řádek -->
      <div class="flex justify-between items-center h-16">
        <!-- Logo -->
        <NuxtLink to="/" class="flex items-center gap-2">
          <UIcon name="lucide:newspaper" class="w-8 h-8 text-blue-600" />
          <span class="font-serif text-2xl">Deník Online</span>
        </NuxtLink>

        <!-- Desktop menu -->
        <div class="hidden lg:flex items-center gap-8">
          <NuxtLink
              v-for="cat in categories"
              :key="cat.id"
              :to="cat.id != null ? `/category/${cat.id}` : '#'"
              class="text-gray-700 hover:text-blue-600 transition-colors"
          >
            {{ cat.name || 'Kategorie' }}
          </NuxtLink>

          <NuxtLink
              v-if="authStore.isLoggedIn()"
              to="/dashboard"
              class="px-2 py-2 rounded-md text-gray-700 bg-gray-50 hover:bg-gray-100 hover:text-blue-600 transition-colors"
          >
            Dashboard
          </NuxtLink>
        </div>

        <!-- Pravá strana -->
        <div class="flex items-center gap-4">
          <!-- Search -->
          <UButton
              variant="ghost"
              color="primary"
              class="p-2 rounded-lg hover:bg-gray-100 transition-colors"
              @click="onSearch"
          >
            <UIcon name="lucide:search" class="w-5 h-5 text-gray-600" />
          </UButton>

          <!-- Hamburger (mobile) -->
          <UButton
              variant="ghost"
              color="primary"
              class="lg:hidden p-2 rounded-lg hover:bg-gray-100 transition-colors"
              @click="toggleMobileMenu"
          >
            <UIcon
                :name="isMobileMenuOpen ? 'lucide:x' : 'lucide:menu'"
                class="w-5 h-5 text-gray-600"
            />
          </UButton>
        </div>
      </div>

      <!-- Mobile menu -->
      <div
          v-if="isMobileMenuOpen"
          class="lg:hidden pb-4 border-t border-gray-200"
      >
        <div class="flex flex-col gap-1 pt-3">
          <NuxtLink
              v-for="cat in categories"
              :key="cat.id"
              :to="cat.id != null ? `/category/${cat.id}` : '#'"
              class="px-2 py-2 rounded-md text-gray-700 hover:bg-gray-100 hover:text-blue-600 transition-colors"
              @click="isMobileMenuOpen = false"
          >
            {{ cat.name || 'Kategorie' }}
          </NuxtLink>

          <NuxtLink
              v-if="authStore.isLoggedIn()"
              to="/dashboard"
              class="px-2 py-2 rounded-md text-gray-700 hover:bg-gray-100 hover:text-blue-600 transition-colors"
              @click="isMobileMenuOpen = false"
          >
            Dashboard
          </NuxtLink>
        </div>
      </div>
    </div>
  </nav>
</template>

<script lang="ts">
import type {
  CategoryResponse,
  ListCategoriesRequest,
  PaginatedCategoryResponse
} from '~~/api'

export default {
  name: 'Navigation',

  data() {
    return {
      authStore: useAuthStore(),
      isMobileMenuOpen: false as boolean,
      categories: [] as CategoryResponse[]
    }
  },

  methods: {
    onSearch() {
      console.log('Search click')
    },

    toggleMobileMenu() {
      this.isMobileMenuOpen = !this.isMobileMenuOpen
    },

    async fetchCategories() {
      const request: ListCategoriesRequest = {
        pageable: {
          page: 0,
          size: 5,
          sort: ['name,asc']
        }
      }

      try {
        const res: PaginatedCategoryResponse = await (this as any).$categoriesApi.listCategories(request)
        this.categories = res.categories || []
      } catch (e: any) {
        console.error('Failed to load categories for nav:', e)
        this.categories = []
      }
    }
  },

  mounted() {
    this.fetchCategories()
  }
}
</script>

<style scoped>
</style>
