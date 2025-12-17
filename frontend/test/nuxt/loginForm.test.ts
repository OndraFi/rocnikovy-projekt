import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import LoginForm from '~/components/dashboard/loginForm.vue'

describe('loginForm', () => {
  it('blocks submit when fields are empty', async () => {
    const wrapper = mount(LoginForm)

    await wrapper.find('form').trigger('submit')

    const vm = wrapper.vm as any
    expect(vm.errors.identifier).not.toBe('')
    expect(vm.errors.password).not.toBe('')
    expect(wrapper.emitted('submit')).toBeUndefined()
  })

  it('emits submit payload when valid', async () => {
    const wrapper = mount(LoginForm)
    const vm = wrapper.vm as any

    vm.form.identifier = 'user'
    vm.form.password = 'secret'

    await vm.onSubmit()

    const emitted = wrapper.emitted('submit')
    expect(emitted).toHaveLength(1)
    expect(emitted?.[0]?.[0]).toEqual({
      identifier: 'user',
      password: 'secret',
    })
  })
})
