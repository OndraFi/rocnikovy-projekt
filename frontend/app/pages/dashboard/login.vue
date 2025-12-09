<template>
  <dashboard-login-form @submit="onLogin" />
</template>

<script lang="ts">
import type {LoginOperationRequest, LoginRequest, UserResponse} from "~~/api";

export default {
  name: 'LoginPage',
  setup(){
    definePageMeta({
      layout: 'login',
    })
  },
  data(){
    return {
      authStore: useAuthStore(),
    }
  },
  methods: {
    onLogin(payload: LoginRequest) {
      console.log('Login emit chycen:', payload)
      const loginOperationRequest: LoginOperationRequest = {
        loginRequest: payload
      }
      this.$authenticationApi.login(loginOperationRequest).then(response => {
        if(response.token){
          this.authStore.logIn(response.token);

          this.$authenticationApi.getInfo().then((response: UserResponse)=>{
            this.authStore.setUser(response);
            this.$router.push('/dashboard');
          });
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
