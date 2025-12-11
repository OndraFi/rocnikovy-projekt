<template>
  <NuxtLayout>
    <template #actions>
      <dashboard-tickets-create-modal/>
    </template>
    <div class="flex justify-end pt-4 px-4 gap-x-4">
        <UButton variant="outline"
                :disabled="filter === ListMyTicketsFilterTypeEnum.Owned"
                 @click="filterTickets(ListMyTicketsFilterTypeEnum.Owned)">Vlastněné
        </UButton>
        <UButton variant="outline"
                 :disabled="filter === ListMyTicketsFilterTypeEnum.Assigned"
                 @click="filterTickets(ListMyTicketsFilterTypeEnum.Assigned)">Přiřazené
        </UButton>
        <UButton variant="outline"
                 :disabled="filter === ListMyTicketsFilterTypeEnum.All"
                 @click="filterTickets(ListMyTicketsFilterTypeEnum.All)">Všechny
        </UButton>
    </div>
    <UTable
        :loading="fetching"
        loading-color="primary"
        loading-animation="carousel"
        :data="tickets"
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

  </NuxtLayout>
</template>

<script lang="ts">
import {defineComponent, h} from 'vue'
import {
  type TicketResponse,
  type ListTicketsRequest,
  type UserResponse, ListMyTicketsFilterTypeEnum, type ListMyTicketsRequest
} from '~~/api'
import type {TableColumn} from '@nuxt/ui'

export default defineComponent({
  name: 'TicketsPage',
  computed: {
    ListMyTicketsFilterTypeEnum() {
      return ListMyTicketsFilterTypeEnum
    }
  },

  data() {
    return {
      filter: 'ALL',
      tickets: [] as TicketResponse[],
      fetching: false,

      // stránkování
      page: 0,
      size: 10,
      totalPages: 0,
      totalElements: 0,

      // hover + pointer na řádek
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
          accessorKey: 'state',
          header: 'Stav',
          meta: {
            class: {
              th: 'text-center',
              td: 'text-center'
            }
          },
          cell: ({row}) => {
            const state = row.getValue('state') as string | undefined

            if (!state) return '—'

            const labelMap: Record<string, string> = {
              OPEN: 'Otevřený',
              IN_PROGRESS: 'Probíhá',
              FOR_REVIEW: 'Ke schválení',
              APPROVED: 'Schváleno',
              PUBLISHED: 'Publikováno'
            }

            const colorMap: Record<string, string> = {
              OPEN: 'bg-gray-100 text-gray-700',
              IN_PROGRESS: 'bg-blue-100 text-blue-700',
              FOR_REVIEW: 'bg-yellow-100 text-yellow-700',
              APPROVED: 'bg-emerald-100 text-emerald-700',
              PUBLISHED: 'bg-primary/10 text-primary'
            }

            return h(
                'span',
                {
                  class:
                      'inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ' +
                      (colorMap[state] ?? 'bg-gray-100 text-gray-700')
                },
                labelMap[state] ?? state
            )
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
          cell: ({row}) => {
            const user = row.getValue('author') as UserResponse | undefined
            if (!user) return '—'
            return user.fullName || user.username || `#${user.id}`
          }
        },
        {
          accessorKey: 'assignee',
          header: 'Přiřazeno',
          meta: {
            class: {
              th: 'text-left',
              td: 'text-left'
            }
          },
          cell: ({row}) => {
            const user = row.getValue('assignee') as UserResponse | undefined
            if (!user) return '—'
            return user.fullName || user.username || `#${user.id}`
          }
        },
        {
          accessorKey: 'createdAt',
          header: 'Vytvořeno',
          meta: {
            class: {
              th: 'text-center',
              td: 'text-center font-mono text-xs'
            }
          },
          cell: ({row}) => {
            const raw = row.getValue('createdAt') as string | Date | undefined
            if (!raw) return '—'
            return new Date(raw).toLocaleString('cs-CZ', {
              day: '2-digit',
              month: '2-digit',
              year: 'numeric',
              hour: '2-digit',
              minute: '2-digit'
            })
          }
        },
        {
          accessorKey: 'updatedAt',
          header: 'Aktualizováno',
          meta: {
            class: {
              th: 'text-center',
              td: 'text-center font-mono text-xs'
            }
          },
          cell: ({row}) => {
            const raw = row.getValue('updatedAt') as string | Date | undefined
            if (!raw) return '—'
            return new Date(raw).toLocaleString('cs-CZ', {
              day: '2-digit',
              month: '2-digit',
              year: 'numeric',
              hour: '2-digit',
              minute: '2-digit'
            })
          }
        }
      ] as TableColumn<TicketResponse>[]
    }
  },

  setup() {
    definePageMeta({
      layout: 'dashboard'
    })
  },

  methods: {
    filterTickets(filter: ListMyTicketsFilterTypeEnum) {
      this.filter = filter;
      this.$router.replace({
        query: {
          ...this.$router.currentRoute.value.query,
          filter
        }
      })
      this.getTickets()
    },
    // klik na řádek → detail ticketu
    onRowSelect(arg1: any, arg2?: any) {
      const row = arg2 && arg2.original ? arg2 : arg1
      const ticket = row?.original ?? row
      const id = ticket?.id

      if (!id) return

      this.$router.push(`/dashboard/tickets/${id}`)
    },

    onPageChange(p: number) {
      this.page = p
      this.getTickets()
    },

    async getTickets() {
      this.fetching = true

      const request: ListMyTicketsRequest = {
        pageable: {
          page: this.page, // pokud backend chce 0-based a UI je 1-based, můžeš si tady hrát s -1/+1
          size: this.size
        },
        filterType: this.filter
      }

      this.$ticketsApi
          .listMyTickets(request)
          .then(res => {
            if (res.page !== undefined) this.page = res.page
            if (res.totalPages !== undefined) this.totalPages = res.totalPages
            if (res.totalElements !== undefined)
              this.totalElements = res.totalElements
            if (res.tickets) this.tickets = res.tickets

            this.fetching = false
          })
          .catch(err => {
            console.error(err?.message || err)
            this.fetching = false
          })
    }
  },

  created() {
    this.filter = this.$route.query.filter
    this.getTickets()
  }
})
</script>
