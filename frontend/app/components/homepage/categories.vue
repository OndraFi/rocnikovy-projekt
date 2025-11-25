<template>
  <section class="py-16 px-4 sm:px-6 lg:px-8 bg-gray-50">
    <div class="max-w-7xl mx-auto">

      <h2 class="text-3xl mb-8 text-center">Rubriky</h2>

      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">

        <!-- Zprávy -->
        <div
            class="group cursor-pointer bg-white rounded-xl p-6 text-center hover:shadow-xl transition-all border border-gray-200 hover:border-transparent"
          v-for="c in categories"
        >
          <div
              class="w-16 h-16 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl flex items-center justify-center mx-auto mb-4 group-hover:scale-110 transition-transform"
          >
            <UIcon name="lucide:folder" class="w-8 h-8 text-white" />
          </div>
          <h3 class="mb-1">{{c.name}}</h3>
          <p class="text-sm text-gray-500">{{c.description}}</p>
        </div>


      </div>
      <div class="flex">
        <UButton v-if="canGetAnotherPage"
                 :disabled="fetching"
                 class="ms-auto me-auto mt-6"
                 size="xl"
                 variant="outline"
                 color="primary"
                 @click="getAnotherPage" >načíst další</UButton>
      </div>
    </div>
  </section>
</template>

<script lang="ts">
import type {CategoryResponse, ListCategoriesRequest} from "~~/api";

export default {
  name: 'Categories',
  data(){
    return {
      categories: null as CategoryResponse[] | null,
      page: 1,
      size: 6,
      canGetAnotherPage: true,
      fetching: false,
    }
  },
  methods:{
    async getAnotherPage(){
      this.page++;
      await this.getCategories();
    },
    async getCategories(){
      this.fetching = true;
      const listCategoriesRequest : ListCategoriesRequest = {
        pageable: {
          page: this.page,
          size: this.size
        }
      }
      this.$categoriesApi.listCategories(listCategoriesRequest).then(res=>{
        if(res.categories)
          this.categories = res.categories;
        if(res.totalPages && this.page >= res.totalPages)
          this.canGetAnotherPage = false;
        console.log(res);
        this.fetching = false;
      }).catch(err=>{
        console.error(err.message)
        this.fetching = false;
      })
    }
  },
  created(){
    this.getCategories();
  }
}
</script>

<style scoped>
</style>
