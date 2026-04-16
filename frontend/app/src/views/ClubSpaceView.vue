<template>
  <div class="page-stack club-space">
    <section class="space-hero">
      <div class="space-hero__content">
        <p class="page-eyebrow">Club Space</p>
        <h1 class="page-title">{{ space?.clubName || '社团空间' }}</h1>
        <div class="inline-meta" v-if="space">
          <span class="meta-pill">{{ space.category }}</span>
          <span class="meta-pill">{{ space.memberCount }} 位成员</span>
          <span class="meta-pill">负责人：{{ space.managerName }}</span>
        </div>
      </div>
      <div class="space-hero__aside">
        <div class="orb orb--one"></div>
        <div class="orb orb--two"></div>
        <div class="toolbar">
          <el-button plain @click="router.push('/profile')">返回社团空间列表</el-button>
          <el-tag v-if="space?.canManage" effect="dark" color="#0d7a5f">管理者视角</el-tag>
        </div>
      </div>
    </section>

    <section class="club-space-layout" v-if="space">
      <article class="section-panel">
        <div class="section-header">
          <div>
            <p>Chat</p>
            <h3>即时交流</h3>
          </div>
        </div>

        <div class="wechat-chat">
          <div
            v-for="message in space.messages"
            :key="message.id"
            :class="['wechat-row', { 'wechat-row--mine': message.authorId === session.userId }]"
          >
            <div v-if="message.authorId !== session.userId" class="wechat-avatar">
              {{ message.authorName.slice(0, 1) }}
            </div>
            <div class="wechat-content">
              <div class="wechat-meta">
                <strong>{{ message.authorId === session.userId ? '我' : message.authorName }}</strong>
                <span>{{ message.createTime }}</span>
              </div>
              <div :class="['wechat-bubble', { 'wechat-bubble--mine': message.authorId === session.userId }]">
                {{ message.content }}
              </div>
            </div>
            <div v-if="message.authorId === session.userId" class="wechat-avatar wechat-avatar--mine">
              {{ session.displayName.slice(0, 1) }}
            </div>
          </div>
        </div>

        <el-form :model="messageForm" label-position="top" style="margin-top: 18px;" @submit.prevent="submitMessage">
          <el-form-item label="发送群消息">
            <el-input
              v-model="messageForm.content"
              type="textarea"
              :rows="3"
              placeholder="例如：我负责带相机电池和延长线"
            />
          </el-form-item>
          <div class="toolbar" style="justify-content: flex-end;">
            <el-button type="primary" @click="submitMessage">发送消息</el-button>
          </div>
        </el-form>
      </article>

      <article class="section-panel">
        <div class="section-header">
          <div>
            <p>Posts</p>
            <h3>社团动态墙</h3>
          </div>
        </div>

        <el-form :model="postForm" label-position="top" style="margin-top: 18px;" @submit.prevent="submitPost">
          <el-form-item label="帖子标题">
            <el-input v-model="postForm.title" placeholder="例如：周末采风路线最终确认" />
          </el-form-item>
          <el-form-item label="帖子内容">
            <el-input
              v-model="postForm.content"
              type="textarea"
              :rows="4"
              placeholder="把排期、集合点、注意事项或复盘信息写在这里"
            />
          </el-form-item>
          <div class="toolbar">
            <el-button type="primary" @click="submitPost">发布帖子</el-button>
          </div>
        </el-form>

        <div class="split-list">
          <div v-for="post in space.posts" :key="post.id" class="space-post">
            <div class="toolbar">
              <strong>{{ post.title }}</strong>
              <el-tag v-if="post.pinned" type="warning">置顶</el-tag>
            </div>
            <p>{{ post.content }}</p>
            <span class="muted-text">{{ post.authorName }} · {{ post.createTime }}</span>
          </div>
        </div>
      </article>
    </section>

    <section class="grid-two" v-if="space">
      <article class="section-panel">
        <div class="section-header">
          <div>
            <p>Activities</p>
            <h3>历史与近期活动</h3>
          </div>
        </div>
        <div class="card-grid" style="margin-top: 18px;">
          <article v-for="activity in space.activities" :key="activity.id" class="data-card">
            <h3>{{ activity.name }}</h3>
            <div class="inline-meta">
              <span class="meta-pill">{{ activity.activeTime }}</span>
              <span class="meta-pill">{{ activity.total }} 人报名</span>
            </div>
            <p>{{ activity.summary }}</p>
          </article>
        </div>
      </article>

      <article class="section-panel">
        <div class="section-header">
          <div>
            <p>Notices</p>
            <h3>社团公告</h3>
          </div>
        </div>
        <div class="split-list">
          <div v-for="notice in space.notices" :key="notice.id" class="split-list__item">
            <div>
              <strong>{{ notice.title }}</strong>
              <span>{{ notice.detail }}</span>
            </div>
            <span class="muted-text">{{ notice.createTime }}</span>
          </div>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createClubMessage, createClubPost, getClubSpace, type ClubSpace } from '../api/club-space'
