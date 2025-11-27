export default defineNuxtRouteMiddleware((to, from) => {

    const authStore = useAuthStore();
    if (to.path.startsWith('/dashboard') && to.path !== '/dashboard/login' && !authStore.isLoggedIn()) {
        return navigateTo('/dashboard/login')
    }
})