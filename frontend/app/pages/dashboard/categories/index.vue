<template>
  <div>
    <UTable
        :loading="fetching"
        loading-color="primary"
        loading-animation="carousel"
        :data="categories"
        :columns="columns"
        :meta="tableMeta"
        @select="onRowSelect"
        class="flex-1"
    />

    <div class="flex justify-end border-t border-default pt-4 px-4">
      <UPagination
          :page="page"
          :items-per-page="size"
          :total="totalElements"
          @update:page="onPageChange"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import type { CategoryResponse, ListCategoriesRequest } from '~~/api'
import type { TableColumn } from '@nuxt/ui'

export default defineComponent({
  name: 'CategoriesPage',
  data() {
    return {
      categories: [] as Array<CategoryResponse>,
      fetching: false,

      // stránkování
      page: 0,              // držím stejný pattern jako u articles
      size: 10,
      totalPages: 0,
      totalElements: 0,

      // styly řádku – hover + pointer
      tableMeta: {
        class: {
          tr: 'cursor-pointer hover:bg-gray-50'
        }
      },

      // sloupce tabulky
      columns: [
        {
          accessorKey: 'id',
          header: 'ID',
          meta: {
            class: {
              th: 'text-center font-semibold',
              td: 'text-center font-mono text-xs text-gray-500'
            }
          }
        },
        {
          accessorKey: 'name',
          header: 'Název',
          meta: {
            class: {
              th: 'text-left font-semibold',
              td: 'text-left'
            }
          }
        },
        {
          accessorKey: 'description',
          header: 'Popis',
          meta: {
            class: {
              th: 'text-left',
              td: 'text-left text-gray-600'
            }
          },
          cell: ({ row }) => {
            const raw = row.getValue('description') as string | undefined
            if (!raw) return '—'
            return raw
          }
        }
      ] as TableColumn<CategoryResponse>[]
    }
  },

  setup() {
    definePageMeta({
      layout: 'dashboard'
    })
  },

  methods: {
    // klik na řádek → detail kategorie
    onRowSelect(arg1: any, arg2?: any) {
      // kompatibilita s různými verzemi: (event, row) / (row, event)
      const row = arg2 && arg2.original ? arg2 : arg1
      const category = row?.original ?? row
      const id = category?.id

      if (!id) return

      this.$router.push(`/dashboard/categories/${id}`)
    },

    // změna stránky z UPagination
    onPageChange(p: number) {
      this.page = p
      this.getCategories()
    },

    async getCategories() {
      this.fetching = true

      const listCategoriesRequest: ListCategoriesRequest = {
        pageable: {
          page: this.page,   // pokud backend chce 0-based → máš to stejně jako u articles
          size: this.size
        }
      }

      this.$categoriesApi
          .listCategories(listCategoriesRequest)
          .then(res => {
            // PaginatedCategoryResponse
            if (res.page !== undefined) {
              this.page = res.page
            }

            if (res.totalPages !== undefined) {
              this.totalPages = res.totalPages
            }

            if (res.totalElements !== undefined) {
              this.totalElements = res.totalElements
            }

            if (res.categories) {
              this.categories = res.categories
            }

            this.fetching = false
          })
          .catch(err => {
            console.error(err.message)
            this.fetching = false
          })
    }
  },

  created() {
    this.getCategories()
  }
})
</script>
