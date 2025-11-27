<script setup lang="ts">
import type { BreadcrumbItem } from '@nuxt/ui'

const route = useRoute()

/**
 * Převod segmentu z URL na hezčí label:
 * "blog-posty" -> "Blog posty"
 */
const formatSegment = (segment: string): string => {
  return segment
      .split('-')
      .map(part => part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ')
}

const items = computed<BreadcrumbItem[]>(() => {
  // route.path např. "/dashboard/articles"
  const path = route?.path?.split('?')[0]?.split('#')[0]

  const segments = path
      ?.split('/')
      ?.filter(Boolean) // vyhodí prázdné stringy kolem "/"

  const crumbs: BreadcrumbItem[] = []

  // // Root / Home
  // crumbs.push({
  //   label: 'Home',
  //   icon: 'i-lucide-house',
  //   to: '/'
  // })

  let cumulativePath = ''

  segments.forEach((segment, index) => {
    cumulativePath += `/${segment}`
    const isLast = index === segments.length - 1

    crumbs.push({
      label: formatSegment(segment),
      // poslední breadcrumb bude jen text, ne odkaz
      to: isLast ? undefined : cumulativePath
    })
  })

  return crumbs
})
</script>

<template>
  <div class="mb-4">
    <UBreadcrumb :items="items">
      <!-- vlastní separator, ať to vypadá jako "dashboard -> articles" -->
      <template #separator>
        <span class="mx-2 text-gray-400">→</span>
      </template>
    </UBreadcrumb>
  </div>
</template>
