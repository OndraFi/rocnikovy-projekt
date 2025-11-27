<template>
  <UApp>
    <NuxtLoadingIndicator />
    <NuxtLayout>
      <NuxtPage/>
    </NuxtLayout>
  </UApp>
</template>
<!--<script setup lang="ts">-->
<!--const route = useRoute();-->

<!--const layout = computed(() => {-->
<!--  if (route.path === '/dashboard/login') {-->
<!--    return 'login';-->
<!--  }-->

<!--  if (route.path.startsWith('/dashboard')) {-->
<!--    return 'dashboard';-->
<!--  }-->

<!--  return 'default';-->
<!--});-->
<!--</script>-->
