<template>
  <UDashboardPanel id="Dashboard">
    <template #header>
      <UDashboardNavbar title="Dashboard" :ui="{ right: 'gap-3' }">
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>

        <template #right>
          zde může něco být
        </template>
      </UDashboardNavbar>

      <UDashboardToolbar>
        <template #left>
          nějaký tlačítka??
        </template>
      </UDashboardToolbar>
    </template>

    <template #body>
      body
    </template>
  </UDashboardPanel>
</template>