export interface MessageContact {
  userId: string
  userName: string
  displayName: string
  role: string
  clubId: string
  clubName: string
  lastMessage: string
  lastMessageTime: string
  unreadCount: number
}

export interface DirectMessage {
  id: string
  senderId: string
  senderName: string
  receiverId: string
  receiverName: string
  clubId: string
  clubName: string
  content: string
  createTime: string
  mine: boolean
}
