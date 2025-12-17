import { describe, expect, it, vi } from 'vitest'
import { mount } from '@vue/test-utils'

vi.mock('#imports', () => ({
  useRuntimeConfig: () => ({
    public: { apiBase: 'http://localhost:8080' },
  }),
}))

import HtmlRender from '~/components/htmlRender.vue'

describe('htmlRender', () => {
  it('rewrites relative api image sources', () => {
    vi.spyOn(console, 'log').mockImplementation(() => {})
    const wrapper = mount(HtmlRender, {
      props: {
        html: '<p>Hi</p><img src="/api/images/pic.png"><img src="http://cdn.test/ok.png">',
      },
    })

    const vm = wrapper.vm as any
    expect(vm.safeHtml).toContain('http://localhost:8080/api/images/pic.png')
    expect(vm.safeHtml).toContain('http://cdn.test/ok.png')
  })

  it('returns empty html when input is empty', () => {
    vi.spyOn(console, 'log').mockImplementation(() => {})
    const wrapper = mount(HtmlRender, { props: { html: '' } })

    const vm = wrapper.vm as any
    expect(vm.safeHtml).toBe('')
  })
})
