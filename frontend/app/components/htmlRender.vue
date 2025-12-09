<template>
  <article>
    <div
        class="prose prose-slate max-w-full"
        v-html="safeHtml"
    />
  </article>
</template>

<script lang="ts">
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
      // kdybys chtěl sanitizovat:
      // return DOMPurify.sanitize(this.html)
      return this.html
    }
  }
}
</script>
