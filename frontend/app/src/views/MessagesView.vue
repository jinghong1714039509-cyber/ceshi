<template>
  <div class="page-stack">
    <section class="message-shell">
      <aside class="message-sidebar">
        <div class="message-sidebar__top">
          <h2>联系人</h2>
        </div>
        <div class="message-sidebar__list">
          <button
            v-for="contact in contacts"
            :key="contact.userId"
            type="button"
            :class="['message-contact', { 'message-contact--active': activeContact?.userId === contact.userId }]"
            @click="openConversation(contact)"
          >
            <div class="message-contact__avatar">{{ contact.displayName.slice(0, 1) }}</div>
            <div class="message-contact__body">
              <div class="message-contact__head">
                <strong>{{ contact.displayName }}</strong>
                <span>{{ contact.lastMessageTime || '' }}</span>
              </div>
              <div class="message-contact__meta">
                <small>{{ contact.clubName || contact.role }}</small>
                <el-badge v-if="contact.unreadCount > 0" :value="contact.unreadCount" />
              </div>
              <p>{{ contact.lastMessage || '还没有聊天记录' }}</p>
            </div>
          </button>
          <div v-if="contacts.length === 0" class="empty-state">当前还没有可用联系人。</div>
        </div>
      </aside>

      <article class="message-panel">
        <template v-if="activeContact">
          <header class="message-panel__header">
            <div>
              <h2>{{ activeContact.displayName }}</h2>
              <span>{{ activeContact.role }} · {{ activeContact.clubName || '平台私信' }}</span>
            </div>
          </header>

          <div class="message-thread">
            <div
              v-for="message in conversation"
              :key="message.id"
              :class="['message-thread__row', { 'message-thread__row--mine': message.mine }]"
            >
              <div v-if="!message.mine" class="message-avatar">{{ message.senderName.slice(0, 1) }}</div>
              <div class="message-bubble-wrap">
                <div class="message-bubble-meta">
                  <strong>{{ message.mine ? '我' : message.senderName }}</strong>
                  <span>{{ message.createTime }}</span>
                </div>
                <div :class="['message-bubble', { 'message-bubble--mine': message.mine }]">
                  {{ message.content }}
                </div>
              </div>
              <div v-if="message.mine" class="message-avatar message-avatar--mine">{{ session.displayName.slice(0, 1) }}</div>
            </div>
          </div>

          <footer class="message-composer">
            <el-input
              v-model="draft"
              type="textarea"
              :rows="4"
              placeholder="输入私信内容，Ctrl + Enter 发送"
              @keydown.ctrl.enter.prevent="submit"
            />
            <div class="toolbar message-composer__actions">
              <el-button type="primary" @click="submit">发送私信</el-button>
            </div>
          </footer>
        </template>

        <div v-else class="empty-state message-empty">选择左侧联系人后即可开始私信。</div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getConversation, getMessageContacts, sendDirectMessage, type DirectMessage, type MessageContact } from '../api/messages'
import { useSessionStore } from '../stores/session'

const route = useRoute()
const session = useSessionStore()
const contacts = ref<MessageContact[]>([])
const activeContact = ref<MessageContact | null>(null)
const conversation = ref<DirectMessage[]>([])
const draft = ref('')

async function loadContacts(preserveContactId?: string) {
  const result = await getMessageContacts()
  if (result.code !== 0) throw new Error(result.msg)
  contacts.value = result.data
  const nextContact = contacts.value.find((item) => item.userId === preserveContactId) || contacts.value[0] || null
  activeContact.value = nextContact
  if (nextContact) {
    await loadConversation(nextContact.userId)
  } else {
    conversation.value = []
  }
}

async function loadConversation(contactId: string) {
  const result = await getConversation(contactId)
  if (result.code !== 0) throw new Error(result.msg)
  conversation.value = result.data
}

async function openConversation(contact: MessageContact) {
  activeContact.value = contact
  await loadConversation(contact.userId)
  await loadContacts(contact.userId)
}

