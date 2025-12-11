<template>
  <dashboard-settings-register-user v-if="canRegister"/>
  <dashboard-settings-change-password class="mt-4"/>
</template>
<script>

export default {
  setup(){
    setPageLayout('dashboard');
  },
  data(){
    return {
      authStore: useAuthStore(),
    }
  },
  computed:{
    canRegister(){
      return this.authStore.user.role === 'ADMIN' || this.authStore.user.role === 'CHIEF_EDITOR';
    }
  }
}
</script>