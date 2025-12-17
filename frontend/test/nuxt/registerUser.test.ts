import { beforeEach, describe, expect, it, vi } from 'vitest'
import { mount } from '@vue/test-utils'

const toastAdd = vi.hoisted(() => vi.fn())

vi.mock('#imports', () => ({
  useToast: () => ({ add: toastAdd }),
}))

import RegisterUser from '~/components/dashboard/settings/registerUser.vue'

describe('registerUser', () => {
  beforeEach(() => {
    toastAdd.mockReset()
  })

  it('validates required fields before submit', async () => {
    const register = vi.fn()
    const wrapper = mount(RegisterUser, {
      global: {
        mocks: {
          $authenticationApi: { register },
        },
        stubs: {
          UInput: true,
          UButton: true,
        },
      },
    })

    const vm = wrapper.vm as any
    await vm.onSubmit()

    expect(register).not.toHaveBeenCalled()
    expect(vm.errors.username).not.toBe('')
    expect(vm.errors.fullName).not.toBe('')
    expect(vm.errors.email).not.toBe('')
    expect(vm.errors.password).not.toBe('')
    expect(vm.errors.confirmPassword).not.toBe('')
  })

  it('submits registration and emits registered', async () => {
    const register = vi.fn().mockResolvedValue({ id: 1 })
    const wrapper = mount(RegisterUser, {
      global: {
        mocks: {
          $authenticationApi: { register },
        },
        stubs: {
          UInput: true,
          UButton: true,
        },
      },
    })

    const vm = wrapper.vm as any
    vm.form.username = 'user'
    vm.form.fullName = 'User Name'
    vm.form.email = 'user@example.com'
    vm.form.password = 'secret123'
    vm.confirmPassword = 'secret123'

    await vm.onSubmit()

    expect(register).toHaveBeenCalledWith({
      registerRequest: {
        username: 'user',
        fullName: 'User Name',
        email: 'user@example.com',
        password: 'secret123',
      },
    })
    expect(wrapper.emitted('registered')).toHaveLength(1)
    expect(vm.loading).toBe(false)
  })
})
