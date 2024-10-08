import { mergeAttributes, Node } from '@tiptap/core';
import { ReactNodeViewRenderer, NodeViewWrapper } from '@tiptap/react';


export const ImageNode = Node.create({
    name: 'imageNode',
    group: 'block',
    atom: true,
    addAttributes() {
        return {
            src:{
                default: ''
            },
            name: {
                default: ''
            },
            size: {
                default: 0
            },
            file: {
                default: null
            },
            type: {
                default: null
            },
            height:{
                default: null
            },
            width:{
                default: null
            }
        }
    },
    parseHTML() {
        return [
            {
                tag: 'imageNode'
            }
        ]
    },
    renderHTML({ HTMLAttributes }) {
        return ['imageNode', mergeAttributes(HTMLAttributes)];
    },
    addNodeView() {
        return ReactNodeViewRenderer(ImageNodeComponent);
    }
});


export const ImageNodeComponent = (props) => {
    const src = props.node.attrs.src;
    const name = props.node.attrs.name;
    return (
        <NodeViewWrapper>
            <img style={{maxWidth:'100px',height:'auto'}} src={src} alt={name}/>
        </NodeViewWrapper >
    );
}