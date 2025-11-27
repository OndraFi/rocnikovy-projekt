<template>
  <div class="max-w-80 md:w-120 md:max-w-120 mx-auto bg-white p-8 rounded-xl shadow-md border border-gray-200">
    <h2 class="text-2xl font-bold mb-6 text-center">Přihlášení</h2>

    <form @submit.prevent="onSubmit" class="space-y-6">

      <!-- Identifier -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          Uživatelské jméno nebo e-mail
        </label>
        <input
            v-model="form.identifier"
            type="text"
            placeholder="např. admin nebo admin@gmail.com"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring focus:ring-blue-200 focus:border-blue-500"
        />
        <p v-if="errors.identifier" class="text-red-600 text-sm mt-1">{{ errors.identifier }}</p>
      </div>

      <!-- Password -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          Heslo
        </label>
        <input
            v-model="form.password"
            type="password"
            placeholder="••••••••••"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring focus:ring-blue-200 focus:border-blue-500"
        />
        <p v-if="errors.password" class="text-red-600 text-sm mt-1">{{ errors.password }}</p>
      </div>

      <!-- Submit -->
      <button
          type="submit"
          class="w-full bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-lg font-semibold transition-colors"
      >
        Přihlásit se
      </button>

    </form>
  </div>
</template>

<script setup lang="ts">
import type { LoginRequest } from '~~/api'; // uprav podle cesty ke tvému klientovi
import { reactive } from 'vue';

const emit = defineEmits<{
  (e: 'submit', payload: LoginRequest): void;
}>();

// formulář podle tvého datového modelu
const form = reactive<LoginRequest>({
  identifier: '',
  password: '',
});

// jednoduchá validace
const errors = reactive({
  identifier: '',
  password: '',
});

const validate = () => {
  let ok = true;
  errors.identifier = '';
  errors.password = '';

  if (!form.identifier.trim()) {
    errors.identifier = 'Vyplňte uživatelské jméno nebo e-mail.';
    ok = false;
  }

  if (!form.password.trim()) {
    errors.password = 'Zadejte heslo.';
    ok = false;
  }

  return ok;
};

const onSubmit = () => {
  if (!validate()) return;

  // emitujeme čistý LoginRequest
  emit('submit', {
    identifier: form.identifier,
    password: form.password,
  });
};
</script>

<style scoped>
</style>
