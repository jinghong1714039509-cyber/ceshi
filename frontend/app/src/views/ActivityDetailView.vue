<template>
  <div v-if="activity" class="page-stack">
    <section class="paper-cover activity-detail-cover">
      <img :src="coverFor(activity)" :alt="activity.name" />
      <div class="activity-detail-cover__body">
        <h1 class="page-title">{{ activity.name }}</h1>
        <div class="inline-meta">
          <span class="meta-pill">{{ activity.clubName }}</span>
          <span class="meta-pill">{{ activity.activeTime }}</span>
          <span class="meta-pill">{{ activity.location }}</span>
          <span class="meta-pill">{{ activity.total }}/{{ activity.capacity || '不限' }} 人</span>
          <span class="meta-pill">{{ completionLabel(activity.completionStatus) }}</span>
        </div>
      </div>
    </section>

    <section class="grid-two detail-layout">
      <article class="section-panel detail-article">
        <section class="detail-block">
          <h2>活动简介</h2>
          <p>{{ activity.summary }}</p>
        </section>

        <section class="detail-block">
          <h2>活动内容</h2>
          <p>{{ activity.detail }}</p>
        </section>

        <section class="detail-block">
          <h2>参与要求</h2>
          <p>{{ activity.requirement }}</p>
        </section>

        <section class="detail-block">
          <h2>活动结束</h2>
          <div class="inline-meta">
            <span class="meta-pill">{{ completionLabel(activity.completionStatus) }}</span>
            <span v-if="activity.completionSubmittedAt" class="meta-pill">提交于 {{ activity.completionSubmittedAt }}</span>
            <span v-if="activity.completionReviewedAt" class="meta-pill">确认于 {{ activity.completionReviewedAt }}</span>
          </div>
          <p v-if="activity.completionSummary">{{ activity.completionSummary }}</p>
          <p v-else>当前还没有提交活动结束材料。</p>
          <div v-if="activity.completionImages.length" class="detail-gallery">
            <img v-for="image in activity.completionImages" :key="image" :src="image" alt="活动结束照片" />
          </div>
          <blockquote v-if="activity.completionReviewComment" class="detail-quote">
            指导意见：{{ activity.completionReviewComment }}
          </blockquote>
        </section>
      </article>

      <aside class="detail-side">
        <section class="section-panel">
          <h2 class="page-title page-title--section">活动信息</h2>
          <div class="detail-side__list">
            <span>所属社团：{{ activity.clubName }}</span>
            <span>活动时间：{{ activity.activeTime }}</span>
            <span>活动地点：{{ activity.location }}</span>
            <span>报名人数：{{ activity.total }}/{{ activity.capacity || '不限' }}</span>
          </div>
          <div class="card-footer">
            <el-button plain @click="router.push(`/clubs/${activity.clubId}`)">查看社团</el-button>
            <el-button v-if="activity.joined && !activity.manageable" type="danger" plain @click="cancelSignup">取消报名</el-button>
            <el-button v-else-if="!activity.joined" type="primary" @click="signup">立即报名</el-button>
          </div>
        </section>

        <section v-if="activity.canSubmitCompletion" class="section-panel">
          <h2 class="page-title page-title--section">提交结束材料</h2>
          <el-form label-position="top" :model="completionForm">
            <el-form-item label="活动总结">
              <el-input v-model="completionForm.summary" type="textarea" :rows="4" placeholder="填写收尾情况、成果和复盘" />
            </el-form-item>
            <el-form-item label="结束照片">
              <div class="completion-upload">
                <input ref="uploadInput" type="file" accept="image/*" multiple class="completion-upload__input" @change="handleCompletionImages" />
                <el-button :loading="completionUploading" plain @click="uploadInput?.click()">上传照片</el-button>
                <div v-if="completionForm.images.length" class="detail-gallery">
                  <img v-for="image in completionForm.images" :key="image" :src="image" alt="结束照片预览" />
                </div>
              </div>
            </el-form-item>
          </el-form>
          <el-button type="primary" @click="submitCompletion">提交结束材料</el-button>
        </section>

        <section v-if="activity.canReviewCompletion" class="section-panel">
          <h2 class="page-title page-title--section">教师确认</h2>
          <el-form label-position="top" :model="reviewForm">
            <el-form-item label="指导意见">
              <el-input v-model="reviewForm.comment" type="textarea" :rows="4" placeholder="填写对活动结束材料的确认意见" />
            </el-form-item>
          </el-form>
          <div class="toolbar">
            <el-button type="danger" plain @click="reviewCompletion(false)">退回补充</el-button>
            <el-button type="primary" @click="reviewCompletion(true)">确认通过</el-button>
          </div>
        </section>
      </aside>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  cancelSignupActivity,
  getActivityDetail,
  reviewActivityCompletion,
  signupActivity,
  submitActivityCompletion,
  uploadActivityImage,
  type ActivitySummary,
} from '../api/activity'
import { pickDefaultCover } from '../content/default-covers'

