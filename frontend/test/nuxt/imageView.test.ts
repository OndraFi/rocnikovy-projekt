import { describe, expect, it, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { nextTick } from 'vue'

vi.mock('#imports', () => ({
  useRuntimeConfig: () => ({
    public: { apiBase: 'http://localhost:8080' },
  }),
}))

import ImageView from '~/components/imageView.vue'

describe('imageView', () => {
  it('loads image src from runtime config', async () => {
    vi.spyOn(console, 'log').mockImplementation(() => {})

    const wrapper = mount(ImageView, {
      props: {
        node: { attrs: { filename: 'photo.png', fileName: 'photo.png' } },
        editor: {},
        updateAttributes: vi.fn(),
      },
      global: {
        stubs: {
          NodeViewWrapper: { template: '<div><slot /></div>' },
        },
      },
    })

    await nextTick()

    const vm = wrapper.vm as any
    expect(vm.loading).toBe(false)
    expect(vm.error).toBe('')
    expect(vm.src).toBe('http://localhost:8080/api/images/photo.png')
  })
})
