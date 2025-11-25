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
          <NuxtLink to="#" class="text-gray-700 hover:text-blue-600 transition-colors">Zprávy</NuxtLink>
          <NuxtLink to="#" class="text-gray-700 hover:text-blue-600 transition-colors">Ekonomika</NuxtLink>
          <NuxtLink to="#" class="text-gray-700 hover:text-blue-600 transition-colors">Sport</NuxtLink>
          <NuxtLink to="#" class="text-gray-700 hover:text-blue-600 transition-colors">Kultura</NuxtLink>
          <NuxtLink to="#" class="text-gray-700 hover:text-blue-600 transition-colors">Technologie</NuxtLink>
          <NuxtLink to="#" class="text-gray-700 hover:text-blue-600 transition-colors">Názory</NuxtLink>
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
          <NuxtLink to="#" class="px-2 py-2 rounded-md text-gray-700 hover:bg-gray-100 hover:text-blue-600 transition-colors">
            Zprávy
          </NuxtLink>
          <NuxtLink to="#" class="px-2 py-2 rounded-md text-gray-700 hover:bg-gray-100 hover:text-blue-600 transition-colors">
            Ekonomika
          </NuxtLink>
          <NuxtLink to="#" class="px-2 py-2 rounded-md text-gray-700 hover:bg-gray-100 hover:text-blue-600 transition-colors">
            Sport
          </NuxtLink>
          <NuxtLink to="#" class="px-2 py-2 rounded-md text-gray-700 hover:bg-gray-100 hover:text-blue-600 transition-colors">
            Kultura
          </NuxtLink>
          <NuxtLink to="#" class="px-2 py-2 rounded-md text-gray-700 hover:bg-gray-100 hover:text-blue-600 transition-colors">
            Technologie
          </NuxtLink>
          <NuxtLink to="#" class="px-2 py-2 rounded-md text-gray-700 hover:bg-gray-100 hover:text-blue-600 transition-colors">
            Názory
          </NuxtLink>
        </div>
      </div>
    </div>
  </nav>
</template>

<script lang="ts">
export default {
  name: 'Navigation',

  data() {
    return {
      isMobileMenuOpen: false as boolean
    }
  },

  methods: {
    onSearch() {
      console.log('Search click')
    },
    toggleMobileMenu() {
      this.isMobileMenuOpen = !this.isMobileMenuOpen
    }
  }
}
</script>

<style scoped>
</style>
