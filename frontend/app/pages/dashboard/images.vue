<template>
  <NuxtLayout>
    <div class="flex flex-wrap items-center justify-between gap-4 mb-6">
      <div>
        <h1 class="text-2xl font-semibold">Obrázky</h1>
        <p class="text-sm text-gray-500">Správa nahraných obrázků.</p>
      </div>
      <div class="flex flex-wrap items-center gap-3">
        <input
          ref="fileInput"
          type="file"
          accept="image/*"
          class="hidden"
          @change="onFileChange"
        />
        <UButton
          variant="outline"
          icon="i-lucide-upload"
          @click="triggerFileSelect"
        >
          Vybrat soubor
        </UButton>
        <UButton
          color="primary"
          icon="i-lucide-cloud-upload"
          :disabled="!selectedFile || uploading"
          :loading="uploading"
          @click="uploadSelected"
        >
          Nahrát
        </UButton>
        <UButton
          variant="outline"
          icon="i-lucide-refresh-cw"
          :loading="fetching"
          @click="getImages"
        >
          Obnovit
        </UButton>
      </div>
    </div>

    <p v-if="selectedFile" class="text-sm text-gray-600 mb-4">
      Vybraný soubor: <span class="font-medium">{{ selectedFile.name }}</span>
    </p>

    <p v-if="error" class="text-sm text-red-600 mb-4">
      {{ error }}
    </p>

    <div v-if="fetching && images.length === 0" class="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
      <div
        v-for="n in 8"
        :key="n"
        class="rounded-xl border border-gray-200 overflow-hidden bg-white"
      >
        <USkeleton class="h-36 w-full" />
        <div class="p-3 space-y-2">
          <USkeleton class="h-4 w-3/4" />
          <USkeleton class="h-3 w-1/2" />
        </div>
      </div>
    </div>

    <div
      v-else-if="images.length === 0"
      class="py-12 text-center text-sm text-gray-500"
    >
      Žádné obrázky nebyly nalezeny.
    </div>

    <div v-else class="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
      <div
        v-for="(image, index) in images"
        :key="imageKey(image, index)"
        class="rounded-xl border border-gray-200 bg-white overflow-hidden flex flex-col"
      >
        <div class="relative bg-gray-50">
          <img
            v-if="imageSrc(image)"
            :src="imageSrc(image)"
            :alt="image.originalFilename || image.filename || 'image'"
            class="h-36 w-full object-cover"
            loading="lazy"
          />
          <div v-else class="h-36 flex items-center justify-center text-xs text-gray-400">
            Bez náhledu
          </div>
        </div>
        <div class="p-3 flex flex-col gap-2">
          <div>
            <p class="text-sm font-medium truncate">
              {{ image.originalFilename || image.filename || 'Neznámý název' }}
            </p>
            <div class="text-xs text-gray-500 flex flex-wrap gap-x-2">
              <span>{{ image.contentType || 'neznámý typ' }}</span>
              <span v-if="image.fileSize">• {{ formatFileSize(image.fileSize) }}</span>
            </div>
          </div>
          <div class="flex items-center justify-between gap-2">
            <span class="text-[11px] text-gray-400 truncate">
              {{ image.filename || image.url || 'bez názvu souboru' }}
            </span>
            <UButton
              v-if="image.filename"
              size="xs"
              color="neutral"
              variant="outline"
              icon="i-lucide-copy"
              @click="copyFilename(image.filename)"
            >
              Kopírovat
            </UButton>
            <UButton
              size="xs"
              color="error"
              variant="outline"
              icon="i-lucide-trash-2"
              :loading="deleting[imageKey(image, index)]"
              :disabled="deleting[imageKey(image, index)]"
              @click="deleteImage(image, index)"
            >
              Smazat
            </UButton>
          </div>
          <p v-if="image.uploadedAt" class="text-[11px] text-gray-400">
            Nahráno: {{ formatUploadedAt(image.uploadedAt) }}
          </p>
        </div>
      </div>
    </div>

    <div class="flex justify-end border-t border-default pt-4 px-4 mt-6">
      <UPagination
        :page="page"
        :items-per-page="size"
        :total="paginationTotal"
        @update:page="onPageChange"
      />
    </div>
  </NuxtLayout>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import type { ImageResponse } from '~~/api'

