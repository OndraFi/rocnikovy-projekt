<template>
  <dashboard-login-form @submit="onLogin" />
</template>

<script lang="ts">
import type {LoginOperationRequest, LoginRequest} from "~~/api";

export default {
  name: 'LoginPage',

  methods: {
    onLogin(payload: LoginRequest) {
      console.log('Login emit chycen:', payload)
      const loginOperationRequest: LoginOperationRequest = {
        loginRequest: payload
      }
      this.$authenticationApi.login(loginOperationRequest).then(response => {
        if(response.token){
          const authStore = useAuthStore();
          authStore.logIn(response.token);
          this.$router.push('/dashboard');
        }
      }).catch(error => {
        const toast = useToast()
        console.log(error)
        toast.add({
          color: "error",
          title: 'Přihlášení selhalo',
          description: 'špatné přihlašovací údaje',
          icon: 'i-lucide-exclamation',
        })
      })
    }
  }
}
</script>
