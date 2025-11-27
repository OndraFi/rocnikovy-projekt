<template>
  <div>
    <UTable
        :loading="fetching"
        loading-color="primary"
        loading-animation="carousel"
        :data="articles"
        :columns="columns"
        :meta="tableMeta"
        @select="onRowSelect"
        class="flex-1"
    >
    </UTable>

    <div class="flex justify-end border-t border-default pt-4 px-4">
      <UPagination
          :page="page"
          :items-per-page="size"
          :total="totalPages * size"
          @update:page="onPageChange"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, h } from 'vue';
import type {ArticleResponse, CategoryResponse, ListArticlesRequest} from '~~/api';
import type { TableColumn } from '@nuxt/ui';

export default defineComponent({
  name: 'ArticlesPage',
  data() {
    return {
      articles: [] as Array<ArticleResponse>,
      fetching: false,
      page: 0,
      totalPages: 0,
      size: 10,
      tableMeta: {
        class: {
          tr: 'cursor-pointer hover:bg-gray-50'
        }
      },
      columns: [
        {
          accessorKey: 'title',
          header: 'Název',
          meta: {
            class: {
              th: 'text-left font-semibold',
              td: 'text-left'
            }
          }
        },
        {
          accessorKey: 'author',
          header: 'Autor',
          meta: {
            class: {
              th: 'text-left',
              td: 'text-left'
            }
          },
          cell: ({ row }) => {
            const raw = row.getValue('author') as any;
            if (!raw) return '—';
            return raw.fullName;
          }
        },
        {
          accessorKey: 'categories',
          header: 'Kategorie',
          meta: {
            class: {
              th: 'text-left',
              td: 'text-left text-gray-600'
            }
          },
          cell: ({ row }) => {
            const raw = row.getValue('categories');

            if (!raw) return '-';

            // !!! Převod z iteratoru na normální pole
            const categories = [...raw.values()];

            if (!categories.length) return '-';

            return categories.map((c: any) => c.name).join(', ');
          }
        },
        {
          accessorKey: 'publishedAt',
          header: 'Publikováno',
          meta: {
            class: {
              th: 'text-center',
              td: 'text-center font-mono'
            }
          },
          cell: ({ row }) => {
            const raw = row.getValue('publishedAt') as string | Date | undefined;
            if (!raw) return '—';
            return new Date(raw).toLocaleDateString('cs-CZ');
          }
        },
        {
          accessorKey: 'articleState',
          header: 'Stav',
          meta: {
            class: {
              th: 'text-center',
              td: 'text-center'
            }
          },
          cell: ({ row }) => {
            const state = row.getValue('articleState') as string;

            const colorMap: Record<string, string> = {
              PUBLISHED: 'text-success',
              DRAFT: 'text-warning',
              REVIEW: 'text-primary'
            };

            return h(
                'span',
                {
                  class: `font-semibold ${colorMap[state] ?? 'text-gray-500'}`
                },
                state
            );
          }
        }
      ] as TableColumn<ArticleResponse>[]
    };
  },
  setup() {
    definePageMeta({
      layout: 'dashboard',
    });
  },
  methods: {
    onRowSelect(arg1: any, arg2?: any) {
      const row = arg2 && arg2.original ? arg2 : arg1; // funguje pro obě verze
      const article = row?.original ?? row;
      const id = article?.id;

      if (!id) return;

      this.$router.push(`/dashboard/articles/${id}`);
    },
    // volání z UPagination
    onPageChange(p: number) {
      console.log("page change",p);
      this.page = p;
      this.getArticles();
    },

    async getArticles() {
      this.fetching = true;

      const listArticlesRequest: ListArticlesRequest = {
        pageable: {
          page: this.page,
          size: this.size,
        }
      };

      this.$articlesApi.listArticles(listArticlesRequest)
          .then(res => {
            console.log(res);
            if (res.page !== undefined)
              this.page = res.page;

            if (res.totalPages !== undefined)
              this.totalPages = res.totalPages;

            if (res.articles)
              this.articles = res.articles;

            this.fetching = false;
          })
          .catch(err => {
            console.error(err.message);
            this.fetching = false;
          });
    }
  },
  created() {
    this.getArticles();
  }
});
</script>
