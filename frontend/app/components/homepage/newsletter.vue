<template>
  <section class="py-20 px-4 sm:px-6 lg:px-8 bg-gradient-to-br from-blue-600 to-purple-600">
    <div class="max-w-4xl mx-auto text-center text-white">
      <div class="inline-flex items-center justify-center w-16 h-16 bg-white/20 backdrop-blur-sm rounded-full mb-6">
        <UIcon name="lucide:mail" class="w-8 h-8" />
      </div>

      <h2 class="text-3xl sm:text-4xl mb-4">
        Odebírejte náš newsletter
      </h2>
      <p class="text-xl text-blue-100 mb-8 max-w-2xl mx-auto">
        Dostávejte nejdůležitější zprávy dne přímo do vaší emailové schránky. Každý den ráno.
      </p>

      <div class="flex flex-col sm:flex-row gap-3 max-w-md mx-auto">
        <input
            type="email"
            placeholder="váš@email.cz"
            class="flex-1 px-6 py-4 rounded-lg text-gray-900 focus:outline-none focus:ring-2 focus:ring-white text-white"
        />
        <button class="cursor-pointer px-8 py-4 bg-white text-blue-600 rounded-lg hover:bg-gray-100 transition-colors whitespace-nowrap">
          Odebírat
        </button>
      </div>

      <p class="text-sm text-blue-100 mt-4">
        Odesláním souhlasíte s našimi podmínkami ochrany osobních údajů
      </p>
    </div>
  </section>
</template>