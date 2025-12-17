<template>
  <article>
    <div
        class="prose prose-slate max-w-full"
        v-html="safeHtml"
    />
  </article>
</template>

<script lang="ts">
import { useRuntimeConfig } from '#imports'

export default {
  name: 'HtmlRenderer',
  props: {
    html: {
      type: String,
      required: true
    }
  },
  computed: {
    safeHtml(): string {
      const config = useRuntimeConfig()
      const apiBase = config.public.apiBase

      console.log(apiBase)
      if (!this.html) return ''

      const parser = new DOMParser()
      const doc = parser.parseFromString(this.html, 'text/html')
      const images = doc.querySelectorAll('img')
      images.forEach(img => {
        const src = img.getAttribute('src')
        if (!src) return

        // upravujeme jen relativní /api cesty
        if (src.startsWith('/api')) {
          img.setAttribute('src', `${apiBase}${src}`)
        }
      })

      return doc.body.innerHTML
    }
  }
}
</script>
