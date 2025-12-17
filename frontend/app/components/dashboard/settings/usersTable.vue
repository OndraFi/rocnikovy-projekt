<template>
  <div class="flex flex-col flex-1">

    <UTable
        :data="users"
        :columns="columns"
        :meta="tableMeta"
        :loading="fetching"
        loading-color="primary"
        loading-animation="carousel"
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
import { defineComponent, h, resolveComponent } from "vue";
import type { TableColumn, TableMeta } from "@nuxt/ui";
import type { UserResponse, ListUsersRequest } from "~~/api";

export default defineComponent({
  name: "UsersTable",

  data() {
    return {
      users: [] as (UserResponse & { blocked?: boolean })[],
      fetching: false,

      page: 0,
      size: 10,
      totalElements: 0,
      totalPages: 0,

      columns: [] as TableColumn<UserResponse>[],
      tableMeta: {
        class: {
          tr: "cursor-default hover:bg-gray-50"
        }
      } as TableMeta<UserResponse>
    };
  },

  created() {
    this.initColumns();
    this.getUsers();
  },

  methods: {
    initColumns() {
      const UButton = resolveComponent("UButton");

      this.columns = [
        {
          accessorKey: "id",
          header: "ID",
          meta: {
            class: {
              th: "text-center font-semibold",
              td: "text-center font-mono text-xs text-gray-500"
            }
          }
        },
        {
          accessorKey: "username",
          header: "Uživatelské jméno",
          meta: {
            class: {
              th: "text-left font-semibold",
              td: "text-left font-mono text-xs"
            }
          }
        },
        {
          accessorKey: "fullName",
          header: "Jméno",
          meta: {
            class: {
              th: "text-left font-semibold",
              td: "text-left"
            }
          },
          cell: ({ row }) => {
            const full = row.getValue("fullName");
            const user = row.getValue("username");

            return full ? `${full} (${user})` : user ?? "—";
          }
        },
        {
          accessorKey: "role",
          header: "Role",
          meta: {
            class: {
              th: "text-center font-semibold",
              td: "text-center uppercase text-xs text-gray-700"
            }
          }
        },
        {
          id: "actions",
          header: "Akce",
          meta: {
            class: {
              th: "text-right font-semibold",
              td: "text-right"
            }
          },
          cell: ({ row }) => {
            const user = row.original;

            const isBlocked = !user.active;
            const label = isBlocked ? "Odblokovat" : "Zablokovat";
            const color = isBlocked ? "primary" : "error";
            const icon = isBlocked ? "i-lucide-unlock-2" : "i-lucide-ban";

            return h(
                UButton,
                {
                  size: "xs",
                  variant: "outline",
                  color,
                  icon,
                  onClick: () => this.toggleBlock(user)
                },
                { default: () => label }
            );
          }
        }
      ];
    },
    async getUsers() {
      this.fetching = true;

      const request: ListUsersRequest = {
        pageable: {
          page: this.page,
          size: this.size
        }
      };

      try {
        const res: any = await this.$usersApi.listUsers(request);

        // Najdi správné pole s uživateli
        this.users = res.users ?? res.content ?? res.items ?? [];

        if (typeof res.page === "number") this.page = res.page;
        if (typeof res.totalPages === "number") this.totalPages = res.totalPages;
        if (typeof res.totalElements === "number") this.totalElements = res.totalElements;
      } catch (err) {
        console.error("getUsers error:", err);
      } finally {
        this.fetching = false;
      }
    },

    async toggleBlock(user: any) {
      console.log("block toogle");
      console.log(user);
      if (!user.username) return;

      const isBlocked = !user.active;

      try {
        if (isBlocked) {
          await this.$usersApi.unblockUser({ identifier: user.username });
          console.log("unblocking")
        } else {
          const res = await this.$usersApi.blockUser({ identifier: user.username });
          console.log("blocking", res)
        }

        this.getUsers();
      } catch (err) {
        console.error("toggleBlock error:", err);
      }
    },
    onPageChange(p: number) {
      this.page = p;
      this.getUsers();
    }
  }
});
</script>
