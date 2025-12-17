<template>
  <div class="border rounded-md bg-white shadow-sm">
    <!-- TOOLBAR -->
      <div
          v-if="editor"
          class=" z-50 w-full flex flex-wrap items-center justify-between gap-2 border-b bg-gray-50/80 px-3 py-2 backdrop-blur"
      >
        <!-- LEFT SIDE: block type + basic formatting -->
        <div class=" flex flex-wrap items-center gap-1.5">
          <!-- Block type dropdown -->
          <div class="relative">
            <select
                class="h-8 min-w-[120px] rounded-md border border-gray-300 bg-white px-2 text-xs font-medium text-gray-800 shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                v-model="blockType"
            >
              <option value="paragraph">Text</option>
              <option value="h1">Nadpis 1</option>
              <option value="h2">Nadpis 2</option>
              <option value="h3">Nadpis 3</option>
            </select>
          </div>

          <div class="mx-1 h-6 w-px bg-gray-200" />

          <!-- Bold / Italic / Underline / Strike -->
          <button
              type="button"
              :class="buttonClass(editor?.isActive('bold'))"
              @click="editor?.chain().focus().toggleBold().run()"
          >
            <UIcon name="i-lucide-bold" class="h-4 w-4" />
          </button>
          <button
              type="button"
              :class="buttonClass(editor?.isActive('italic'))"
              @click="editor?.chain().focus().toggleItalic().run()"
          >
            <UIcon name="i-lucide-italic" class="h-4 w-4" />
          </button>
          <button
              type="button"
              :class="buttonClass(editor?.isActive('underline'))"
              @click="editor?.chain().focus().toggleUnderline().run()"
          >
            <UIcon name="i-lucide-underline" class="h-4 w-4" />
          </button>
          <button
              type="button"
              :class="buttonClass(editor?.isActive('link'))"
              @click="toggleLink"
          >
            <UIcon name="i-lucide-link-2" class="h-4 w-4" />
          </button>
          <button
              type="button"
              :class="buttonClass(editor?.isActive('strike'))"
              @click="editor?.chain().focus().toggleStrike().run()"
          >
            <UIcon name="i-lucide-strikethrough" class="h-4 w-4" />
          </button>

          <div class="mx-1 h-6 w-px bg-gray-200" />

          <!-- Lists -->
          <button
              type="button"
              :class="buttonClass(editor?.isActive('bulletList'))"
              @click="editor?.chain().focus().toggleBulletList().run()"
          >
            <UIcon name="i-lucide-list" class="h-4 w-4" />
          </button>
          <button
              type="button"
              :class="buttonClass(editor?.isActive('orderedList'))"
              @click="editor?.chain().focus().toggleOrderedList().run()"
          >
            <UIcon name="i-lucide-list-ordered" class="h-4 w-4" />
          </button>

          <div class="mx-1 h-6 w-px bg-gray-200" />

          <!-- Alignment -->
          <button
              type="button"
              :class="buttonClass(editor?.isActive({ textAlign: 'left' }))"
              @click="toggleAlign('left')"
          >
            <UIcon name="i-lucide-align-left" class="h-4 w-4" />
          </button>
          <button
              type="button"
              :class="buttonClass(editor?.isActive({ textAlign: 'center' }))"
              @click="toggleAlign('center')"
          >
            <UIcon name="i-lucide-align-center" class="h-4 w-4" />
          </button>
          <button
              type="button"
              :class="buttonClass(editor?.isActive({ textAlign: 'right' }))"
              @click="toggleAlign('right')"
          >
            <UIcon name="i-lucide-align-right" class="h-4 w-4" />
          </button>
          <button
              type="button"
              :class="buttonClass(editor?.isActive({ textAlign: 'justify' }))"
              @click="toggleAlign('justify')"
          >
            <UIcon name="i-lucide-align-justify" class="h-4 w-4" />
          </button>
        </div>

        <!-- RIGHT SIDE: undo/redo -->
        <div class="flex items-center gap-1.5">
          <button
              type="button"
              :class="[buttonClass(false), !(editor && editor.can().undo()) ? 'opacity-40 cursor-not-allowed' : '']"
              :disabled="!(editor && editor.can().undo())"
              @click="editor?.chain().focus().undo().run()"
          >
            <UIcon name="i-lucide-undo-2" class="h-4 w-4" />
          </button>
          <button
              type="button"
              :class="[buttonClass(false), !(editor && editor.can().redo()) ? 'opacity-40 cursor-not-allowed' : '']"
              :disabled="!(editor && editor.can().redo())"
              @click="editor?.chain().focus().redo().run()"
          >
            <UIcon name="i-lucide-redo-2" class="h-4 w-4" />
          </button>
        </div>
      </div>

    <!-- CONTENT -->
      <EditorContent
          :editor="editor"
          class="tiptap-content bg-white prose prose-sm max-w-none min-h-[260px] px-4 py-3"
      />

    <div v-if="isNewImageOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
      <div class="w-full max-w-md rounded-lg bg-white p-4 shadow">
        <div class="flex items-center justify-between">
          <h3 class="font-semibold">Nahrát obrázek</h3>
          <button class="text-sm" @click="isNewImageOpen = false">✕</button>
        </div>

        <input class="mt-3" type="file" accept="image/*" @change="onPickFile" />

        <div class="mt-4 flex gap-2 justify-end">
          <button class="px-3 py-1 rounded border" @click="isNewImageOpen = false">Zrušit</button>
          <button class="px-3 py-1 rounded bg-primary text-white"
                  :disabled="!uploadFile || uploading"
                  @click="doUpload">
            {{ uploading ? "Nahrávám…" : "Nahrát" }}
          </button>
        </div>

        <p v-if="uploadError" class="mt-2 text-sm text-red-600">{{ uploadError }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import { Editor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Underline from '@tiptap/extension-underline'
import TextAlign from '@tiptap/extension-text-align'
import Link from '@tiptap/extension-link'
import {ApiImage} from "~/extensions/ApiImage.ts";
import { SlashCommands } from "@/extensions/SlashCommands"

export default {
  name: 'TiptapEditor',

  components: {
    EditorContent,
  },

  props: {
    modelValue: {
      type: String,
      default: '',
    },
  },

  emits: ['update:modelValue'],

  data() {
    return {
      editor: null,
      isNewImageOpen: false,
      uploadFile: null,
      uploadError: "",
      uploading: false,
    }
  },

  computed: {
    // hodnoty: 'paragraph' | 'h1' | 'h2' | 'h3'
    blockType: {
      get() {
        if (!this.editor) return 'paragraph'
        if (this.editor.isActive('heading', { level: 1 })) return 'h1'
        if (this.editor.isActive('heading', { level: 2 })) return 'h2'
        if (this.editor.isActive('heading', { level: 3 })) return 'h3'
        return 'paragraph'
      },
      set(value) {
        if (!this.editor) return
        const chain = this.editor.chain().focus()

        if (value === 'paragraph') {
          chain.setParagraph().run()
        } else if (value === 'h1') {
          chain.toggleHeading({ level: 1 }).run()
        } else if (value === 'h2') {
          chain.toggleHeading({ level: 2 }).run()
        } else if (value === 'h3') {
          chain.toggleHeading({ level: 3 }).run()
        }
      },
    },
  },

  watch: {
    modelValue(value) {
      if (!this.editor) return
      const html = value || ''
      if (this.editor.getHTML() === html) return
      this.editor.commands.setContent(html, false)
    },
  },

  mounted() {
    this.editor = new Editor({
      content: this.modelValue || '',
      extensions: [
        StarterKit,
        Underline,
        TextAlign.configure({
          types: ['heading', 'paragraph'],
        }),
        Link.configure({
          openOnClick: false,
          autolink: true,
          linkOnPaste: true,
        }),
          ApiImage,
        SlashCommands.configure({
          openNewImageModal: () => {
            this.isNewImageOpen = true
          },
        }),
      ],
      ApiImage: this.$imagesApi,
      onUpdate: ({ editor }) => {
        this.$emit('update:modelValue', editor.getHTML())
      },
    })
  },

  beforeUnmount() {
    if (this.editor) {
      this.editor.destroy()
    }
  },

  methods: {
    onPickFile(e) {
      this.uploadFile = e.target.files?.[0] ?? null
    },
    async doUpload() {
      if (!this.uploadFile || !this.editor) return
      this.uploading = true
      this.uploadError = ""
      try {
        const res = await this.$imagesApi.uploadImage({ file: this.uploadFile }) // ImageResponse
        if (!res?.filename) throw new Error("API nevrátilo filename")

        console.log(res)

        this.editor.chain().focus().insertApiImage({
          id: res.id ?? null,
          filename: res.filename,
          originalFilename: res.originalFilename ?? null,
          contentType: res.contentType ?? null,
          url: res.url ?? null,
        }).run()

        this.isNewImageOpen = false
        this.uploadFile = null
      } catch (e) {
        this.uploadError = e?.message || String(e)
      } finally {
        this.uploading = false
      }
    },
    toggleAlign(direction) {
      if (!this.editor) return
      const isActive = this.editor.isActive({ textAlign: direction })
      const chain = this.editor.chain().focus()

      if (isActive) {
        // odebere zarovnání = vrátí se na "default" stav (bez textAlign atributu)
        chain.unsetTextAlign().run()
      } else {
        chain.setTextAlign(direction).run()
      }
    },
    buttonClass(isActive) {
      const base =
          'inline-flex items-center justify-center h-8 w-8 rounded-md border text-gray-700 text-xs ' +
          'border-transparent hover:border-gray-300 hover:bg-gray-100 transition-colors'

      // výrazné aktivní tlačítko – primary background + white ikonka
      const active = ' bg-primary text-white border-primary hover:bg-primary'

      return isActive ? base + active : base
    },
    toggleLink() {
      if (!this.editor) return

      if (this.editor.isActive('link')) {
        this.editor.chain().focus().extendMarkRange('link').unsetLink().run()
        return
      }

      const raw = window.prompt('Zadejte URL odkazu')
      if (!raw) return

      const value = raw.trim()
      if (!value) return

      const normalized =
          value.startsWith('http://') ||
          value.startsWith('https://') ||
          value.startsWith('mailto:')
              ? value
              : `https://${value}`

      this.editor
          .chain()
          .focus()
          .extendMarkRange('link')
          .setLink({
            href: normalized,
            target: '_blank',
            rel: 'noopener noreferrer'
          })
          .run()
    }
  },
}
</script>

<style scoped>
/* ProseMirror root uvnitř */
.tiptap-content :deep(.ProseMirror) {
  min-height: 220px;
  outline: none;
}

.tiptap-content :deep(.ProseMirror p:last-child) {
  margin-bottom: 0;
}
</style>
