import { mergeAttributes, Node } from '@tiptap/core';
import { ReactNodeViewRenderer, NodeViewWrapper } from '@tiptap/react';
import {CaretRightOutlined} from '@ant-design/icons'
import { Flex } from "antd"
import { formatFileSize } from '../utils'


export const VideoNode = Node.create({
    name: 'videoNode',
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
            height:{
                default: null
            },
            width:{
                default: null
            },
            duration:{
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
                tag: 'videoNode'
            }
        ]
    },
    renderHTML({ HTMLAttributes }) {
        return ['videoNode', mergeAttributes(HTMLAttributes)];
    },
    addNodeView() {
        return ReactNodeViewRenderer(VideoNodeComponent);
    }
});


export const VideoNodeComponent = (props) => {
    const name = props.node.attrs.name;
    let outlined = props.node.attrs.outlined;
    if(!outlined){
        outlined = CaretRightOutlined
    }
    let size = formatFileSize(props.node.attrs.size);
    return (
        <NodeViewWrapper>
            <Flex gap="small" style={{border:'1px solid lightgray',width:'35%',backgroundColor:'white',padding:'5px'}}>
                <CaretRightOutlined style={{color: 'gray',fontSize:'30px' }}/>
                <Flex vertical>
                    <label style={{fontSize:'12px'}}>{name}</label>
                    <label style={{fontSize:'12px'}}>{size}</label>
                </Flex>
            </Flex>
        </NodeViewWrapper >
    );
}