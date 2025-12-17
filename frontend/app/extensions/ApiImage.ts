import { Node, mergeAttributes } from "@tiptap/core"
import { VueNodeViewRenderer } from "@tiptap/vue-3"
import { nodeInputRule } from "@tiptap/core"
import ImageView from "@/components/imageView.vue"

export const ApiImage = Node.create({
    name: "apiImage",
    group: "block",
    atom: true,

    addAttributes() {
        return {
            id: { default: null },
            filename: { default: null },
            originalFilename: { default: null },
            contentType: { default: null },
            url: { default: null },
        }
    },

    parseHTML() {
        return [{
            tag: 'img[data-api-image="1"]',
            getAttrs: (el: any) => ({
                id: el.getAttribute("data-id") ? Number(el.getAttribute("data-id")) : null,
                filename: el.getAttribute("data-filename"),
                originalFilename: el.getAttribute("data-original-filename"),
                contentType: el.getAttribute("data-content-type"),
                url: el.getAttribute("data-url"),
            }),
        }]
    },

    renderHTML({ HTMLAttributes }) {
        return ["img", mergeAttributes(HTMLAttributes, {
            "src": "/api/images/" + HTMLAttributes.filename,
            "data-api-image": "1",
            "data-id": HTMLAttributes.id ?? "",
            "data-filename": HTMLAttributes.filename ?? "",
            "data-original-filename": HTMLAttributes.originalFilename ?? "",
            "data-content-type": HTMLAttributes.contentType ?? "",
            "data-url": HTMLAttributes.url ?? "",
        })]
    },

    addNodeView() {
        return VueNodeViewRenderer(ImageView)
    },

    addCommands() {
        return {
            insertApiImage:
                (attrs: any) =>
                    ({ chain }: any) =>
                        chain().focus().insertContent({ type: this.name, attrs }).run(),
        }
    },

    addInputRules() {
        // fix na to “skákání”: spustí se až po mezeře
        return [
            nodeInputRule({
                find: /(?:^|\s)\/image=([A-Za-z0-9._-]+)\s$/,
                type: this.type,
                getAttributes: (match) => ({ filename: match[1] }),
            }),
        ]
    },
})
