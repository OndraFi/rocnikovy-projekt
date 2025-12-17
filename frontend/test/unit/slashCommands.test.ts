import { describe, expect, it, vi } from 'vitest'
import { SlashCommands } from '../../app/extensions/SlashCommands'

const buildEditor = (textBefore: string, pos: number) => {
  const deleteRange = vi.fn()
  const run = vi.fn()

  const editor = {
    state: {
      selection: {
        $from: {
          parent: {
            textBetween: () => textBefore,
          },
          parentOffset: textBefore.length,
          pos,
        },
      },
    },
    chain: () => ({
      focus: () => ({
        deleteRange: (range: { from: number; to: number }) => {
          deleteRange(range)
          return { run }
        },
      }),
    }),
  }

  return { editor, deleteRange, run }
}

describe('SlashCommands extension', () => {
  it('handles /newImage and opens modal', () => {
    const openNewImageModal = vi.fn()
    const shortcuts = SlashCommands.config.addKeyboardShortcuts.call({
      options: { openNewImageModal },
    })

    const { editor, deleteRange } = buildEditor('hello /newImage', 15)
    const handled = shortcuts.Space({ editor } as any)

    expect(handled).toBe(true)
    expect(deleteRange).toHaveBeenCalledWith({ from: 5, to: 15 })
    expect(openNewImageModal).toHaveBeenCalledWith({ editor })
  })

  it('ignores other text', () => {
    const openNewImageModal = vi.fn()
    const shortcuts = SlashCommands.config.addKeyboardShortcuts.call({
      options: { openNewImageModal },
    })

    const { editor, deleteRange } = buildEditor('hello world', 11)
    const handled = shortcuts.Enter({ editor } as any)

    expect(handled).toBe(false)
    expect(deleteRange).not.toHaveBeenCalled()
    expect(openNewImageModal).not.toHaveBeenCalled()
  })
})
