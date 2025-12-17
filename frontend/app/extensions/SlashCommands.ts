import { Extension } from "@tiptap/core"

export const SlashCommands = Extension.create({
    name: "slashCommands",

    addOptions() {
        return {
            openNewImageModal: null as null | ((ctx: { editor: any }) => void),
        }
    },

    addKeyboardShortcuts() {
        const runIfMatches = ({ editor }: any) => {
            const { state } = editor
            const { $from } = state.selection

            // text od začátku aktuálního odstavce po kurzor
            const textBefore = $from.parent.textBetween(0, $from.parentOffset, "\n", "\n")

            // chytí "... /newImage" na konci (před stiskem mezery/enteru)
            const m = textBefore.match(/(?:^|\s)\/newImage$/)
            if (!m) return false

            // smaž "/newImage" (včetně případné mezery před ním)
            const from = $from.pos - m[0].length
            const to = $from.pos

            editor.chain().focus().deleteRange({ from, to }).run()

            this.options.openNewImageModal?.({ editor })
            return true
        }

        return {
            Space: ({ editor }) => runIfMatches({ editor }),
            Enter: ({ editor }) => runIfMatches({ editor }),
        }
    },
})
