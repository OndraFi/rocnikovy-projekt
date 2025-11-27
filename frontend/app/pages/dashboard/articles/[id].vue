<template>
  <div>
    article detail
  </div>
</template>
<script lang="ts">
import type {ArticleResponse} from "~~/api";

export default{
  setup(){
    definePageMeta({
      layout: 'dashboard',
        })
  },
  data(){
    return {
      article: null as ArticleResponse | null,
      fetching: false,
    }
  },
  methods: {
    getArticle(){
      this.$route.params.id;
    }
  },
  created(){
    this.getArticle();
  }
}
</script>