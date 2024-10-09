import React, { useState, useRef, useEffect, useCallback, useContext} from 'react'
import './index.less'
import Picker from '@emoji-mart/react'
import Icon, { FileOutlined, LoadingOutlined } from '@ant-design/icons';
import { Flex, Layout, Avatar, Button, Image as AntdImage } from "antd"
import { List as VirtualizedList, AutoSizer, CellMeasurer, CellMeasurerCache } from 'react-virtualized'
import { formatFileSize, getVideoDimensionsOfByFile, getVideoPoster, download, dataURLtoFile } from '../../../utils'
import { FieNode } from '../../../components/FileNode'
import { VideoNode } from '../../../components/VideoNode'
import { ImageNode } from '../../../components/ImageNode';
import { v4 as uuidv4 } from 'uuid';
import emoteImg from '../../../assets/img/emote_icon.png'
import videoPlayIcon from '../../../assets/img/video-play-48.png'
import videoLoadingIcon from '../../../assets/img/video-loading50 .gif'
import { EditorContent, useEditor } from '@tiptap/react'
import HardBreak from '@tiptap/extension-hard-break'
import { StarterKit } from '@tiptap/starter-kit';
import emojiMartData from '@emoji-mart/data'
import Uploader from '../../../components/Uploader';
import { ChatPanelContext, useWebSocket } from '../../../context';
import SnowflakeIdWorker from '../../../components/SnowflakeIdWorker';

const { Content } = Layout;

const PENDING = "PENDING";
const SENT = "SENT";
const DELIVERED = "DELIVERED";

const TEXT_MESSAGE = 1;
const IMAGE_MESSAGE = 4;
const VIDEO_MESSAGE = 5;
const FILE_MESSAGE = 6;

const SINGLE = "SINGLE";

const snowflake = new SnowflakeIdWorker(1);

