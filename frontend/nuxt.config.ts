// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    compatibilityDate: '2025-07-15',
    devtools: {enabled: true},
    modules: [
        '@nuxt/test-utils/module',
        '@pinia/nuxt',
        'pinia-plugin-persistedstate/nuxt',
        '@nuxt/ui',
    ],
    css: ['~/assets/css/main.css'],
    runtimeConfig: {
        public: {
            apiBase: 'http://localhost:8080'
        }
    },
    ui: {
        colorMode: false
    }
})
