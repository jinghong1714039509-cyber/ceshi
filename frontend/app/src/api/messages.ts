import { get, post } from './request'
import type { DirectMessage, MessageContact } from '../types/message'

export type { DirectMessage, MessageContact } from '../types/message'

export function getMessageContacts() {
  return get<MessageContact[]>('/messages/contacts')
}

export function getConversation(contactId: string) {
  return get<DirectMessage[]>(`/messages/conversations/${contactId}`)
}

export function sendDirectMessage(contactId: string, payload: { content: string }) {
  return post<null, { content: string }>(`/messages/conversations/${contactId}`, payload)
}
