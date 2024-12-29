export const MessageType = Object.freeze({
    TEXT_MESSAGE: 1,
    IMAGE_MESSAGE: 4,
    VIDEO_MESSAGE: 5,
    FILE_MESSAGE: 6
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