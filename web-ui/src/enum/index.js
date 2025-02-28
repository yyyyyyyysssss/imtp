export const MessageType = Object.freeze({
    COMMON_RESPONSE: -1,
    TEXT_MESSAGE: 1,
    VOICE_MESSAGE: 2,
    IMAGE_MESSAGE: 4,
    VIDEO_MESSAGE: 5,
    FILE_MESSAGE: 6,
    SIGNALING_OFFER: 40,
    SIGNALING_ANSWER: 41,
    SIGNALING_CANDIDATE: 42,
    SIGNALING_CLOSE: 49,
})


export const MessageStatus = Object.freeze({
    PENDING: 'PENDING',
    SENT: 'SENT',
    DELIVERED: 'DELIVERED',
    FAILED: 'FAILED'
})

export const DeliveryMethod = Object.freeze({
    SINGLE: 'SINGLE',
    GROUP: 'GROUP'
})


export const VoiceCallType = Object.freeze({
    INVITE: 'INVITE',
    ACCEPT: 'ACCEPT'
})