import { useSessionStore } from '../stores/session'

const route = useRoute()
const router = useRouter()
const session = useSessionStore()
const clubId = String(route.params.clubId)
const space = ref<ClubSpace | null>(null)

const postForm = reactive({
  title: '',
  content: '',
})

const messageForm = reactive({
  content: '',
})

async function loadSpace() {
  try {
    const result = await getClubSpace(clubId)
    if (result.code !== 0) throw new Error(result.msg)
    space.value = result.data
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '加载社团空间失败')
    router.push('/profile')
  }
}

async function submitPost() {
  if (!postForm.title || !postForm.content) {
    ElMessage.warning('请先填写帖子标题和内容')
    return
  }
  try {
    const result = await createClubPost(clubId, postForm)
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '帖子发布成功')
    postForm.title = ''
    postForm.content = ''
    await loadSpace()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '帖子发布失败')
  }
}

async function submitMessage() {
  if (!messageForm.content) {
    ElMessage.warning('请输入消息内容')
    return
  }
  try {
    const result = await createClubMessage(clubId, messageForm)
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '消息发送成功')
    messageForm.content = ''
    await loadSpace()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '消息发送失败')
  }
}

onMounted(() => {
  void loadSpace()
})
</script>

<style scoped>
.space-hero {
  position: relative;
  overflow: hidden;
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 16px;
  padding: 26px;
  border: 1px solid var(--line);
  border-radius: 28px;
  background:
    radial-gradient(circle at top left, rgba(255, 135, 89, 0.32), transparent 28%),
    radial-gradient(circle at right, rgba(18, 132, 255, 0.24), transparent 32%),
    linear-gradient(135deg, rgba(255, 248, 241, 0.96), rgba(244, 249, 255, 0.92));
  box-shadow: var(--shadow-panel);
}

.space-hero__aside {
  position: relative;
  min-height: 180px;
}

.club-space-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(320px, 0.85fr);
  gap: 18px;
}

.wechat-chat {
  display: grid;
  gap: 16px;
  margin-top: 18px;
  max-height: 520px;
  overflow: auto;
  padding-right: 4px;
}

.wechat-row {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.wechat-row--mine {
  justify-content: flex-end;
}

.wechat-avatar {
  width: 42px;
  height: 42px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #0d7a5f, #58b7a2);
  color: #fff;
  font-weight: 700;
}

.wechat-avatar--mine {
  background: linear-gradient(135deg, #cb6b2c, #ff9d62);
}

.wechat-content {
  max-width: min(540px, 100%);
}

.wechat-meta {
  display: flex;
  gap: 12px;
  align-items: center;
  color: var(--muted);
  font-size: 13px;
}

.wechat-row--mine .wechat-meta {
  justify-content: flex-end;
}

.wechat-bubble {
  margin-top: 8px;
  padding: 14px 16px;
  border-radius: 18px 18px 18px 6px;
  background: #ffffff;
  border: 1px solid var(--line);
  line-height: 1.75;
}

.wechat-bubble--mine {
  border-radius: 18px 18px 6px 18px;
  background: linear-gradient(135deg, rgba(13, 122, 95, 0.12), rgba(88, 183, 162, 0.18));
}

.orb {
  position: absolute;
  border-radius: 999px;
  filter: blur(6px);
}

.orb--one {
  width: 180px;
  height: 180px;
  right: 30px;
  top: 10px;
  background: radial-gradient(circle, rgba(255, 132, 102, 0.8), rgba(255, 132, 102, 0.1));
}

.orb--two {
  width: 140px;
  height: 140px;
  right: 120px;
  bottom: 0;
  background: radial-gradient(circle, rgba(46, 164, 255, 0.72), rgba(46, 164, 255, 0.08));
}

.space-post {
  padding: 16px 18px;
  border-radius: 20px;
  border: 1px solid var(--line);
  background: rgba(255, 255, 255, 0.72);
}

.space-post p {
  margin: 12px 0 8px;
  line-height: 1.7;
}

@media (max-width: 1080px) {
  .club-space-layout,
  .space-hero {
    grid-template-columns: 1fr;
  }
}
</style>
