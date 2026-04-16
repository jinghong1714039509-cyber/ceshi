import http from './http'

interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

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

export async function getMessageContacts() {
  const response = await http.get<ApiResponse<MessageContact[]>>('/messages/contacts')
  return response.data
}

export async function getConversation(contactId: string) {
  const response = await http.get<ApiResponse<DirectMessage[]>>(`/messages/conversations/${contactId}`)
  return response.data
}

export async function sendDirectMessage(contactId: string, payload: { content: string }) {
  const response = await http.post<ApiResponse<null>>(`/messages/conversations/${contactId}`, payload)
  return response.data
}
