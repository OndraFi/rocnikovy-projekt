<template>
  <NodeViewWrapper>
  <div class="my-3">
    <div v-if="loading" class="text-xs text-gray-500">Načítám obrázek…</div>
    <img
        v-else
        :src="src"
        :alt="fileName"
        class="max-w-full rounded-md border"
        draggable="true"
    />
    <div v-if="error" class="mt-1 text-xs text-red-600">
      Nepodařilo se načíst obrázek: {{ error }}
    </div>
  </div>
  </NodeViewWrapper>
</template>

<script>
import {NodeViewWrapper} from "@tiptap/vue-3";
import { useRuntimeConfig } from "#imports"

export default {
  name: "ApiImageNodeView",
  components: {NodeViewWrapper},
  props: {
    node: { type: Object, required: true },
    editor: { type: Object, required: true },
    updateAttributes: { type: Function, required: true },
  },
  data() {
    return {
      loading: true,
      src: "",
      error: "",
    }
  },
  computed: {
    fileName() {
      return this.node.attrs.fileName
    },
  },
  watch: {
    fileName: {
      immediate: true,
      async handler() {
        this.loading = true
        this.error = ""
        try {
          console.log("attrs keys:", Object.keys(this.node.attrs), this.node.attrs)
          const config = useRuntimeConfig()
          this.src = config.public.apiBase+'/api/images/' + this.node.attrs.filename

        } catch (e) {
          this.error = e?.message || String(e)
        } finally {
          this.loading = false
        }
      },
    },
  },
}
</script>
