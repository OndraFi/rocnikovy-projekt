<template>
  <div>
    <h1>Articles</h1>
    <UTable :data="articles" class="flex-1" />
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import type { ArticleResponse, ListArticlesRequest, ListCategoriesRequest, UserResponse } from '~~/api';

export default defineComponent({
  name: 'ArticlesPage',
  data() {
    return {
      articles: [] as ArticleResponse[],
    };
  },

  methods: {
    async getArticles(){
      const listArticlesRequest : ListArticlesRequest = {
        pageable: {}
      }
      this.$articlesApi.listArticles(listArticlesRequest).then(res=>{
        if(res.articles)
          this.articles = res.articles;
        console.log(res);
      }).catch(err=>{
        console.error(err.message)
      })
    }
  },
  created() {
    this.getArticles();
  }
});
</script>
