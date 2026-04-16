<template>
  <div class="forum-shell">
    <section class="forum-main">
      <article class="section-panel forum-composer">
        <div class="forum-composer__header">
          <div class="forum-avatar">
            <img v-if="session.avatarUrl" :src="session.avatarUrl" alt="用户头像" />
            <span v-else>{{ avatarText }}</span>
          </div>
          <div>
            <strong>{{ session.displayName }}</strong>
            <span>分享今天在校园里看到的活动、帖子和想法</span>
          </div>
        </div>

        <el-form :model="form" label-position="top">
          <el-form-item label="正文">
            <el-input v-model="form.content" type="textarea" :rows="5" placeholder="写点此刻的社团动态、活动感受或者想发起的话题" />
          </el-form-item>

          <div class="form-grid">
            <el-form-item label="标题">
              <el-input v-model="form.title" placeholder="可选，不填会自动生成摘要标题" />
            </el-form-item>
            <el-form-item label="话题">
              <el-input v-model="form.topicTag" placeholder="例如：校园活动" />
            </el-form-item>
            <el-form-item label="封面">
              <el-select v-model="form.coverImage" clearable placeholder="可选封面">
                <el-option v-for="cover in defaultCoverImages" :key="cover" :label="cover" :value="cover" />
              </el-select>
            </el-form-item>
          </div>

          <div class="forum-composer__actions">
            <el-button type="primary" :loading="submitting" @click="submitPost">发布动态</el-button>
          </div>
        </el-form>
      </article>

      <article class="section-panel forum-feed-panel">
        <header class="forum-feed-panel__head">
          <h2 class="page-title page-title--section">论坛动态</h2>
          <div class="toolbar">
            <el-input v-model="keyword" clearable placeholder="搜索内容、作者、话题" class="forum-feed-panel__search" />
            <el-select v-model="topicFilter" clearable placeholder="筛选话题">
              <el-option v-for="topic in feed.hotTopics" :key="topic.topicTag" :label="`#${topic.topicTag}`" :value="topic.topicTag" />
            </el-select>
          </div>
        </header>

        <div class="forum-feed">
          <article v-for="post in filteredPosts" :key="post.id" class="paper-list-card forum-post">
            <div class="forum-post__header">
              <div class="forum-avatar forum-avatar--small">
                <img v-if="post.authorAvatar" :src="post.authorAvatar" :alt="post.authorName" />
                <span v-else>{{ post.authorName.slice(0, 1) }}</span>
              </div>
              <div class="forum-post__author">
                <strong>{{ post.authorName }}</strong>
                <span>{{ post.authorRole }} · {{ post.createTime }}</span>
              </div>
            </div>

            <div class="paper-list-card__body">
              <div class="inline-meta">
                <span v-if="post.topicTag" class="meta-pill">#{{ post.topicTag }}</span>
                <span v-if="post.ownPost" class="meta-pill">我的帖子</span>
              </div>

              <h3 class="paper-list-card__title">{{ post.title }}</h3>
              <p class="paper-list-card__text">{{ post.content }}</p>

              <img v-if="post.coverImage" class="forum-post__cover" :src="post.coverImage || pickDefaultCover(post.id)" :alt="post.title" />

              <div class="forum-post__actions">
                <button type="button" class="forum-action" :class="{ 'forum-action--active': post.liked }" @click="handleToggleLike(post.id)">
                  点赞 {{ post.likeCount }}
                </button>
                <button type="button" class="forum-action">
                  评论 {{ post.commentCount }}
                </button>
              </div>

              <div v-if="post.recentComments.length" class="forum-comments">
                <div v-for="comment in post.recentComments" :key="comment.id" class="forum-comment">
                  <strong>{{ comment.authorName }}</strong>
                  <span>{{ comment.content }}</span>
                  <small>{{ comment.createTime }}</small>
                </div>
              </div>

              <div class="forum-comment-box">
                <el-input
                  v-model="commentDrafts[post.id]"
                  placeholder="写下你的评论"
                  @keydown.enter.prevent="submitComment(post.id)"
                />
                <el-button plain @click="submitComment(post.id)">发送</el-button>
              </div>
            </div>
          </article>
        </div>

        <div v-if="!filteredPosts.length" class="empty-state">当前没有符合条件的论坛内容。</div>
      </article>
    </section>

    <aside class="forum-side">
      <article class="section-panel">
        <h2 class="page-title page-title--section">热议话题</h2>
        <div class="forum-topic-list">
          <button
            v-for="topic in feed.hotTopics"
            :key="topic.topicTag"
            type="button"
            class="forum-topic"
            @click="topicFilter = topic.topicTag"
          >
            <strong>#{{ topic.topicTag }}</strong>
            <span>{{ topic.postCount }} 条动态</span>
          </button>
        </div>
      </article>

      <article class="section-panel">
        <h2 class="page-title page-title--section">推荐社团</h2>
        <div class="split-list">
          <div v-for="club in recommendedClubs" :key="club.id" class="split-list__item">
            <div>
              <strong>{{ club.name }}</strong>
              <span>{{ club.category }} · {{ club.totalMembers }} 人</span>
            </div>
            <el-button text @click="router.push(`/clubs/${club.id}`)">查看</el-button>
          </div>
        </div>
      </article>
    </aside>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPublicClubs, type ClubSummary } from '../api/club'
import { createForumComment, createForumPost, getForumFeed, toggleForumLike, type ForumFeed } from '../api/forum'
import { defaultCoverImages, pickDefaultCover } from '../content/default-covers'
import { useSessionStore } from '../stores/session'

