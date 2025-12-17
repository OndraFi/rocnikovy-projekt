import { beforeEach, describe, expect, it, vi } from 'vitest'
import { mount } from '@vue/test-utils'

const toastAdd = vi.hoisted(() => vi.fn())

vi.mock('#imports', () => ({
  useToast: () => ({ add: toastAdd }),
}))

import ChangePassword from '~/components/dashboard/settings/changePassword.vue'

describe('changePassword', () => {
  beforeEach(() => {
    toastAdd.mockReset()
  })

  it('validates before submit', async () => {
    const changePassword = vi.fn()
    const wrapper = mount(ChangePassword, {
      global: {
        mocks: {
          $authenticationApi: { changePassword },
        },
        stubs: {
          UInput: true,
          UButton: true,
        },
      },
    })

    const vm = wrapper.vm as any
    await vm.onSubmit()

    expect(changePassword).not.toHaveBeenCalled()
    expect(vm.errors.oldPassword).not.toBe('')
    expect(vm.errors.newPassword).not.toBe('')
    expect(vm.errors.confirmNewPassword).not.toBe('')
  })

  it('submits change password request and emits changed', async () => {
    const changePassword = vi.fn().mockResolvedValue({ message: 'ok' })
    const wrapper = mount(ChangePassword, {
      global: {
        mocks: {
          $authenticationApi: { changePassword },
        },
        stubs: {
          UInput: true,
          UButton: true,
        },
      },
    })

    const vm = wrapper.vm as any
    vm.form.oldPassword = 'oldpass'
    vm.form.newPassword = 'newpass123'
    vm.confirmNewPassword = 'newpass123'

    await vm.onSubmit()

    expect(changePassword).toHaveBeenCalledWith({
      passwordChangeRequest: {
        oldPassword: 'oldpass',
        newPassword: 'newpass123',
      },
    })
    expect(wrapper.emitted('changed')).toHaveLength(1)
    expect(vm.form.oldPassword).toBe('')
    expect(vm.form.newPassword).toBe('')
    expect(vm.confirmNewPassword).toBe('')
    expect(vm.loading).toBe(false)
  })
})
