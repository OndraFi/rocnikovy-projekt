<template>
  <UModal title="Přidat kategorii">
    <UButton label="Přidat kategorii" color="primary" variant="solid" />

    <template #body>
      <UForm @submit.prevent="createCategory">
        <UFormField label="Název"  name="name">
          <UInput class="w-full" v-model="name"/>
        </UFormField>
        <UFormField label="Popis"  name="description">
          <UTextarea class="w-full" v-model="description"/>
        </UFormField>
        <UButton class="mt-4" type="submit">přidat</UButton>
      </UForm>
    </template>
  </UModal>
</template>
<script lang="ts">
import {type CategoryResponse, type CreateCategoryOperationRequest} from "~~/api/index.js";

export default {
  data(){
    return {
      name: "",
      description: "",
      isLoading: false,
      toast: useToast()
    }
  },
  methods: {
    createCategory(){
      const request : CreateCategoryOperationRequest = {
        createCategoryRequest: {
          name: this.name,
          description: this.description,
        },
      }
      console.log(request)
      this.$categoriesApi.createCategory(request).then((res: CategoryResponse)=>{
        if(res.id){
          this.toast.add({
            title: res.name,
            description: 'Kategorie úspěšně přidána.',
            color: 'primary'
          })
          this.$router.push("/dashboard/categories/" + res.id);
        }
      }).catch(err=>{
        console.log(err);
        this.toast.add({
          title: 'Kategorie',
          description: 'Kategorii se nepovedlo vytvořit.',
          color: 'error'
        })
      })
    }
  }
}
</script>