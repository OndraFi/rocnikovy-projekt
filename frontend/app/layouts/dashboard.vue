<script setup lang="ts">
import type { NavigationMenuItem } from '@nuxt/ui'

const route = useRoute()
const toast = useToast()

const open = ref(false)

const links = [
  [
    {
      label: 'Dashboard',
      icon: 'i-lucide-house',
      to: '/dashboard',
      onSelect: () => {
        open.value = false
      }
    },
    {
      label: 'Categories',
      icon: 'i-lucide-list',
      to: '/dashboard/categories',
      onSelect: () => {
        open.value = false
      }
    },
    {
      label: 'Articles',
      icon: 'i-lucide-book-open',
      to: '/dashboard/articles',
      onSelect: () => {
        open.value = false
      }
    },
    {
      label: 'Tickets',
      icon: 'i-lucide-ticket',
      to: '/dashboard/tickets',
      onSelect: () => {
        open.value = false
      }
    }
  ], 
  [
    {
      label: 'Otevřít deník',
      icon: 'i-lucide-file',
      to: 'http://localhost:3000',
      target: '_blank'
    }
  ]
] satisfies NavigationMenuItem[][]

const groups = computed(() => [{
  id: 'links',
  label: 'Go to',
  items: links.flat()
}])

const authStore = useAuthStore();
const router = useRouter();
const logout = () => {
  authStore.logout()
  router.push('/dashboard/login')
}
</script>

<template>
  <UDashboardGroup unit="rem">
    <UDashboardSidebar
        id="default"
        v-model:open="open"
        collapsible
        resizable
        class="bg-elevated/25"
        :ui="{ footer: 'lg:border-t lg:border-default' }"
    >
      <template #header="{ collapsed }">
        <UIcon name="lucide:newspaper" class="w-8 h-8 text-primary-600" />
      </template>

      <template #default="{ collapsed }">
        <UDashboardSearchButton :collapsed="collapsed" class="bg-transparent ring-default" />

        <UNavigationMenu
            :collapsed="collapsed"
            :items="links[0]"
            orientation="vertical"
            tooltip
            popover
        />

        <UNavigationMenu
            :collapsed="collapsed"
            :items="links[1]"
            orientation="vertical"
            tooltip
            class="mt-auto"
        />
      </template>

      <template #footer="{ collapsed }">
        <UButton icon="i-lucide-log-out" class="w-full" @click="logout">Odhlásit</UButton>
      </template>
    </UDashboardSidebar>

    <UDashboardSearch :groups="groups" />

    <UDashboardPanel id="Dashboard">
      <template #header>
        <UDashboardNavbar title="Dashboard" :ui="{ right: 'gap-3' }">
          <template #leading>
            <UDashboardSidebarCollapse />
          </template>

          <template #right>
            <slot name="actions" />
          </template>
        </UDashboardNavbar>

        <UDashboardToolbar>
          <template #left>
            <dashboard-bread-crumbs/>
          </template>
        </UDashboardToolbar>
      </template>

      <template #body>
        <slot />
      </template>
    </UDashboardPanel>

  </UDashboardGroup>
</template>