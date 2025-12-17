import { describe, expect, it, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { nextTick } from 'vue'
import Categories from '~/components/homepage/categories.vue'

const flushPromises = () => new Promise((resolve) => setTimeout(resolve, 0))

describe('homepage categories', () => {
  it('loads categories and updates paging state', async () => {
    const listCategories = vi.fn().mockResolvedValue({
      categories: [
        { id: 10, name: 'Tech', description: 'Category Desc' },
      ],
      totalPages: 1,
    })

    const wrapper = mount(Categories, {
      global: {
        mocks: {
          $categoriesApi: { listCategories },
        },
        stubs: {
          NuxtLink: true,
          UButton: true,
          UIcon: true,
          USkeleton: true,
        },
      },
    })

    await flushPromises()
    await nextTick()

    expect(listCategories).toHaveBeenCalledWith({
      pageable: { page: 1, size: 6 },
    })

    const vm = wrapper.vm as any
    expect(vm.categories).toHaveLength(1)
    expect(vm.canGetAnotherPage).toBe(false)

    await vm.getAnotherPage()
    await flushPromises()
    await nextTick()

    expect(vm.page).toBe(2)
    expect(listCategories).toHaveBeenCalledTimes(2)
  })
})
