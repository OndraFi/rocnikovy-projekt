<template>
  <dashboard-settings-users-table />
  <dashboard-settings-register-user v-if="authStore.isAdminOrChief()"/>
  <dashboard-settings-change-password class="mt-4"/>
</template>
<script lang="ts">
export default {
  setup(){
    setPageLayout('dashboard');
  },
  data(){
    return {
      authStore: useAuthStore(),
    }
  },
}
</script>