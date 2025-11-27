<template>
  <div class="p-6 space-y-8">

    <!-- Header -->
    <div class="flex items-center justify-between">
      <h1 class="text-3xl font-bold">{{ article?.title }}</h1>

      <UBadge
        :color="stateColor"
        size="lg"
        class="uppercase tracking-wide"
      >
        {{ article?.articleState }}
      </UBadge>
    </div>

    <!-- Meta info -->
    <div class="bg-white dark:bg-gray-900 rounded-xl shadow p-6 space-y-4">

      <div class="grid grid-cols-2 gap-4">
        <div>
          <h3 class="font-semibold text-gray-500">Autor</h3>
          <p>{{ article?.author?.fullName ?? '—' }}</p>
        </div>

        <div>
          <h3 class="font-semibold text-gray-500">Editor</h3>
          <p>{{ article?.editor?.fullName ?? '—' }}</p>
        </div>

        <div>
          <h3 class="font-semibold text-gray-500">Publikováno</h3>
          <p>
            {{ article?.publishedAt
              ? new Date(article.publishedAt).toLocaleString('cs-CZ')
              : '—'
            }}
          </p>
        </div>

        <div>
          <h3 class="font-semibold text-gray-500">Verze</h3>
          <p>{{ article?.currentVersion }}</p>
        </div>
      </div>

      <!-- Categories -->
      <div>
        <h3 class="font-semibold text-gray-500 mb-2">Kategorie</h3>

        <div v-if="article?.categories?.length" class="flex gap-2 flex-wrap">
          <UBadge
            v-for="cat in article.categories"
            :key="cat.id"
            color="gray"
          >
            {{ cat.name }}
          </UBadge>
        </div>

        <p v-else class="text-gray-400">Žádné kategorie</p>
      </div>
    </div>

    <!-- Content -->
    <div class="bg-white dark:bg-gray-900 rounded-xl shadow p-6">
      <h2 class="text-xl font-semibold mb-4">Obsah článku</h2>

      <div class="prose dark:prose-invert max-w-none">
        <p v-if="!article?.content" class="text-gray-400">Bez obsahu</p>
        <div v-else v-html="article.content"></div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import type { ArticleResponse } from '~~/api';

definePageMeta({
  layout: 'dashboard',
});

const route = useRoute();
const id = Number(route.params.id);

const article = ref<ArticleResponse | null>(null);

// Načtení článku
const { $articlesApi } = useNuxtApp();

const { data, error } = await useAsyncData(
  `article-${id}`,
  () => $articlesApi.getArticle(id)
);

if (data.value) {
  article.value = data.value;
}

// Stav → barva badge
const stateColor = computed(() => {
  switch (article.value?.articleState) {
    case 'PUBLISHED': return 'green';
    case 'REVIEW': return 'blue';
    case 'DRAFT': return 'yellow';
    default: return 'gray';
  }
});
</script>

<style scoped>
/* Styl pro HTML obsah článku */
.prose :deep(img) {
  max-width: 100%;
  border-radius: 8px;
}
</style>
