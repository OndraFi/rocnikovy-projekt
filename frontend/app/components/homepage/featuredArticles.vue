<template>
  <section class="py-16 px-4 sm:px-6 lg:px-8 bg-white">
    <div class="max-w-7xl mx-auto">
      <div class="flex items-center justify-between mb-8">
        <h2 class="text-3xl">Doporučujeme</h2>
        <a href="#" class="text-blue-600 hover:text-blue-700 transition-colors">
          Zobrazit vše →
        </a>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        <!-- Article 1 -->
        <article
            class="group cursor-pointer bg-white rounded-xl overflow-hidden border border-gray-200 hover:shadow-2xl transition-all"
        >
          <div class="relative h-48 overflow-hidden">
            <img
                src="https://images.unsplash.com/photo-1709715357520-5e1047a2b691?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080"
                alt="Ekonomika roste rychleji než se očekávalo"
                class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
            />
            <div class="absolute top-4 left-4">
              <span class="px-3 py-1 bg-blue-600 text-white rounded-full text-sm">
                Ekonomika
              </span>
            </div>
          </div>

          <div class="p-6">
            <h3 class="text-xl mb-3 group-hover:text-blue-600 transition-colors">
              Ekonomika roste rychleji než se očekávalo
            </h3>
            <p class="text-gray-600 mb-4 leading-relaxed">
              Analytici překvapeni pozitivními čísly za poslední čtvrtletí. HDP vzrostl o 3,2 procenta.
            </p>
            <div class="flex items-center gap-4 text-sm text-gray-500">
              <div class="flex items-center gap-1">
                <UIcon name="lucide:user" class="w-4 h-4" />
                <span>Jan Novák</span>
              </div>
              <div class="flex items-center gap-1">
                <UIcon name="lucide:clock" class="w-4 h-4" />
                <span>15 min</span>
              </div>
            </div>
          </div>
        </article>

        <!-- Article 2 -->
        <article
            class="group cursor-pointer bg-white rounded-xl overflow-hidden border border-gray-200 hover:shadow-2xl transition-all"
        >
          <div class="relative h-48 overflow-hidden">
            <img
                src="https://images.unsplash.com/photo-1493134799591-2c9eed26201a?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080"
                alt="Revolucionární objev v medicíně"
                class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
            />
            <div class="absolute top-4 left-4">
              <span class="px-3 py-1 bg-blue-600 text-white rounded-full text-sm">
                Věda
              </span>
            </div>
          </div>

          <div class="p-6">
            <h3 class="text-xl mb-3 group-hover:text-blue-600 transition-colors">
              Revolucionární objev v medicíně
            </h3>
            <p class="text-gray-600 mb-4 leading-relaxed">
              Vědci představili průlomovou metodu léčby, která může změnit životy milionů pacientů.
            </p>
            <div class="flex items-center gap-4 text-sm text-gray-500">
              <div class="flex items-center gap-1">
                <UIcon name="lucide:user" class="w-4 h-4" />
                <span>Marie Svobodová</span>
              </div>
              <div class="flex items-center gap-1">
                <UIcon name="lucide:clock" class="w-4 h-4" />
                <span>30 min</span>
              </div>
            </div>
          </div>
        </article>

        <!-- Article 3 -->
        <article
            class="group cursor-pointer bg-white rounded-xl overflow-hidden border border-gray-200 hover:shadow-2xl transition-all"
        >
          <div class="relative h-48 overflow-hidden">
            <img
                src="https://images.unsplash.com/photo-1642316655095-6349aa377ce5?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080"
                alt="Zelená energie dosáhla nových milníků"
                class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
            />
            <div class="absolute top-4 left-4">
              <span class="px-3 py-1 bg-blue-600 text-white rounded-full text-sm">
                Životní prostředí
              </span>
            </div>
          </div>

          <div class="p-6">
            <h3 class="text-xl mb-3 group-hover:text-blue-600 transition-colors">
              Zelená energie dosáhla nových milníků
            </h3>
            <p class="text-gray-600 mb-4 leading-relaxed">
              Obnovitelné zdroje pokrývají již 45% celkové spotřeby energie v zemi.
            </p>
            <div class="flex items-center gap-4 text-sm text-gray-500">
              <div class="flex items-center gap-1">
                <UIcon name="lucide:user" class="w-4 h-4" />
                <span>Petr Černý</span>
              </div>
              <div class="flex items-center gap-1">
                <UIcon name="lucide:clock" class="w-4 h-4" />
                <span>1 hod</span>
              </div>
            </div>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>

<script lang="ts">
export default {
  name: 'FeaturedArticles'
}
</script>

<style scoped>
</style>
