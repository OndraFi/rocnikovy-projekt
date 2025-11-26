<template>
  <section class="py-12 px-4 sm:px-6 lg:px-8 bg-gradient-to-b from-gray-50 to-white">
    <div class="max-w-7xl mx-auto">

<!--      <div>-->
<!--        <h1 class="font-black text-7xl ">Deník Online</h1>-->
<!--        <p class="font-bold text-xl mb-12 mt-3">Prohlídněte si nejnovější články</p>-->
<!--      </div>-->

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">

        <!-- Main Article -->
        <div class="lg:col-span-1">
          <div
              v-if="mainArticle"
              class="relative group overflow-hidden rounded-2xl bg-gradient-to-br from-blue-600 via-indigo-600 to-purple-600 text-white p-8 h-full flex flex-col justify-end"
          >
            <div class="flex items-center gap-3 mb-4">
              <span class="px-3 py-1 bg-black/30 rounded-full text-sm">
                {{ mainCategory(mainArticle) }}
              </span>
              <div class="flex items-center gap-1 text-sm text-gray-200">
                <UIcon name="lucide:clock" class="w-4 h-4" />
                <span>{{ formatTimeAgo(mainArticle?.publishedAt?.toString()) }}</span>
              </div>
            </div>

            <h2 class="text-3xl sm:text-4xl mb-2 leading-tight">
              {{ mainArticle.title }}
            </h2>

            <p class="text-gray-100 text-sm">
              {{ mainArticle.author?.fullName || mainArticle.author?.username }}
            </p>
          </div>

          <div v-else class="h-full flex items-center justify-center text-gray-400 text-sm">
            Žádné články k zobrazení.
          </div>
        </div>

        <!-- Side Articles -->
        <div class="space-y-6">
          <div
              v-for="article in sideArticles"
              :key="article.id"
              class="group cursor-pointer bg-white rounded-xl overflow-hidden border border-gray-200 hover:shadow-xl transition-all"
          >
            <div class="flex gap-4">
              <!-- místo obrázku jen „avatar“ s prvním písmenem -->
              <div class="relative w-16 h-16 sm:w-22 sm:h-22 flex-shrink-0 flex items-center justify-center rounded-lg bg-gradient-to-br from-slate-200 to-slate-100">
                <span class="text-xl font-semibold text-slate-700">
                  {{ article?.title?.charAt(0) }}
                </span>
              </div>

              <div class="flex-1 py-4 pr-4">
                <div class="flex items-center gap-2 mb-2">
                  <span class="px-2 py-1 bg-slate-900 text-white rounded text-xs">
                    {{ mainCategory(article) }}
                  </span>
                  <span class="text-xs text-gray-500">
                    {{ formatTimeAgo(article?.publishedAt?.toString()) }}
                  </span>
                </div>
                <h3 class="text-lg leading-tight group-hover:text-blue-600 transition-colors line-clamp-2">
                  {{ article.title }}
                </h3>
              </div>
            </div>
          </div>

          <div v-if="!fetching && sideArticles.length === 0" class="text-gray-400 text-sm">
            Žádné další články.
          </div>
        </div>
      </div>

    </div>
  </section>
</template>

<script lang="ts">
import type { ArticleResponse, ListArticlesRequest } from '~~/api';

export default {
  name: 'Hero',
  data() {
    return {
      articles: [] as ArticleResponse[],
      fetching: false
    };
  },
  computed: {
    mainArticle(): ArticleResponse | null | undefined {
      return this.articles.length > 0 ? this.articles[0] : null;
    },
    sideArticles(): ArticleResponse[] {
      return this.articles.length > 1 ? this.articles.slice(1, 4) : [];
    }
  },
  methods: {
    getArticles() {
      this.fetching = true;

      const request: ListArticlesRequest = {
        pageable: {
          page: 0,      // tvoje API podle response začíná od 0
          size: 4,
          sort: ['publishedAt,desc']
        }
      };

      this.$articlesApi
          .listArticles(request)
          .then((res) => {
            if (res.articles) {
              this.articles = res.articles;
            }
            console.log(res);
          })
          .catch((err) => {
            console.error(err);
          })
          .finally(() => {
            this.fetching = false;
          });
    },
    formatTimeAgo(isoString?: string): string {
      if (!isoString) return '';
      const date = new Date(isoString);
      const now = new Date();
      const diffMs = now.getTime() - date.getTime();

      const minutes = Math.floor(diffMs / 60000);
      if (minutes < 1) return 'před chvílí';
      if (minutes < 60) return `před ${minutes} min`;

      const hours = Math.floor(minutes / 60);
      if (hours < 24) return `před ${hours} hod`;

      const days = Math.floor(hours / 24);
      return `před ${days} dny`;
    },
    mainCategory(article: ArticleResponse): string {
      // podle JSONu máš teď "categories": {}, ale do budoucna to asi bude list
      const anyArticle = article as any;
      if (Array.isArray(anyArticle.categories) && anyArticle.categories.length > 0) {
        return anyArticle.categories[0]?.value.name || 'Článek';
      }
      return 'Článek';
    }
  },
  created() {
    this.getArticles();
  }
};
</script>

<style scoped>
/* případně sem můžeš hodit nějaké drobné úpravy */
</style>
