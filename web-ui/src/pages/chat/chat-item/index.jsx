import React, { useState, useRef, useEffect, useCallback, useContext } from 'react'
import './index.less'
import Picker from '@emoji-mart/react'
import { Flex, Layout, Button } from "antd"
import { List as VirtualizedList, AutoSizer, CellMeasurer, CellMeasurerCache } from 'react-virtualized'
import { formatFileSize, getVideoDimensionsOfByFile, dataURLtoFile, createThumbnail } from '../../../utils'
import { FieNode } from '../../../components/FileNode'
import { VideoNode } from '../../../components/VideoNode'
import { ImageNode } from '../../../components/ImageNode';
import { v4 as uuidv4 } from 'uuid';
import emoteImg from '../../../assets/img/emote_icon.png'
import { EditorContent, useEditor } from '@tiptap/react'
import HardBreak from '@tiptap/extension-hard-break'
import { StarterKit } from '@tiptap/starter-kit';
import emojiMartData from '@emoji-mart/data'
import Uploader from '../../../components/Uploader';
import { HomeContext, useWebSocket } from '../../../context';
import Message from '../../../components/message';
import IdGen from '../../../utils/IdGen';
import { useDispatch, useSelector } from 'react-redux';
import { fetchMessageByUserSessionId } from '../../../api/ApiService';
import { loadMessage, addMessage, updateMessage, updateMessageStatus } from '../../../redux/slices/chatSlice';
import { MessageStatus, MessageType } from '../../../enum';

const { Content } = Layout;

