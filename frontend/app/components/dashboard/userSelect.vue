<template>
  <div class="space-y-1">
    <USelect
        v-model="selectedId"
        :items="userOptions"
        :placeholder="placeholder"
        class="w-full"
        :disabled="disabled || loading"
    />

    <p v-if="error" class="text-xs text-error mt-1">
      {{ error }}
    </p>
  </div>
</template>

<script lang="ts">
import type {
  ListUsersRequest,
  PaginatedUserResponse,
  UserResponse
} from '~~/api';
import { useToast } from '#imports';

export default {
  name: 'UserSelect',

  props: {
    modelValue: {
      type: Object as () => UserResponse | null,
      default: null
    },
    label: {
      type: String,
      default: 'Uživatel'
    },
    placeholder: {
      type: String,
      default: 'Vyberte uživatele'
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },

  emits: ['update:modelValue', 'selected'],

  setup() {
    const toast = useToast();
    return { toast };
  },

  data() {
    return {
      loading: false,
      error: '',
      users: [] as UserResponse[]
    };
  },

  computed: {
    // v-model proxy: USelect pracuje s ID, parent s celým userem
    selectedId: {
      get(): number | null {
        return this.modelValue && (this.modelValue as any).id != null
            ? (this.modelValue as any).id
            : null;
      },
      set(val: number | null) {
        const selected =
            this.users.find((u: any) => u.id === val) || null;
        this.$emit('update:modelValue', selected);
        this.$emit('selected', selected);
      }
    },

    userOptions(): { label: string; value: number }[] {
      return this.users.map((u: any) => ({
        value: u.id as number,
        label: u.fullName || u.username || `Uživatel #${u.id}`
      }));
    }
  },

  mounted() {
    this.fetchUsers();
  },

  methods: {
    async fetchUsers() {
      this.loading = true;
      this.error = '';

      const request: ListUsersRequest = {
        pageable: {
          page: 0,
          size: 1000,
          sort: ['fullName,asc']
        }
      };

      try {
        const res: PaginatedUserResponse = await (this as any).$usersApi.listUsers(
            request
        );
        this.users = res.users || [];
      } catch (e: any) {
        console.error(e);
        this.error = 'Nepodařilo se načíst uživatele.';
        this.toast.add({
          title:
              e?.response?.data?.message ||
              e?.message ||
              'Nepodařilo se načíst uživatele',
          color: 'error'
        });
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>