const ChatItem = (props) => {
    const {socket,userInfo} = useWebSocket();
    const socketRef = useRef();
    const userInfoRef = useRef();
    useEffect(() => {
        socketRef.current = socket;
        userInfoRef.current =   userInfo;
    },[socket,userInfo]);
    const { handleVideoPlay,handleSenderMessage,updateChatItem } = useContext(ChatPanelContext);
    const {userSessionItem } = props;
    //聊天内容
    const [chatContentData, setChatContentData] = useState([]);
    const chatContentRef = useRef(null);
    //监听数据变化并滚动到末尾
    useEffect(() => {
        if (chatContentRef.current && chatContentData) {
            requestAnimationFrame(() => {
                chatContentRef.current.scrollToRow(chatContentData.length - 1);
            });
        }
    }, [chatContentData]);
    useEffect(() => {
        const { selectTab, userSessionItem } = props;
        if (selectTab && selectTab === userSessionItem.id) {
            const chatItemData = userSessionItem.chatItemData;
            if (chatItemData) {
                setChatContentData([...chatItemData]);
            }
        }

    }, [props])
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
        const item = chatContentData[index]
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
                    <div ref={registerChild} key={item.id} className='chat-item' style={style}>
                        {renderItem(item, index)}
                    </div>
                )}

            </CellMeasurer>
        );
    }, [chatContentData]);
    //图片消息
    const ImageMessage = (({ msg }) => {
        const self = msg.self;
        const content = msg.content;
        const status = msg.status;
        const mediaHeight = 200 / msg.contentMetadata.width * msg.contentMetadata.height;
        let preview;
        let blur;
        let icon;
        if (status === PENDING) {
            preview = false;
            blur = 'blur(5px)';
        } else {
            preview = true;
            blur = 'blur(0px)';    
        }
        if(status === DELIVERED){
            icon = <LoadingOutlined style={{ fontSize: '15px', order: self ? 0 : 1, display: 'none' }} />;
        }else {
            icon = <LoadingOutlined style={{ fontSize: '15px', order: self ? 0 : 1 }} />;
        }
        return (
            <Flex gap="small">
                <div style={{ order: self ? 1 : 0 }}>
                    <AntdImage
                        className='image-message'
                        height={mediaHeight}
                        preview={preview}
                        src={content}
                        style={{ filter: blur }}
                    />
                </div>
                {self ? icon : ''}
            </Flex>
        );
    });
    //视频消息
    const VideoMessage = (({ msg }) => {
        const self = msg.self;
        const content = msg.content;
        const status = msg.status;
        const fileType = msg.contentMetadata.mediaType;
        const thumbnailUrl = msg.contentMetadata.thumbnailUrl;
        let durationDesc = '';
        const mediaHeight = 120 / msg.contentMetadata.width * msg.contentMetadata.height;
        let icon;
        let videoIcon;
        if (status === PENDING) {
            videoIcon = videoLoadingIcon;
        } else {
            videoIcon = videoPlayIcon;
            durationDesc = msg.contentMetadata.durationDesc;
        }
        if(status === DELIVERED){
            icon = <LoadingOutlined style={{ fontSize: '15px', order: self ? 0 : 1, display: 'none' }} />;
        }else {
            icon = <LoadingOutlined style={{ fontSize: '15px', order: self ? 0 : 1 }} />;
        }
        return (
            <Flex gap="small">
                <div
                    style={{ order: self ? 1 : 0 }}
                    className='video-div'
                    onClick={() => videoPlay(content, fileType)}
                >
                    <AntdImage
                        className='video-message'
                        style={{ width: '120px', height: mediaHeight }}
                        height={mediaHeight}
                        preview={false}
                        src={thumbnailUrl}
                    />
                    <div className='video-gradient' />
                    <div className='video-icon'>
                        <img src={videoIcon} alt='icon' />
                    </div>
                    <div className='video-duration'>
                        <label>{durationDesc}</label>
                    </div>
                </div>
                {self ? icon : ''}
            </Flex>

        );
    });
    //视频播放
    const videoPlay = (url, fileType) => {
        handleVideoPlay(url, fileType);
    }
    //文字消息
    const TextMessage = (({ msg }) => {
        const self = msg.self;
        const content = msg.content;
        const status = msg.status;
        let icon;
        if(status === DELIVERED){
            icon = <LoadingOutlined style={{ fontSize: '15px', order: self ? 0 : 1, display: 'none' }} />;
        }else {
            icon = <LoadingOutlined style={{ fontSize: '15px', order: self ? 0 : 1 }} />;
        }
        return (
            <Flex gap="small" align='center' justify={self ? 'end' : 'start'} style={{ width: '100%' }}>
                <div className={`text-message ${self ? 'text-message-right' : 'text-message-left'}`} style={{ order: self ? 1 : 0 }}>
                    {content}
                </div>
                {self ? icon : ''}
            </Flex>
        );
    });
    //其他文件消息
    const OtherFileMessage = (({ msg }) => {
        const self = msg.self;
        const fileName = msg.contentMetadata.name;
        const fileSizeDesc = msg.contentMetadata.sizeDesc;
        const content = msg.content;
        const status = msg.status;
        let overview;
        if (status === PENDING) {
            overview = '上传中';
        } else {
            overview = fileSizeDesc;
        }
        let icon;
        if(status === DELIVERED){
            icon = <LoadingOutlined style={{ fontSize: '15px', order: self ? 0 : 1, display: 'none' }} />;
        }else {
            icon = <LoadingOutlined style={{ fontSize: '15px', order: self ? 0 : 1 }} />;
        }
        return (
            <Flex gap="small">
                <div style={{ cursor: 'pointer', order: self ? 1 : 0  }} onClick={() => otherFileMessageClick(content, fileName)}>
                    <Flex align='center' className={`other-file-message ${self ? 'other-file-message-right' : 'other-file-message-left'}`} justify={self ? 'end' : 'start'} gap="middle" style={{ width: '200px', height: '80px', order: self ? 1 : 0 }}>
                        <Flex style={{ width: '150px', overflow: 'hidden' }} gap="small" vertical>
                            <label className='other-file-filename-ellipsis' style={{ wordWrap: 'break-word' }}>{fileName}</label>
                            <label style={{ fontSize: '12px', color: 'gray' }}>{overview}</label>
                        </Flex>
                        <Icon component={FileOutlined} style={{ color: 'gray', fontSize: '40px' }} />
                    </Flex>
                </div>
                {self ? icon : ''}
            </Flex>

        );
    });
    //其他文件消息点击下载
    const otherFileMessageClick = (url, fileName) => {
        download(url, fileName)
    }
    //聊天项渲染函数
    const renderItem = (item, index) => {
        if (!item) {
            return (<></>);
        }
        let c;
        switch (item.type) {
            case IMAGE_MESSAGE:
                c = <ImageMessage msg={item} />
                break;
            case VIDEO_MESSAGE:
                c = <VideoMessage msg={item} />
                break;
            case TEXT_MESSAGE:
                c = <TextMessage msg={item} />
                break;
            case FILE_MESSAGE:
                c = <OtherFileMessage msg={item} />
                break;
            default:
                console.log(item);
        }
        const self = item.self;
        const name = item.name;
        const avatar = item.avatar;
        const deliveryMethod = item.deliveryMethod;
        return (
            <Flex gap="small">
                <Avatar size={45} shape="square" src={avatar} style={{ order: 0 }} />
                <Flex gap="small" justify='center' align={self ? 'end' : 'start'} style={{ width: '100%', order: self ? -1 : 1 }} vertical>
                    {deliveryMethod === SINGLE ? <></> : self ? <></> : <label className='chat-item-label-name'>{name}</label>}
                    {c}
                </Flex>
            </Flex>
        );
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
                    const imageMsg = gMessage(IMAGE_MESSAGE,dataUrl);
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
                    uploadFile(imageFile)
                        .then((url) => {
                            msg.status = SENT;
                            msg.content = url;
                            updateChatItem(userSessionItem.id,msg);
                            socketRef.current.send(JSON.stringify({
                                ackId: msg.ackId,
                                type: msg.type,
                                sender: msg.sender,
                                receiver: msg.receiver,
                                deliveryMethod: msg.deliveryMethod,
                                content: msg.content,
                                contentMetadata: msg.contentMetadata
                            }));
                        })
                    break;
                case 'fileNode':
                    const fileName = item.attrs.name;
                    const fileSize = item.attrs.size;
                    const fileSizeDesc = formatFileSize(fileSize);
                    const fileType = item.attrs.type;
                    const file = item.attrs.file;
                    const fileMsg = gMessage(FILE_MESSAGE,'');
                    msg = {
                        ...fileMsg,
                        contentMetadata: {
                            name: fileName,
                            mediaType: fileType,
                            size: fileSize,
                            sizeDesc: fileSizeDesc
                        }
                    }
                    uploadFile(file)
                        .then((url) => {
                            msg.status = SENT;
                            msg.content = url;
                            updateChatItem(userSessionItem.id,msg);
                            socketRef.current.send(JSON.stringify({
                                ackId: msg.ackId,
                                type: msg.type,
                                sender: msg.sender,
                                receiver: msg.receiver,
                                deliveryMethod: msg.deliveryMethod,
                                content: msg.content,
                                contentMetadata: msg.contentMetadata
                            }));
                        })
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
                    const videoMsg = gMessage(VIDEO_MESSAGE,'');
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
                    uploadFile(videoFile)
                        .then((url) => {
                            msg.content = url;
                            getVideoPoster(url)
                                .then(
                                    ({ poster }) => {
                                        const thumbnailName = uuidv4() + '.png';
                                        const thumbnailFile = dataURLtoFile(poster,thumbnailName)
                                        uploadFile(thumbnailFile)
                                        .then((url) => {
                                            msg.status = SENT;
                                            msg.contentMetadata.thumbnailUrl = url;
                                            updateChatItem(userSessionItem.id,msg);
                                            socketRef.current.send(JSON.stringify({
                                                ackId: msg.ackId,
                                                type: msg.type,
                                                sender: msg.sender,
                                                receiver: msg.receiver,
                                                deliveryMethod: msg.deliveryMethod,
                                                content: msg.content,
                                                contentMetadata: msg.contentMetadata
                                            }));
                                        });
                                        
                                    }
                                )
                        })
                    break;
                case 'paragraph':
                    const content = item?.content;
                    if(!content){
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
                    msg = gMessage(TEXT_MESSAGE,text);
                    socketRef.current.send(JSON.stringify({
                        ackId: msg.ackId,
                        type: msg.type,
                        sender: msg.sender,
                        receiver: msg.receiver,
                        deliveryMethod: msg.deliveryMethod,
                        content: msg.content
                    }));
                    break;
                default:
                    message.error('未知的消息类型');
                    continue;
            }
            handleSenderMessage(msg,userSessionItem.id);
        }
        if (editorFlag) {
            editor.commands.clearContent();
        }
    }

    const gMessage = (type,content) => {
        const msg = {
            id: uuidv4(),
            type: type,
            status: PENDING,
            sender: userSessionItem.userId,
            receiver: userSessionItem.receiverUserId,
            deliveryMethod: userSessionItem.deliveryMethod,
            self: true,
            ackId: snowflake.nextId(),
            timestamp: new Date().getTime(),
            avatar: userInfoRef.current.avatar,
            name: userSessionItem.name,
            content: content
        }
        return msg;
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
                                                rowCount={chatContentData?.length || 0}
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