<template>
  <el-dialog v-model="model" title="发布活动" width="760px" @closed="emit('closed')">
    <el-form label-position="top" :model="form">
      <div class="form-grid">
        <el-form-item label="所属社团">
          <el-select v-model="form.clubId" placeholder="请选择社团">
            <el-option v-for="club in clubs" :key="club.id" :label="club.name" :value="club.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="活动时间">
          <el-input v-model="form.activeTime" placeholder="例如：2026-04-20 18:30:00" />
        </el-form-item>
        <el-form-item label="活动地点">
          <el-input v-model="form.location" placeholder="例如：综合楼中庭" />
        </el-form-item>
        <el-form-item label="人数上限">
          <el-input-number v-model="form.capacity" :min="1" :max="500" class="full-width" />
        </el-form-item>
        <el-form-item label="默认封面">
          <el-select v-model="form.coverImage" clearable placeholder="不选则自动使用默认校园封面">
            <el-option v-for="cover in defaultCoverImages" :key="cover" :label="cover" :value="cover" />
          </el-select>
        </el-form-item>
      </div>

      <el-form-item label="上传封面">
        <div class="creator-cover">
          <input ref="uploadInput" type="file" accept="image/*" class="creator-cover__input" @change="handleCoverChange" />
          <div class="toolbar">
            <el-button :loading="uploading" plain @click="uploadInput?.click()">上传图片</el-button>
            <el-button v-if="uploadedCoverUrl" plain @click="clearUploadedCover">清除上传</el-button>
          </div>
          <div v-if="coverPreview" class="creator-cover__preview">
            <img :src="coverPreview" alt="封面预览" />
            <span>{{ uploadedCoverName || '已选择封面' }}</span>
          </div>
        </div>
      </el-form-item>

      <el-form-item label="活动简介">
        <el-input v-model="form.summary" />
      </el-form-item>
      <el-form-item label="活动详情">
        <el-input v-model="form.detail" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item label="参与要求">
        <el-input v-model="form.requirement" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="model = false">取消</el-button>
      <el-button type="primary" @click="submit">发布</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createActivity, uploadActivityCover } from '../../../api/activity'
import { defaultCoverImages } from '../../../content/default-covers'
import type { ClubSummary } from '../../../types/club'

const model = defineModel<boolean>({ required: true })
defineProps<{ clubs: ClubSummary[] }>()

const emit = defineEmits<{ success: []; closed: [] }>()
const uploadInput = ref<HTMLInputElement | null>(null)
const uploading = ref(false)
const coverPreview = ref('')
const uploadedCoverUrl = ref('')
const uploadedCoverName = ref('')

const form = reactive({
  clubId: '',
  name: '',
  summary: '',
  detail: '',
  requirement: '',
  activeTime: '',
  location: '',
  capacity: 20,
  coverImage: '',
})

function resetForm() {
  Object.assign(form, {
    clubId: '',
    name: '',
    summary: '',
    detail: '',
    requirement: '',
    activeTime: '',
    location: '',
    capacity: 20,
    coverImage: '',
  })
  coverPreview.value = ''
  uploadedCoverUrl.value = ''
  uploadedCoverName.value = ''
}

function clearUploadedCover() {
  uploadedCoverUrl.value = ''
  uploadedCoverName.value = ''
  coverPreview.value = ''
}

async function handleCoverChange(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  try {
    uploading.value = true
    const result = await uploadActivityCover(file)
    if (result.code !== 0) throw new Error(result.message)
    uploadedCoverUrl.value = result.data.url
    uploadedCoverName.value = result.data.name
    coverPreview.value = result.data.url
    ElMessage.success('封面上传成功')
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '封面上传失败')
  } finally {
    uploading.value = false
    if (uploadInput.value) uploadInput.value.value = ''
  }
}

async function submit() {
  try {
    const result = await createActivity({
      ...form,
      coverImage: uploadedCoverUrl.value || form.coverImage || undefined,
    })
    if (result.code !== 0) throw new Error(result.message)
    ElMessage.success(result.message || '发布成功')
    model.value = false
    resetForm()
    emit('success')
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '发布活动失败')
  }
}
</script>

<style scoped>
.creator-cover {
  display: grid;
  gap: 12px;
}

.creator-cover__input {
  display: none;
}

.creator-cover__preview {
  display: grid;
  gap: 8px;
  color: var(--ink-soft);
}

.creator-cover__preview img {
  width: 240px;
  height: 150px;
  object-fit: cover;
  border: 1px solid var(--line);
  border-radius: 12px;
}
</style>
