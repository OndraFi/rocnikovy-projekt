import {defineNuxtPlugin} from '#app';
import {useRuntimeConfig} from '#imports';
import {Configuration} from '../../api';
import {useAuthStore} from '@/stores/authStore';
import {
    ArticlesApi,
    ArticleVersionsApi,
    AuthenticationApi,
    CategoriesApi,
    TicketsApi,
    TicketCommentsApi
} from '../../api';
import type {ComponentCustomProperties} from "vue";

declare module '@vue/runtime-core' {
    interface ComponentCustomProperties {
        $articlesApi: ArticlesApi;
        $articlesVersionsApi: ArticleVersionsApi;
        $authenticationApi: AuthenticationApi;
        $categoriesApi: CategoriesApi;
        $ticketsApi: TicketsApi;
        $ticketCommentsApi: TicketCommentsApi;
    }
}
//
// declare module '#app' {
//     interface NuxtApp {
//         $articlesApi: ArticlesApi;
//         $articlesVersionsApi: ArticleVersionsApi;
//         $authenticationApi: AuthenticationApi;
//         $categoriesApi: CategoriesApi;
//         $ticketsApi: TicketsApi;
//         $ticketCommentsApi: TicketCommentsApi;
//     }
// }

export default defineNuxtPlugin((nuxtApp) => {
    const vueApp = nuxtApp.vueApp;
    const runtimeConfig = useRuntimeConfig();
    const authStore = useAuthStore();

    const config = new Configuration({
        basePath: runtimeConfig.public.apiBase,
        accessToken: () => authStore.token || ''
    });

    const articlesApi = new ArticlesApi(config);
    const articlesVersionsApi = new ArticleVersionsApi(config);
    const authenticationApi = new AuthenticationApi(config);
    const categoriesApi = new CategoriesApi(config);
    const ticketsApi = new TicketsApi(config);
    const ticketCommentsApi = new TicketCommentsApi(config);

    vueApp.config.globalProperties.$articlesApi = articlesApi;
    vueApp.config.globalProperties.$articlesVersionsApi = articlesVersionsApi;
    vueApp.config.globalProperties.$authenticationApi = authenticationApi;
    vueApp.config.globalProperties.$categoriesApi = categoriesApi;
    vueApp.config.globalProperties.$ticketsApi = ticketsApi;
    vueApp.config.globalProperties.$ticketCommentsApi = ticketCommentsApi;
});
