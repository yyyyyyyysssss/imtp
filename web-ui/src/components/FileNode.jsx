import { mergeAttributes, Node } from '@tiptap/core';
import { ReactNodeViewRenderer, NodeViewWrapper } from '@tiptap/react';
import { FileOutlined } from './customIcon'
import Icon from '@ant-design/icons'
import { Flex } from "antd"
import { formatFileSize } from '../utils'

export const FieNode = Node.create({
    name: 'fileNode',
    group: 'block',
    atom: true,
    addAttributes() {
        return {
            size: {
                default: 0
            },
            name: {
                default: ''
            },
            file: {
                default: null
            },
            type: {
                default: null
            },
            outlined:{
                default: null
            }
        }
    },
    parseHTML() {
        return [
            {
                tag: 'fileNode'
            }
        ]
    },
    renderHTML({ HTMLAttributes }) {
        return ['fileNode', mergeAttributes(HTMLAttributes)];
    },
    addNodeView() {
        return ReactNodeViewRenderer(FileNodeComponent);
    }
});


export const FileNodeComponent = (props) => {
    const name = props.node.attrs.name;
    let outlined = props.node.attrs.outlined;
    if(!outlined){
        outlined = FileOutlined
    }
    let size = formatFileSize(props.node.attrs.size);
    return (
        <NodeViewWrapper>
            <Flex gap="small" style={{border:'1px solid lightgray',width:'35%',backgroundColor:'white',padding:'5px'}}>
                <Icon component={outlined} style={{color: 'gray',fontSize:'30px' }} />
                <Flex vertical>
                    <label style={{fontSize:'12px'}}>{name}</label>
                    <label style={{fontSize:'12px'}}>{size}</label>
                </Flex>
            </Flex>
        </NodeViewWrapper >
    );
}