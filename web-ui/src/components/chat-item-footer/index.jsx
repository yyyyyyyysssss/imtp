import React, { useEffect, useRef, useState } from 'react';
import './index.less'
import { Flex, Layout, Button, message } from "antd"
import { EditorContent, useEditor } from '@tiptap/react'
import HardBreak from '@tiptap/extension-hard-break'
import { StarterKit } from '@tiptap/starter-kit';
import emojiMartData from '@emoji-mart/data'
import Picker from '@emoji-mart/react'
import emoteImg from '../../assets/img/emote_icon.png'
import voiceCallImg from '../../assets/img/voice_call-50.png'
import videoCallImg from '../../assets/img/video_call-50.png'
import { FieNode } from '../FileNode'
import { VideoNode } from '../VideoNode'
import { ImageNode } from '../ImageNode';
import Uploader from '../Uploader';
import { useWebSocket } from '../../context';
import IdGen from '../../utils/IdGen';
import { useDispatch, useSelector } from 'react-redux';
import { addMessage, updateMessage, updateMessageStatus, startVoiceCall } from '../../redux/slices/chatSlice';
import { MessageStatus, MessageType, CallOperation } from '../../enum';
import { formatFileSize, getVideoDimensionsOfByFile, dataURLtoFile, createThumbnail } from '../../utils'
import { v4 as uuidv4 } from 'uuid';

const { Content } = Layout;

const ChatItemFooter = React.memo(({ session }) => {

    const { socket } = useWebSocket();
    const socketRef = useRef();
    useEffect(() => {
        socketRef.current = socket;
    }, [socket]);

    const dispatch = useDispatch()
    const voiceCallVisible = useSelector(state => state.chat.voiceCall.visible)
    //用户信息
    const userInfo = useSelector(state => state.chat.userInfo) || {}
    //表情框显示与隐藏
    const [emojiHide, setEmojiHide] = useState(true);
    const emojiRef = useRef(null);
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
    //表情包选中
    const emojiSelectHandler = (emoji) => {
        editor.chain().focus().insertContent(emoji.native).run();
        setEmojiHide(true);
    }
    //显示表情框
    const showEmojiFrame = () => {
        setEmojiHide(false);
    }
    //文件发送
    const uploaderRef = useRef(null);
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
    const uploadFile = async (file, progressId = null) => {
        return uploaderRef.current.uploadFile(file, progressId);
    }
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
        const sessionId = session.id
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
                    msg.progressId = IdGen.nextId()
                    //添加消息
                    dispatch(addMessage({ sessionId: sessionId, message: msg }))
                    uploadFile(file, msg.progressId)
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
    //服务器发送消息
    const realSendMessage = (msg) => {
        if (socketRef.current.readyState === WebSocket.OPEN) {
            socketRef.current.send(JSON.stringify(msg));
        } else {
            //更新消息状态
            dispatch(updateMessageStatus({ id: msg.id, newStatus: MessageStatus.FAILED }))
        }
    }

    const voiceCall = () => {
        if (voiceCallVisible) {
            message.info('正在通话中...')
        } else {
            dispatch(startVoiceCall({
                sessionId: session.id,
                type: CallOperation.INVITE
            }))
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
            sessionId: session.id,
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
        <Layout style={{ height: '100%' }}>
            <Flex flex={1} vertical>
                {/* 聊天工具栏 */}
                <Flex flex={2}>
                    <Content className='content-toolbar'>
                        <Flex gap='middle' justify='flex-start' align='center' style={{ height: '100%', marginLeft: '15px' }}>
                            <div ref={emojiRef} className='div-Picker' hidden={emojiHide}>
                                <Picker set="native" previewPosition="none" searchPosition="none" data={emojiMartData} onEmojiSelect={emojiSelectHandler} />
                            </div>
                            <img src={emoteImg} title='表情' alt='表情' style={{ width: '20px', height: '20px', cursor: 'pointer' }} onClick={showEmojiFrame} />
                            <Uploader ref={uploaderRef} {...uploadProps} />
                            <img src={voiceCallImg} onClick={voiceCall} title='语音通话' alt='语音通话' style={{ width: '22px', height: '22px', cursor: 'pointer' }} />
                            <img src={videoCallImg} title='视频通话' alt='视频通话' style={{ width: '24px', height: '24px', cursor: 'pointer' }} />
                        </Flex>
                    </Content>
                </Flex>
                {/* 文本编辑 */}
                <Flex flex={6}>
                    <Content className='content-text-area' >
                        <EditorContent editor={editor} />
                    </Content>
                </Flex>
                {/* 发送按钮 */}
                <Flex flex={2}>
                    <Content className='content-footer'>
                        <Flex flex={1} justify='end' align='center'>
                            <Button className='content-footer-send-button' onClick={() => sendMessage()}>发送(S)</Button>
                        </Flex>
                    </Content>
                </Flex>
            </Flex>
        </Layout>
    )
})

export default ChatItemFooter