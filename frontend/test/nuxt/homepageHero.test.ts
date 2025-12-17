import { describe, expect, it, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { nextTick } from 'vue'
import Hero from '~/components/homepage/hero.vue'

const flushPromises = () => new Promise((resolve) => setTimeout(resolve, 0))

describe('homepage hero', () => {
  it('loads articles and exposes main + side items', async () => {
    const articles = [
      {
        id: 1,
        title: 'Main',
        publishedAt: new Date(2025, 0, 1).toISOString(),
        author: { fullName: 'Author A' },
      },
      {
        id: 2,
        title: 'Side 1',
        publishedAt: new Date(2025, 0, 1).toISOString(),
        author: { fullName: 'Author B' },
      },
      {
        id: 3,
        title: 'Side 2',
        publishedAt: new Date(2025, 0, 1).toISOString(),
        author: { fullName: 'Author C' },
      },
    ]

    const listArticles = vi.fn().mockResolvedValue({ articles })

    const wrapper = mount(Hero, {
      global: {
        mocks: {
          $articlesApi: { listArticles },
        },
        stubs: {
          NuxtLink: true,
          UIcon: true,
          USkeleton: true,
        },
      },
    })

    await flushPromises()
    await nextTick()

    expect(listArticles).toHaveBeenCalledWith({
      pageable: { page: 0, size: 4, sort: ['publishedAt,desc'] },
    })

    const vm = wrapper.vm as any
    expect(vm.articles).toHaveLength(3)
    expect(vm.mainArticle.id).toBe(1)
    expect(vm.sideArticles).toHaveLength(2)
  })
})
