<template>
  <div class="space-y-1">
    <label v-if="label" class="block text-sm font-medium text-gray-700">
      {{ label }}
    </label>

    <USelectMenu
        v-model="selectedIds"
        :items="items"
        multiple
        value-key="value"
        :placeholder="placeholder"
        class="w-full"
        :loading="loading"
        :disabled="disabled || loading"
    />

    <p v-if="error" class="text-xs text-error mt-1">
      {{ error }}
    </p>
  </div>
</template>

<script lang="ts">
import { useToast } from '#imports';
import type {
  ListCategoriesRequest,
  PaginatedCategoryResponse,
  CategoryResponse
} from '~~/api';

export default {
  name: 'CategoryMultiSelect',

  props: {
    // v-model: aktuálně vybraná pole IDček kategorií
    modelValue: {
      type: Array as () => number[],
      default: () => []
    },
    // optional preselect – např. při editaci
    preselectedIds: {
      type: Array as () => number[] | null,
      default: null
    },
    label: {
      type: String,
      default: 'Kategorie'
    },
    placeholder: {
      type: String,
      default: 'Vyberte kategorie'
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },

  emits: ['update:modelValue', 'change'],

  setup() {
    const toast = useToast();
    return { toast };
  },

  data() {
    return {
      loading: false,
      error: '',
      categories: [] as CategoryResponse[]
    };
  },

  computed: {
    // proxy pro v-model – žádné watchery, žádný local state, žádná rekurze
    selectedIds: {
      get(): number[] {
        if (this.modelValue && this.modelValue.length > 0) {
          return this.modelValue;
        }
        if (this.preselectedIds && this.preselectedIds.length > 0) {
          return this.preselectedIds;
        }
        return [];
      },
      set(val: number[]) {
        this.$emit('update:modelValue', val);
        this.$emit('change', val);
      }
    },

    items(): { label: string; value: number }[] {
      return this.categories.map((c: any) => ({
        value: c.id,
        label: c.name || `Kategorie #${c.id}`
      }));
    }
  },

  mounted() {
    this.fetchCategories();
  },

  methods: {
    async fetchCategories() {
      this.loading = true;
      this.error = '';

      const request: ListCategoriesRequest = {
        pageable: {
          page: 0,
          size: 200,
          sort: ['name,asc']
        }
      };

      try {
        const res: PaginatedCategoryResponse = await (this as any).$categoriesApi.listCategories(
            request
        );

        const list = res.categories ?? [];
        this.categories = list;
      } catch (e: any) {
        console.error(e);
        this.error = 'Nepodařilo se načíst kategorie.';
        this.toast.add({
          title:
              e?.response?.data?.message ||
              e?.message ||
              'Nepodařilo se načíst kategorie',
          color: 'error'
        });
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
</style>
