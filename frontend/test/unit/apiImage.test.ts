import { describe, expect, it, vi } from 'vitest'

vi.mock('@/components/imageView.vue', () => ({
  default: {},
}))

import { ApiImage } from '../../app/extensions/ApiImage'

describe('ApiImage extension', () => {
  it('parses attributes from HTML dataset', () => {
    const [{ getAttrs }] = ApiImage.config.parseHTML()

    const attrs = getAttrs({
      getAttribute: (name: string) => {
        const map: Record<string, string | null> = {
          'data-id': '12',
          'data-filename': 'photo.png',
          'data-original-filename': 'photo-original.png',
          'data-content-type': 'image/png',
          'data-url': 'http://example.test/photo.png',
        }
        return map[name] ?? null
      },
    })

    expect(attrs).toEqual({
      id: 12,
      filename: 'photo.png',
      originalFilename: 'photo-original.png',
      contentType: 'image/png',
      url: 'http://example.test/photo.png',
    })
  })

  it('renders an image tag with api attributes', () => {
    const [tag, attrs] = ApiImage.config.renderHTML({
      HTMLAttributes: {
        id: 7,
        filename: 'hero.jpg',
        originalFilename: 'hero-original.jpg',
        contentType: 'image/jpeg',
        url: 'http://example.test/hero.jpg',
      },
    })

    expect(tag).toBe('img')
    expect(attrs).toMatchObject({
      src: '/api/images/hero.jpg',
      'data-api-image': '1',
      'data-id': 7,
      'data-filename': 'hero.jpg',
      'data-original-filename': 'hero-original.jpg',
      'data-content-type': 'image/jpeg',
      'data-url': 'http://example.test/hero.jpg',
    })
  })
})
