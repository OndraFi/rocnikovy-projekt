<template>
  <div class="bg-white rounded-2xl shadow-sm border border-gray-200 p-8">
    <h2 class="text-2xl font-bold mb-6">Změna hesla</h2>

    <form @submit.prevent="onSubmit" class="space-y-5">
      <!-- staré heslo -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          Staré heslo
        </label>
        <UInput
            v-model="form.oldPassword"
            type="password"
            placeholder="Zadejte stávající heslo"
            autocomplete="current-password"
            class="w-full"
        />
        <p v-if="errors.oldPassword" class="text-xs text-error mt-1">
          {{ errors.oldPassword }}
        </p>
      </div>

      <!-- nové heslo -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          Nové heslo
        </label>
        <UInput
            v-model="form.newPassword"
            type="password"
            placeholder="Zadejte nové heslo"
            autocomplete="new-password"
            class="w-full"
        />
        <p v-if="errors.newPassword" class="text-xs text-error mt-1">
          {{ errors.newPassword }}
        </p>
      </div>

      <!-- potvrzení nového hesla -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          Potvrzení nového hesla
        </label>
        <UInput
            v-model="confirmNewPassword"
            type="password"
            placeholder="Zadejte nové heslo znovu"
            autocomplete="new-password"
            class="w-full"
        />
        <p v-if="errors.confirmNewPassword" class="text-xs text-error mt-1">
          {{ errors.confirmNewPassword }}
        </p>
      </div>

      <div class="pt-2 flex justify-end">
        <UButton
            type="submit"
            color="primary"
            :loading="loading"
        >
          Změnit heslo
        </UButton>
      </div>
    </form>
  </div>
</template>

<script lang="ts">
import type {
  ChangePasswordRequest,
  PasswordChangeRequest,
  MessageResponse
} from '~~/api';

export default {
  name: 'ChangePasswordForm',

  emits: ['changed'],

  setup() {
    const toast = useToast();
    return { toast };
  },

  data() {
    return {
      form: {
        oldPassword: '',
        newPassword: ''
      } as PasswordChangeRequest,
      confirmNewPassword: '',
      loading: false,
      errors: {
        oldPassword: '',
        newPassword: '',
        confirmNewPassword: ''
      } as Record<string, string>
    };
  },

  methods: {
    resetErrors() {
      this.errors = {
        oldPassword: '',
        newPassword: '',
        confirmNewPassword: ''
      };
    },

    validate(): boolean {
      let valid = true;
      this.resetErrors();

      if (!this.form.oldPassword.trim()) {
        this.errors.oldPassword = 'Zadejte stávající heslo.';
        valid = false;
      }

      if (!this.form.newPassword.trim()) {
        this.errors.newPassword = 'Zadejte nové heslo.';
        valid = false;
      } else if (this.form.newPassword.length < 6) {
        this.errors.newPassword = 'Nové heslo musí mít alespoň 6 znaků.';
        valid = false;
      }

      if (this.form.newPassword && this.form.oldPassword && this.form.newPassword === this.form.oldPassword) {
        this.errors.newPassword = 'Nové heslo nesmí být stejné jako staré.';
        valid = false;
      }

      if (!this.confirmNewPassword.trim()) {
        this.errors.confirmNewPassword = 'Potvrďte nové heslo.';
        valid = false;
      } else if (this.confirmNewPassword !== this.form.newPassword) {
        this.errors.confirmNewPassword = 'Nová hesla se neshodují.';
        valid = false;
      }

      if (!valid) {
        this.toast.add({
          title: 'Formulář obsahuje chyby',
          color: 'error'
        });
      }

      return valid;
    },

    async onSubmit() {
      if (!this.validate()) return;

      this.loading = true;

      const request: ChangePasswordRequest = {
        passwordChangeRequest: {
          oldPassword: this.form.oldPassword,
          newPassword: this.form.newPassword
        }
      };

      try {
        const res: MessageResponse = await (this as any).$authenticationApi.changePassword(
            request
        );

        this.toast.add({
          title: res?.message || 'Heslo bylo úspěšně změněno',
          color: 'success'
        });

        this.$emit('changed', res);

        // případný reset
        this.form.oldPassword = '';
        this.form.newPassword = '';
        this.confirmNewPassword = '';
      } catch (e: any) {
        console.error(e);

        this.toast.add({
          title:
              e?.response?.data?.message ||
              e?.message ||
              'Změna hesla se nezdařila',
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