const route = useRoute()
const router = useRouter()
const uploadInput = ref<HTMLInputElement | null>(null)
const activity = ref<ActivitySummary | null>(null)
const completionUploading = ref(false)

const completionForm = reactive({
  summary: '',
  images: [] as string[],
})

const reviewForm = reactive({
  comment: '',
})

function coverFor(item: ActivitySummary) {
  return item.coverImage || pickDefaultCover(item.id)
}

function completionLabel(status: number) {
  switch (status) {
    case 1:
      return '待教师确认'
    case 2:
      return '已确认'
    case 3:
      return '需补充'
    default:
      return '进行中'
  }
}

async function loadActivity() {
  try {
    const result = await getActivityDetail(String(route.params.activityId))
    if (result.code !== 0) throw new Error(result.msg)
    activity.value = result.data
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '加载活动详情失败')
    router.push('/activities')
  }
}

async function signup() {
  if (!activity.value) return
  try {
    const result = await signupActivity(activity.value.id)
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '报名成功')
    await loadActivity()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '报名失败')
  }
}

async function cancelSignup() {
  if (!activity.value) return
  try {
    const result = await cancelSignupActivity(activity.value.id)
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '已取消报名')
    await loadActivity()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '取消报名失败')
  }
}

async function handleCompletionImages(event: Event) {
  const input = event.target as HTMLInputElement
  const files = Array.from(input.files || [])
  if (!files.length) return

  try {
    completionUploading.value = true
    const uploaded: string[] = []
    for (const file of files) {
      const result = await uploadActivityImage(file)
      if (result.code !== 0) throw new Error(result.msg)
      uploaded.push(result.data.url)
    }
    completionForm.images = [...completionForm.images, ...uploaded]
    ElMessage.success('活动结束照片上传成功')
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '上传活动照片失败')
  } finally {
    completionUploading.value = false
    if (uploadInput.value) uploadInput.value.value = ''
  }
}

async function submitCompletion() {
  if (!activity.value) return
  try {
    const result = await submitActivityCompletion(activity.value.id, completionForm)
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '活动结束材料已提交')
    completionForm.summary = ''
    completionForm.images = []
    await loadActivity()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '提交结束失败')
  }
}

async function reviewCompletion(approved: boolean) {
  if (!activity.value) return
  try {
    const result = await reviewActivityCompletion(activity.value.id, {
      approved,
      comment: reviewForm.comment,
    })
    if (result.code !== 0) throw new Error(result.msg)
    ElMessage.success(result.msg || '活动结束审核已完成')
    reviewForm.comment = ''
    await loadActivity()
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '审核结束材料失败')
  }
}

onMounted(() => {
  void loadActivity()
})
</script>

<style scoped>
.activity-detail-cover img {
  height: 360px;
}

.activity-detail-cover__body {
  display: grid;
  gap: 14px;
  padding: 22px;
}

.detail-layout {
  align-items: start;
}

.detail-article,
.detail-side {
  display: grid;
  gap: 18px;
}

.detail-block {
  display: grid;
  gap: 12px;
}

.detail-block h2,
.page-title--section {
  margin: 0;
  font-family: var(--font-heading);
  color: var(--ink);
}

.detail-block h2 {
  font-size: 32px;
}

.page-title--section {
  font-size: 28px;
  margin-bottom: 16px;
}

.detail-block p,
.detail-side__list span {
  margin: 0;
  line-height: 1.85;
  color: var(--ink-soft);
}

.detail-side__list {
  display: grid;
  gap: 8px;
}

.detail-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
}

.detail-gallery img {
  width: 100%;
  height: 132px;
  object-fit: cover;
  border: 1px solid var(--line);
  border-radius: 10px;
}

.detail-quote {
  margin: 0;
  padding: 14px 16px;
  border-left: 3px solid var(--terracotta);
  background: rgba(243, 225, 215, 0.24);
  color: var(--ink);
}

.completion-upload {
  display: grid;
  gap: 12px;
}

.completion-upload__input {
  display: none;
}
</style>