const ChatItem = ({ sessionId, selectTab }) => {
    const { socket } = useWebSocket();
    const socketRef = useRef();
    useEffect(() => {
        socketRef.current = socket;
    }, [socket]);


    const { userInfo } = useContext(HomeContext);

    const session = useSelector(state => state.chat.entities.sessions[sessionId])

    const { messages } = session

    const dispatch = useDispatch()

    useEffect(() => {
        const fetchData = async () => {
            const data = await fetchMessageByUserSessionId(sessionId)
            const messageList = data.list
            const newMessageList = messageList.map(item => {
                item.self = userInfo.id === item.senderUserId
                return item
            })
            dispatch(loadMessage({ sessionId: sessionId, messages: newMessageList }))
        }
        if (sessionId === selectTab) {
            if (session.messageInit === undefined || session.messageInit === false) {
                fetchData()
            }
        }
    }, [sessionId, selectTab])

    //聊天内容
    const chatContentRef = useRef(null);
    //监听数据变化并滚动到末尾
    useEffect(() => {
        if (chatContentRef.current && messages) {
            requestAnimationFrame(() => {
                chatContentRef.current.scrollToRow(messages.length - 1);
            });
        }
    }, [messages]);
    //表情框显示与隐藏
    const [emojiHide, setEmojiHide] = useState(true);
    const emojiRef = useRef(null);
    //文件发送
    const uploaderRef = useRef(null);
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (emojiRef.current && !emojiRef.current.contains(event.target)) {
                setEmojiHide(true);
            }
        }
        // 绑定事件监听器
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            // 组件卸载时移除事件监听器
            document.removeEventListener('mousedown', handleClickOutside);
        }
    }, []);
    //文本编辑
    const editor = useEditor({
        extensions: [
            StarterKit.configure({ hardBreak: false }),
            FieNode,
            VideoNode,
            ImageNode,
            HardBreak.extend({
                addKeyboardShortcuts() {
                    return {
                        'Shift-Enter': () => this.editor.commands.setHardBreak(),
                        Enter: () => {
                            sendMessage();
                            return true;
                        }
                    }
                }
            })
        ],
        editorProps: {
            handlePaste: (view, event, slice) => {
                const items = Array.from(event.clipboardData?.items || []);
                const conut = items.length
                if (conut === 0) {
                    return true;
                }
                const fileItems = items.filter(f => f.kind === 'file');
                let tmpIndex = 0;
                const fileContent = [];
                const processItem = (item) => {
                    if (item.kind === 'file') {
                        const file = item.getAsFile();
                        const fileType = item.type;
                        const fileSizeInMB = file.size / (1024 * 1024);
                        console.log(`文件名称：${file.name} 文件大小：${fileSizeInMB.toFixed(2)} MB`);
                        if (fileType.startsWith("image/")) {
                            const reader = new FileReader();
                            reader.onload = (loadEvent) => {
                                const img = document.createElement('img');
                                //读取图片以便于自适应聊天列表的宽高
                                img.onload = () => {
                                    fileContent.push({
                                        type: 'imageNode',
                                        attrs: {
                                            src: loadEvent.target.result,
                                            name: file.name,
                                            size: file.size,
                                            file: file,
                                            type: file.type,
                                            height: img.height,
                                            width: img.width
                                        }
                                    });
                                    tmpIndex += 1;
                                    if (fileItems.length === tmpIndex) {
                                        editor.chain().focus().insertContent(fileContent).run();
                                        editor.commands.createParagraphNear();
                                        // editor.commands.setTextSelection(editor.state.selection.to);
                                    }
                                }
                                img.src = loadEvent.target.result;
                            }
                            reader.readAsDataURL(file);
                        } else if (fileType.startsWith("video/")) {
                            getVideoDimensionsOfByFile(file)
                                .then(
                                    ({ height, width, duration }) => {
                                        fileContent.push({
                                            type: 'videoNode',
                                            attrs: {
                                                size: file.size,
                                                name: file.name,
                                                type: file.type,
                                                height: height,
                                                width: width,
                                                duration: duration,
                                                file: file
                                            }
                                        });
                                        tmpIndex += 1;
                                        if (fileItems.length === tmpIndex) {
                                            editor.chain().focus().insertContent(fileContent).run();
                                            editor.commands.createParagraphNear();
                                        }
                                    }
                                );
                        } else {
                            fileContent.push({
                                type: 'fileNode',
                                attrs: {
                                    size: file.size,
                                    name: file.name,
                                    type: file.type,
                                    file: file
                                }
                            });
                            tmpIndex += 1;
                            if (fileItems.length === tmpIndex) {
                                editor.chain().focus().insertContent(fileContent).run();
                                editor.commands.createParagraphNear();
                            }
                        }

                    }
                }
                items.forEach(item => processItem(item));
                return false;
            }
        },
        content: '',
    });
    //表情包选中
    const emojiSelectHandler = (emoji) => {
        editor.chain().focus().insertContent(emoji.native).run();
        setEmojiHide(true);
    }
    //显示表情框
    const showEmojiFrame = () => {
        setEmojiHide(false);
    }
    //上传前置处理
    const beforeUpload = async (file) => {

        return true;
    }
    //自定义上传
    const customRequest = (e) => {
        const { file } = e;
        let items = [];
        if (file.type.startsWith("image/")) {
            const reader = new FileReader();
            reader.onload = (loadEvent) => {
                const img = document.createElement('img');
                //读取图片以便于自适应聊天列表的宽高
                img.onload = () => {
                    items.push({
                        type: 'imageNode',
                        attrs: {
                            src: loadEvent.target.result,
                            name: file.name,
                            size: file.size,
                            file: file,
                            type: file.type,
                            height: img.height,
                            width: img.width
                        }
                    });
                    sendMessage(items);
                }
                img.src = loadEvent.target.result;
            }
            reader.readAsDataURL(file);
        } else if (file.type.startsWith("video/")) {
            getVideoDimensionsOfByFile(file)
                .then(
                    ({ height, width, duration }) => {
                        items.push({
                            type: 'videoNode',
                            attrs: {
                                size: file.size,
                                name: file.name,
                                height: height,
                                width: width,
                                type: file.type,
                                duration: duration,
                                file: file
                            }
                        });
                        sendMessage(items);
                    }
                );
        } else {
            items.push({
                type: 'fileNode',
                attrs: {
                    size: file.size,
                    name: file.name,
                    type: file.type,
                    file: file
                }
            });
            sendMessage(items);
        }
    }
    const uploadProps = {
        showUploadList: false,
        beforeUpload: beforeUpload,
        customRequest: customRequest
    }
    const uploadFile = async (file) => {
        return uploaderRef.current.uploadFile(file);
    }

    const cache = React.useRef(
        new CellMeasurerCache({
            fixedWidth: true
        })
    );
    //聊天内容显示
    const rowRenderer = useCallback(({ index, key, parent, style }) => {
        const item = messages[index]
        return (
            <CellMeasurer
                key={key}
                cache={cache.current}
                columnCount={1}
                columnIndex={0}
                rowIndex={index}
                parent={parent}
            >
                {({ registerChild }) => (
                    <div ref={registerChild} key={item} className='chat-item' style={style}>
                        {renderItem(item, index)}
                    </div>
                )}

            </CellMeasurer>
        );
    }, [messages]);
    //聊天项渲染函数
    const renderItem = (item, index) => {
        if (!item) {
            return (<></>);
        }
        return (
            <Message messageId={item} />
        )
    };
    //列表项渲染回调函数
    const handleRowsRendered = ({ startIndex, stopIndex }) => {

    }
    //发送消息
    const sendMessage = (message) => {
        let editorFlag = false;
        if (!message) {
            const json = editor.getJSON();
            const { content } = json;
            message = content;
            editorFlag = true;
        }
        if (!message || message.length === 0) {
            return;
        }
        if (message.length === 1 && message[0].type === 'paragraph' && !message[0].content) {
            return;
        }
        for (const item of message) {
            let msg;
            switch (item.type) {
                case 'imageNode':
                    const dataUrl = item.attrs.src;
                    const imageName = item.attrs.name;
                    const imageFile = item.attrs.file;
                    const imageSize = item.attrs.size;
                    const imageHeight = item.attrs.height;
                    const imageWidth = item.attrs.width;
                    const imageSizeDesc = formatFileSize(imageSize);
                    const imageType = imageFile.type;
                    const imageMsg = messageBase(dataUrl, MessageType.IMAGE_MESSAGE);
                    msg = {
                        ...imageMsg,
                        contentMetadata: {
                            name: imageName,
                            width: imageWidth,
                            height: imageHeight,
                            mediaType: imageType,
                            size: imageSize,
                            sizeDesc: imageSizeDesc
                        }
                    }
                    //添加消息
                    dispatch(addMessage({ sessionId: sessionId, message: msg }))
                    uploadFile(imageFile)
                        .then(
                            (url) => {
                                const newMsg = { ...msg, status: MessageStatus.SENT, content: url }
                                dispatch(updateMessage({ message: newMsg }))
                                //向服务器发送消息
                                realSendMessage(newMsg)
                            },
                            (error) => {
                                dispatch(updateMessageStatus({ id: msg.id, newStatus: MessageStatus.FAILED }))
                            }
                        )
                    break;
                case 'fileNode':
                    const fileName = item.attrs.name;
                    const fileSize = item.attrs.size;
                    const fileSizeDesc = formatFileSize(fileSize);
                    const fileType = item.attrs.type;
                    const file = item.attrs.file;
                    const fileMsg = messageBase('', MessageType.FILE_MESSAGE);
                    msg = {
                        ...fileMsg,
                        contentMetadata: {
                            name: fileName,
                            mediaType: fileType,
                            size: fileSize,
                            sizeDesc: fileSizeDesc
                        }
                    }
                    //添加消息
                    dispatch(addMessage({ sessionId: sessionId, message: msg }))
                    uploadFile(file)
                        .then(
                            (url) => {
                                const newMsg = { ...msg, status: MessageStatus.SENT, content: url }
                                dispatch(updateMessage({ message: newMsg }))
                                //向服务器发送消息
                                realSendMessage(newMsg)
                            },
                            (error) => {
                                dispatch(updateMessageStatus({ id: msg.id, newStatus: MessageStatus.FAILED }))
                            }
                        )
                    break;
                case 'videoNode':
                    const videoName = item.attrs.name;
                    const videoSize = item.attrs.size;
                    const videoSizeDesc = formatFileSize(videoSize);
                    const videoFile = item.attrs.file;
                    const videoType = item.attrs.type;
                    const videoHeight = item.attrs.height;
                    const videoWidth = item.attrs.width;
                    const videoDuration = item.attrs.duration;
                    const minutes = Math.floor(videoDuration / 60);
                    const seconds = Math.floor(videoDuration % 60);
                    const formattedDuration = `${minutes}:${seconds.toString().padStart(2, '0')}`;
                    console.log(`video height: ${videoHeight} video width: ${videoWidth} video duration: ${formattedDuration}`);
                    const videoMsg = messageBase('', MessageType.VIDEO_MESSAGE);
                    msg = {
                        ...videoMsg,
                        contentMetadata: {
                            name: videoName,
                            width: videoWidth,
                            height: videoHeight,
                            mediaType: videoType,
                            duration: videoDuration,
                            durationDesc: formattedDuration,
                            size: videoSize,
                            sizeDesc: videoSizeDesc
                        }
                    }
                    //添加消息
                    dispatch(addMessage({ sessionId: sessionId, message: msg }))
                    //获取视频封面
                    createThumbnail(videoFile)
                        .then(
                            ({ poster }) => {
                                const thumbnailName = uuidv4() + '.png';
                                const thumbnailFile = dataURLtoFile(poster, thumbnailName)
                                //上传视频封面图片
                                uploadFile(thumbnailFile)
                                    .then(
                                        (url) => {
                                            const newMsg = { ...msg, status: MessageStatus.SENT, contentMetadata: { ...msg.contentMetadata, thumbnailUrl: url } }
                                            dispatch(updateMessage({ message: newMsg }))
                                            //上传视频
                                            uploadFile(videoFile)
                                                .then(
                                                    (res) => {
                                                        const newMsg2 = { ...newMsg, status: MessageStatus.SENT, content: res }
                                                        dispatch(updateMessage({ message: newMsg2 }))
                                                        //向服务器发送消息
                                                        realSendMessage(newMsg2)
                                                    },
                                                    (error) => {
                                                        dispatch(updateMessageStatus({ id: msg.id, newStatus: MessageStatus.FAILED }))
                                                    }
                                                )
                                        },
                                        (error) => {
                                            dispatch(updateMessageStatus({ id: msg.id, newStatus: MessageStatus.FAILED }))
                                        }
                                    );
                            },
                            (error) => {
                                dispatch(updateMessageStatus({ id: msg.id, newStatus: MessageStatus.FAILED }))
                            }
                        )
                    break;
                case 'paragraph':
                    const content = item?.content;
                    if (!content) {
                        continue;
                    }
                    let textMessage = [];
                    for (const c of content) {
                        if (c.type === 'text') {
                            textMessage.push(c.text);
                        } else if (c.type === 'hardBreak') {
                            textMessage.push('\n');
                        }
                    }
                    const text = textMessage.join("");
                    msg = messageBase(text, MessageType.TEXT_MESSAGE);
                    //添加消息
                    dispatch(addMessage({ sessionId: sessionId, message: msg }))
                    //向服务器发送消息
                    realSendMessage(msg);
                    break;
                default:
                    message.error('未知的消息类型');
                    continue;
            }
        }
        if (editorFlag) {
            editor.commands.clearContent();
        }
    }

    const realSendMessage = (msg) => {
        if (socketRef.current.readyState === WebSocket.OPEN) {
            socketRef.current.send(JSON.stringify(msg));
        } else {
            //更新消息状态
            dispatch(updateMessageStatus({ id: msg.id, newStatus: MessageStatus.FAILED }))
        }
    }

    const messageBase = (content, type) => {
        const id = IdGen.nextId()
        return {
            id: id,
            ackId: id,
            type: type,
            content: content,
            status: MessageStatus.PENDING,
            sessionId: sessionId,
            sender: session.userId,
            receiver: session.receiverUserId,
            deliveryMethod: session.deliveryMethod,
            self: true,
            timestamp: new Date().getTime(),
            name: userInfo.nickname,
            avatar: userInfo.avatar
        }
    }

    return (
        <>
            <div style={{ width: '100%', height: '100%' }}>
                <Content style={{ height: '94%' }}>
                    <Layout style={{ height: '100%' }}>
                        {/* 聊天内容展示 */}
                        <Content className='content-chat' style={{ height: '65%' }}>
                            <AutoSizer>
                                {({ height, width }) => {
                                    return (
                                        <div>
                                            <VirtualizedList
                                                ref={chatContentRef}
                                                className='content-chat-list'
                                                width={width}
                                                height={height}
                                                rowCount={messages?.length || 0}
                                                rowHeight={cache.current.rowHeight}
                                                deferredMeasurementCache={cache.current}
                                                rowRenderer={rowRenderer}
                                                onRowsRendered={handleRowsRendered}
                                            />
                                        </div>
                                    );
                                }}
                            </AutoSizer>
                        </Content>
                        <Content style={{ height: '35%' }}>
                            <Layout style={{ height: '100%' }}>
                                {/* 聊天工具栏 */}
                                <Content className='content-toolbar' style={{ height: '13%' }}>
                                    <Flex gap='middle' justify='flex-start' align='center' style={{ height: '100%', marginLeft: '15px' }}>
                                        <div ref={emojiRef} className='div-Picker' hidden={emojiHide}>
                                            <Picker set="native" previewPosition="none" searchPosition="none" data={emojiMartData} onEmojiSelect={emojiSelectHandler} />
                                        </div>
                                        <img src={emoteImg} alt='表情' style={{ width: '20px', height: '20px' }} onClick={showEmojiFrame} />
                                        <Uploader ref={uploaderRef} {...uploadProps} />
                                    </Flex>
                                </Content>
                                {/* 文本编辑 */}
                                <Content className='content-text-area' style={{ height: '87%' }}>
                                    <EditorContent editor={editor} />
                                </Content>
                            </Layout>
                        </Content>
                    </Layout>
                </Content>
                <Content className='content-footer' style={{ height: '6%' }}>
                    <Flex justify='end' align='center' style={{ height: '100%', width: '100%' }}>
                        <Button className='content-footer-send-button' onClick={() => sendMessage()}>发送(S)</Button>
                    </Flex>
                </Content>
            </div>
        </>
    );
}

export default ChatItem;