const router = useRouter()
const session = useSessionStore()
const submitting = ref(false)
const keyword = ref('')
const topicFilter = ref('')
const feed = reactive<ForumFeed>({
  posts: [],
  hotTopics: [],
})
const recommendedClubs = ref<ClubSummary[]>([])
const commentDrafts = reactive<Record<string, string>>({})

const form = reactive({
  title: '',
  content: '',
  coverImage: '',
  topicTag: '',
})

const avatarText = computed(() => (session.displayName || 'U').slice(0, 1).toUpperCase())
const filteredPosts = computed(() => {
  const search = keyword.value.trim().toLowerCase()
  return feed.posts.filter((post) => {
    const matchesKeyword = !search || [
      post.title,
      post.content,
      post.authorName,
      post.topicTag,
    ].some((item) => item?.toLowerCase().includes(search))

    const matchesTopic = !topicFilter.value || post.topicTag === topicFilter.value
    return matchesKeyword && matchesTopic
  })
})

async function loadFeed() {
  const [feedRes, clubsRes] = await Promise.all([getForumFeed(), getPublicClubs()])
  if (feedRes.code !== 0) throw new Error(feedRes.msg)
  if (clubsRes.code !== 0) throw new Error(clubsRes.msg)
  feed.posts = feedRes.data.posts
  feed.hotTopics = feedRes.data.hotTopics
  recommendedClubs.value = clubsRes.data.slice(0, 5)
}

async function submitPost() {
  if (!form.content.trim()) {
    ElMessage.warning('请先填写正文内容')
    return
  }

  try {
    submitting.value = true
    const result = await createForumPost({
      title: form.title.trim() || undefined,
      content: form.content.trim(),
      coverImage: form.coverImage || undefined,
      topicTag: form.topicTag.trim() || undefined,
    })
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '动态发布成功')
    Object.assign(form, { title: '', content: '', coverImage: '', topicTag: '' })
    await loadFeed()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '动态发布失败')
  } finally {
    submitting.value = false
  }
}

async function handleToggleLike(postId: string) {
  try {
    const result = await toggleForumLike(postId)
    if (result.code !== 0) throw new Error(result.msg)
    await loadFeed()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '点赞操作失败')
  }
}

async function submitComment(postId: string) {
  const content = (commentDrafts[postId] || '').trim()
  if (!content) {
    ElMessage.warning('请输入评论内容')
    return
  }

  try {
    const result = await createForumComment(postId, { content })
    if (result.code !== 0) throw new Error(result.msg)
    commentDrafts[postId] = ''
    ElMessage.success(result.msg || '评论发布成功')
    await loadFeed()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '评论发布失败')
  }
}

onMounted(async () => {
  try {
    await loadFeed()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '论坛内容加载失败')
  }
})
</script>

<style scoped>
.forum-shell {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 18px;
  align-items: start;
}

.forum-main {
  display: grid;
  gap: 18px;
}

.forum-composer__header,
.forum-post__header {
  display: grid;
  grid-template-columns: 56px minmax(0, 1fr);
  gap: 12px;
  align-items: center;
}

.forum-avatar {
  width: 56px;
  height: 56px;
  border-radius: 999px;
  overflow: hidden;
  border: 1px solid var(--line);
  background: var(--terracotta-soft);
  color: var(--terracotta-deep);
  display: grid;
  place-items: center;
  font-weight: 700;
}

.forum-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.forum-avatar--small {
  width: 48px;
  height: 48px;
}

.forum-composer__header strong,
.forum-post__author strong {
  display: block;
  color: var(--ink);
}

.forum-composer__header span,
.forum-post__author span {
  display: block;
  margin-top: 4px;
  color: var(--ink-soft);
}

.forum-composer__actions {
  display: flex;
  justify-content: flex-end;
}

.forum-feed-panel__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 18px;
}

.page-title--section {
  font-size: 30px;
  margin: 0;
}

.forum-feed-panel__search {
  width: 240px;
}

.forum-feed {
  display: grid;
  gap: 16px;
}

.forum-post__cover {
  display: block;
  width: 100%;
  height: 280px;
  object-fit: cover;
  border-radius: 12px;
  border: 1px solid var(--line);
}

.forum-post__actions {
  display: flex;
  gap: 14px;
  align-items: center;
  padding-top: 6px;
}

.forum-action {
  padding: 0;
  border: 0;
  background: transparent;
  color: var(--ink-soft);
  cursor: pointer;
}

.forum-action--active {
  color: var(--terracotta-deep);
}

.forum-comments {
  display: grid;
  gap: 10px;
  padding: 14px;
  border: 1px solid var(--line);
  border-radius: 12px;
  background: rgba(244, 239, 231, 0.45);
}

.forum-comment {
  display: grid;
  gap: 4px;
}

.forum-comment strong {
  color: var(--ink);
}

.forum-comment span,
.forum-comment small {
  color: var(--ink-soft);
}

.forum-comment-box {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 10px;
  align-items: center;
}

.forum-side {
  display: grid;
  gap: 18px;
  position: sticky;
  top: 18px;
}

.forum-topic-list {
  display: grid;
  gap: 10px;
}

.forum-topic {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border: 1px solid var(--line);
  border-radius: 12px;
  background: rgba(255, 253, 249, 0.82);
  cursor: pointer;
  text-align: left;
}

.forum-topic strong,
.forum-topic span {
  display: block;
}

.forum-topic span {
  color: var(--ink-soft);
}

@media (max-width: 1180px) {
  .forum-shell {
    grid-template-columns: 1fr;
  }

  .forum-side {
    position: static;
  }
}

@media (max-width: 760px) {
  .forum-feed-panel__head,
  .forum-comment-box {
    grid-template-columns: 1fr;
    display: grid;
  }

  .forum-feed-panel__search {
    width: 100%;
  }
}
</style>
