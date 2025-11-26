<template>
  <footer class="bg-gray-900 text-gray-300 py-16 px-4 sm:px-6 lg:px-8">
    <div class="max-w-7xl mx-auto">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-12 mb-12">
        <!-- Brand -->
        <div>
          <div class="flex items-center gap-2 mb-4">
            <UIcon name="lucide:newspaper" class="w-8 h-8 text-blue-500" />
            <span class="font-serif text-2xl text-white">Deník Online</span>
          </div>
          <p class="text-gray-400 mb-6">
            Váš denní zdroj pro nejdůležitější zprávy ze světa, ekonomiky, sportu a kultury.
          </p>
          <div class="flex gap-4">
            <a
                href="#"
                class="w-10 h-10 bg-gray-800 rounded-lg flex items-center justify-center hover:bg-blue-600 transition-colors"
            >
              <UIcon name="lucide:facebook" class="w-5 h-5" />
            </a>
            <a
                href="#"
                class="w-10 h-10 bg-gray-800 rounded-lg flex items-center justify-center hover:bg-blue-600 transition-colors"
            >
              <UIcon name="lucide:twitter" class="w-5 h-5" />
            </a>
            <a
                href="#"
                class="w-10 h-10 bg-gray-800 rounded-lg flex items-center justify-center hover:bg-blue-600 transition-colors"
            >
              <UIcon name="lucide:instagram" class="w-5 h-5" />
            </a>
            <a
                href="#"
                class="w-10 h-10 bg-gray-800 rounded-lg flex items-center justify-center hover:bg-blue-600 transition-colors"
            >
              <UIcon name="lucide:youtube" class="w-5 h-5" />
            </a>
          </div>
        </div>

        <!-- Rubriky -->
        <div>
          <h3 class="text-white mb-4">Rubriky</h3>
          <ul class="space-y-3">
            <li>
              <a href="#" class="hover:text-white transition-colors">Zprávy</a>
            </li>
            <li>
              <a href="#" class="hover:text-white transition-colors">Ekonomika</a>
            </li>
            <li>
              <a href="#" class="hover:text-white transition-colors">Sport</a>
            </li>
            <li>
              <a href="#" class="hover:text-white transition-colors">Kultura</a>
            </li>
            <li>
              <a href="#" class="hover:text-white transition-colors">Technologie</a>
            </li>
          </ul>
        </div>

        <!-- O nás -->
        <div>
          <h3 class="text-white mb-4">O nás</h3>
          <ul class="space-y-3">
            <li>
              <a href="#" class="hover:text-white transition-colors">Redakce</a>
            </li>
            <li>
              <a href="#" class="hover:text-white transition-colors">Kontakt</a>
            </li>
            <li>
              <a href="#" class="hover:text-white transition-colors">Inzerce</a>
            </li>
            <li>
              <a href="#" class="hover:text-white transition-colors">Kariéra</a>
            </li>
            <li>
              <a href="#" class="hover:text-white transition-colors">Archiv</a>
            </li>
          </ul>
        </div>

        <!-- Právní -->
        <div>
          <h3 class="text-white mb-4">Právní informace</h3>
          <ul class="space-y-3">
            <li>
              <a href="#" class="hover:text-white transition-colors">Podmínky použití</a>
            </li>
            <li>
              <a href="#" class="hover:text-white transition-colors">Ochrana soukromí</a>
            </li>
            <li>
              <a href="#" class="hover:text-white transition-colors">Cookies</a>
            </li>
            <li>
              <a href="#" class="hover:text-white transition-colors">RSS</a>
            </li>
          </ul>
        </div>
      </div>

      <!-- Bottom -->
      <div class="pt-8 border-t border-gray-800 text-center text-gray-400">
        <p>© 2025 Deník Online. Všechna práva vyhrazena.</p>
      </div>
    </div>
  </footer>
</template>

<script lang="ts">
export default {
  name: 'Footer'
}
</script>

<style scoped>
</style>
