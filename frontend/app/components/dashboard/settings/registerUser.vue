<template>
  <div class="bg-white rounded-2xl shadow-sm border border-gray-200 p-8">
    <h2 class="text-2xl font-bold mb-6">Registrace uživatele</h2>

    <form @submit.prevent="onSubmit" class="space-y-5">
      <!-- username -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          Uživatelské jméno
        </label>
        <UInput
            v-model="form.username"
            placeholder="např. admin"
            autocomplete="off"
            class="w-full"
        />
        <p v-if="errors.username" class="text-xs text-error mt-1">
          {{ errors.username }}
        </p>
      </div>

      <!-- full name -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          Celé jméno
        </label>
        <UInput
            v-model="form.fullName"
            placeholder="např. Jan Novák"
            autocomplete="off"
            class="w-full"
        />
        <p v-if="errors.fullName" class="text-xs text-error mt-1">
          {{ errors.fullName }}
        </p>
      </div>

      <!-- email -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          E-mail
        </label>
        <UInput
            v-model="form.email"
            type="email"
            placeholder="např. uzivatel@example.com"
            autocomplete="off"
            class="w-full"
        />
        <p v-if="errors.email" class="text-xs text-error mt-1">
          {{ errors.email }}
        </p>
      </div>

      <!-- password -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          Heslo
        </label>
        <UInput
            v-model="form.password"
            type="password"
            placeholder="Zadejte heslo"
            autocomplete="new-password"
            class="w-full"
        />
        <p v-if="errors.password" class="text-xs text-error mt-1">
          {{ errors.password }}
        </p>
      </div>

      <!-- confirm password -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          Potvrzení hesla
        </label>
        <UInput
            v-model="confirmPassword"
            type="password"
            placeholder="Zadejte heslo znovu"
            autocomplete="new-password"
            class="w-full"
        />
        <p v-if="errors.confirmPassword" class="text-xs text-error mt-1">
          {{ errors.confirmPassword }}
        </p>
      </div>

      <div class="pt-2 flex justify-end">
        <UButton
            type="submit"
            color="primary"
            :loading="loading"
        >
          Registrovat
        </UButton>
      </div>
    </form>
  </div>
</template>

<script lang="ts">
import type {
  RegisterRequest,
  RegisterOperationRequest,
  UserResponse
} from '~~/api';


export default {
  name: 'RegisterUserForm',

  emits: ['registered'],
  setup(){
  },
  data() {
    return {
      toast:  useToast(),
      form: {
        username: '',
        fullName: '',
        email: '',
        password: ''
      } as RegisterRequest,
      confirmPassword: '',
      loading: false,
      errors: {
        username: '',
        fullName: '',
        email: '',
        password: '',
        confirmPassword: ''
      } as Record<string, string>
    };
  },

  methods: {
    resetErrors() {
      this.errors = {
        username: '',
        fullName: '',
        email: '',
        password: '',
        confirmPassword: ''
      };
    },

    validate(): boolean {
      let valid = true;
      this.resetErrors();

      if (!this.form.username.trim()) {
        this.errors.username = 'Vyplňte uživatelské jméno.';
        valid = false;
      }

      if (!this.form.fullName.trim()) {
        this.errors.fullName = 'Vyplňte celé jméno.';
        valid = false;
      }

      if (!this.form.email.trim()) {
        this.errors.email = 'Vyplňte e-mail.';
        valid = false;
      } else if (!/^\S+@\S+\.\S+$/.test(this.form.email)) {
        this.errors.email = 'Zadejte platný e-mail.';
        valid = false;
      }

      if (!this.form.password.trim()) {
        this.errors.password = 'Zadejte heslo.';
        valid = false;
      } else if (this.form.password.length < 6) {
        this.errors.password = 'Heslo musí mít alespoň 6 znaků.';
        valid = false;
      }

      if (!this.confirmPassword.trim()) {
        this.errors.confirmPassword = 'Potvrďte heslo.';
        valid = false;
      } else if (this.confirmPassword !== this.form.password) {
        this.errors.confirmPassword = 'Hesla se neshodují.';
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

      const request: RegisterOperationRequest = {
        registerRequest: { ...this.form }
      };

      try {
        const res: UserResponse = await this.$authenticationApi.register(
            request
        );

        this.toast.add({
          title: 'Uživatel byl úspěšně zaregistrován',
          color: 'success'
        });

        this.$emit('registered', res);

      } catch (e: any) {
        console.error(e);

        this.toast.add({
          title:
              e?.response?.data?.message ||
              e?.message ||
              'Registrace se nezdařila',
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