export default defineComponent({
  name: 'ImagesPage',
  data() {
    return {
      images: [] as ImageResponse[],
      fetching: false,
      deleting: {} as Record<string, boolean>,
      selectedFile: null as File | null,
      uploading: false,
      page: 0,
      size: 12,
      totalPages: 0,
      totalElements: 0,
      error: ''
    }
  },

  computed: {
    paginationTotal(): number {
      if (this.totalElements) return this.totalElements
      return this.totalPages * this.size
    }
  },

  setup() {
    definePageMeta({
      layout: 'dashboard'
    })

    const config = useRuntimeConfig()
    const toast = useToast()

    return { config, toast }
  },

  methods: {
    imageKey(image: ImageResponse, index?: number): string {
      return String(image.id ?? image.filename ?? image.url ?? index ?? '')
    },

    imageSrc(image: ImageResponse): string {
      const apiBase = (this.config?.public?.apiBase ?? '').replace(/\/$/, '')

      if (image.filename) {
        return `${apiBase}/api/images/${image.filename}`
      }

      return ''
    },

    formatFileSize(size: number) {
      if (size < 1024) return `${size} B`
      if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`
      return `${(size / (1024 * 1024)).toFixed(1)} MB`
    },

    formatUploadedAt(dateValue: Date | string) {
      const date = dateValue instanceof Date ? dateValue : new Date(dateValue)
      return date.toLocaleString('cs-CZ', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    },

    onPageChange(p: number) {
      this.page = p
      this.getImages()
    },

    async getImages() {
      this.fetching = true
      this.error = ''

      const request = {
        pageable: {
          page: this.page,
          size: this.size
        }
      }

      try {
        const response = await this.$imagesApi.listImages(request);
        console.log(response)
        const list =
          response?.images

        this.images = Array.isArray(list) ? list : []
        if (typeof response?.page === 'number') this.page = response.page
        if (typeof response?.totalPages === 'number')
          this.totalPages = response.totalPages
        if (typeof response?.totalElements === 'number')
          this.totalElements = response.totalElements
        if (typeof response?.totalItems === 'number')
          this.totalElements = response.totalItems
        if (typeof response?.pageable?.pageNumber === 'number')
          this.page = response.pageable.pageNumber
        if (typeof response?.pageable?.pageSize === 'number')
          this.size = response.pageable.pageSize
      } catch (err: any) {
        console.error(err?.message || err)
        this.error = 'Nepodařilo se načíst obrázky.'
      } finally {
        this.fetching = false
      }
    },

    async deleteImage(image: ImageResponse, index?: number) {
      if (!image.filename) return

      const key = this.imageKey(image, index)
      if (this.deleting[key]) return

      this.deleting[key] = true

      try {
        await (this as any).$imagesApi.deleteImage({
          fileName: image.filename
        })

        this.images = this.images.filter(
          (item, idx) => this.imageKey(item, idx) !== key
        )
        if (this.totalElements > 0) this.totalElements -= 1

        this.toast.add({
          title: 'Obrázek smazán',
          color: 'success'
        })
      } catch (err: any) {
        console.error(err?.message || err)
        this.toast.add({
          title: err?.response?.data?.message || 'Smazání obrázku se nezdařilo',
          color: 'error'
        })
      } finally {
        this.deleting[key] = false
      }
    },

    triggerFileSelect() {
      const input = this.$refs.fileInput as HTMLInputElement | undefined
      input?.click()
    },

    onFileChange(event: Event) {
      const target = event.target as HTMLInputElement
      this.selectedFile = target.files?.[0] ?? null
    },

    async uploadSelected() {
      if (!this.selectedFile) return

      this.uploading = true

      try {
        await (this as any).$imagesApi.uploadImage({
          file: this.selectedFile
        })

        this.toast.add({
          title: 'Obrázek nahrán',
          color: 'success'
        })

        this.selectedFile = null
        const input = this.$refs.fileInput as HTMLInputElement | undefined
        if (input) input.value = ''

        await this.getImages()
      } catch (err: any) {
        console.error(err?.message || err)
        this.toast.add({
          title: err?.response?.data?.message || 'Nahrání obrázku se nezdařilo',
          color: 'error'
        })
      } finally {
        this.uploading = false
      }
    },

    async copyFilename(filename: string) {
      try {
        await navigator.clipboard.writeText(filename)
        this.toast.add({
          title: 'Název souboru zkopírován',
          color: 'success'
        })
      } catch (err: any) {
        console.error(err?.message || err)
        this.toast.add({
          title: 'Kopírování se nezdařilo',
          color: 'error'
        })
      }
    }
  },

  created() {
    this.getImages()
  }
})
</script>
