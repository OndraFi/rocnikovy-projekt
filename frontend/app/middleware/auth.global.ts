export default defineNuxtRouteMiddleware((to, from) => {
    // isAuthenticated() is an example method verifying if a user is authenticated
    const authStore = useAuthStore();
    if (to.path.startsWith('/dashboard') && to.path !== '/dashboard/login' && !authStore.isLoggedIn()) {
        return navigateTo('/dashboard/login')
    }
})