async function submit() {
  if (!activeContact.value) return
  if (!draft.value.trim()) {
    ElMessage.warning('请输入私信内容')
    return
  }
  try {
    const result = await sendDirectMessage(activeContact.value.userId, { content: draft.value.trim() })
    if (result.code !== 0) throw new Error(result.msg)
    draft.value = ''
    await loadConversation(activeContact.value.userId)
    await loadContacts(activeContact.value.userId)
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '发送私信失败')
  }
}

onMounted(async () => {
  try {
    await loadContacts(typeof route.query.contact === 'string' ? route.query.contact : undefined)
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '加载消息失败')
  }
})
</script>

<style scoped>
.message-shell {
  display: grid;
  grid-template-columns: 320px minmax(0, 1fr);
  gap: 18px;
  min-height: 720px;
}

.message-sidebar,
.message-panel {
  border: 1px solid var(--line);
  border-radius: 14px;
  background: linear-gradient(180deg, rgba(255, 253, 249, 0.98), rgba(247, 244, 239, 0.9));
  box-shadow: var(--shadow-soft);
}

.message-sidebar {
  padding: 16px;
}

.message-sidebar__top {
  padding-bottom: 14px;
  border-bottom: 1px solid var(--line);
}

.message-sidebar__top h2,
.message-panel__header h2 {
  margin: 0;
  font-family: var(--font-heading);
  color: var(--ink);
}

.message-sidebar__list {
  display: grid;
  gap: 8px;
  margin-top: 12px;
}

.message-contact {
  display: grid;
  grid-template-columns: 44px minmax(0, 1fr);
  gap: 12px;
  padding: 12px;
  border: 1px solid transparent;
  border-radius: 12px;
  background: transparent;
  text-align: left;
  cursor: pointer;
}

.message-contact--active {
  border-color: var(--line-strong);
  background: rgba(243, 225, 215, 0.34);
}

.message-contact__avatar,
.message-avatar {
  width: 44px;
  height: 44px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  background: var(--terracotta-soft);
  color: var(--terracotta-deep);
  font-weight: 700;
}

.message-avatar--mine {
  background: var(--sage-soft);
  color: var(--sage);
}

.message-contact__body,
.message-bubble-wrap {
  min-width: 0;
}

.message-contact__head,
.message-contact__meta,
.message-bubble-meta {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  align-items: center;
}

.message-contact__head span,
.message-contact__meta small,
.message-contact p,
.message-panel__header span,
.message-bubble-meta span {
  color: var(--ink-soft);
}

.message-contact p {
  margin: 6px 0 0;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.message-panel {
  display: grid;
  grid-template-rows: auto minmax(0, 1fr) auto;
  padding: 18px;
}

.message-panel__header {
  padding-bottom: 14px;
  border-bottom: 1px solid var(--line);
}

.message-thread {
  display: grid;
  gap: 16px;
  padding: 18px 0;
  overflow: auto;
}

.message-thread__row {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.message-thread__row--mine {
  justify-content: flex-end;
}

.message-thread__row--mine .message-bubble-meta {
  justify-content: flex-end;
}

.message-bubble {
  max-width: min(560px, 100%);
  margin-top: 6px;
  padding: 13px 15px;
  border: 1px solid var(--line);
  border-radius: 12px 12px 12px 4px;
  background: rgba(255, 253, 249, 0.94);
  line-height: 1.75;
  color: var(--ink);
}

.message-bubble--mine {
  border-radius: 12px 12px 4px 12px;
  background: rgba(243, 225, 215, 0.48);
}

.message-composer {
  padding-top: 14px;
  border-top: 1px solid var(--line);
}

.message-composer__actions {
  justify-content: flex-end;
  margin-top: 12px;
}

.message-empty {
  min-height: 540px;
  display: grid;
  place-items: center;
}

@media (max-width: 980px) {
  .message-shell {
    grid-template-columns: 1fr;
  }
}
</style>
