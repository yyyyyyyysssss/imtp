export const MessageType = Object.freeze({
    COMMON_RESPONSE: -1,
    TEXT_MESSAGE: 1,
    VOICE_MESSAGE: 2,
    IMAGE_MESSAGE: 4,
    VIDEO_MESSAGE: 5,
    FILE_MESSAGE: 6,
    VOICE_CALL_MESSAGE: 7,
    VIDEO_CALL_MESSAGE: 8,
    SIGNALING_PRE_OFFER: 40,
    SIGNALING_OFFER: 41,
    SIGNALING_ANSWER: 42,
    SIGNALING_CANDIDATE: 43,
    SIGNALING_BUSY: 44,
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


export const CallOperation = Object.freeze({
    INVITE: 'INVITE',
    ACCEPT: 'ACCEPT'
})

export const CallType = Object.freeze({
    VOICE: 'VOICE',
    VIDEO: 'VIDEO'
})

export const CallStatus = Object.freeze({
    PENDING: 'PENDING',
    CONNECTING: 'CONNECTING',
    PROGRESSING: 'PROGRESSING',
    COMPLETED: 'COMPLETED',
    CANCELLED: 'CANCELLED',
    REFUSED: 'REFUSED',
    INTERRUPTED: 'INTERRUPTED'
})