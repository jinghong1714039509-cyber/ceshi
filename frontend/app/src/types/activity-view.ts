import type { ActivitySummary } from './activity'

export type ActivityViewMode = 'table' | 'card'
export type ActivitySignupFilter = 'all' | 'joined' | 'not-joined' | 'manageable'
export type ActivitySortKey = 'activeTime' | 'total' | 'capacity' | 'name'
export type ActivitySortOrder = 'ascending' | 'descending' | null
export type ActivityLifecycleStage = 'preparing' | 'promotion' | 'running' | 'review'

export interface ActivityFilters {
  keyword: string
  clubId: string
  status: string
  signup: ActivitySignupFilter
}

export interface ActivityColumnState {
  clubName: boolean
  activeTime: boolean
  location: boolean
  capacity: boolean
  total: boolean
  completionStatus: boolean
}

export interface ActivityLifecycleBlock {
  id: string
  activityId: string
  name: string
  clubName: string
  location: string
  activeTime: string
  summary: string
  stage: ActivityLifecycleStage
  stageLabel: string
  stageOffset: number
  stageSpan: number
  completionStatus: number
  total: number
  capacity: number
  manageable: boolean
}

export function getLifecycleStage(activity: ActivitySummary): ActivityLifecycleStage {
  if (activity.completionStatus > 0) {
    return 'review'
  }

  const activityDate = new Date(activity.activeTime.replace(' ', 'T'))
  const now = new Date()
  const diffDays = (activityDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24)

  if (diffDays > 5) return 'preparing'
  if (diffDays > 1) return 'promotion'
  return 'running'
}

export function getLifecycleStageLabel(stage: ActivityLifecycleStage) {
  switch (stage) {
    case 'preparing':
      return '筹备'
    case 'promotion':
      return '宣传'
    case 'running':
      return '执行中'
    case 'review':
      return '复盘'
  }
}

export function buildLifecycleBlocks(activities: ActivitySummary[]): ActivityLifecycleBlock[] {
  return activities.map((activity) => {
    const stage = getLifecycleStage(activity)
    const activityDate = new Date(activity.activeTime.replace(' ', 'T'))
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    const startOfWeek = new Date(today)
    startOfWeek.setDate(today.getDate() - today.getDay() + 1)
    const offset = Math.max(0, Math.min(27, Math.floor((activityDate.getTime() - startOfWeek.getTime()) / (1000 * 60 * 60 * 24))))
    const span = stage === 'preparing' ? 6 : stage === 'promotion' ? 4 : stage === 'running' ? 2 : 3

    return {
      id: activity.id,
      activityId: activity.id,
      name: activity.name,
      clubName: activity.clubName,
      location: activity.location,
      activeTime: activity.activeTime,
      summary: activity.summary,
      stage,
      stageLabel: getLifecycleStageLabel(stage),
      stageOffset: offset,
      stageSpan: span,
      completionStatus: activity.completionStatus,
      total: activity.total,
      capacity: activity.capacity,
      manageable: activity.manageable,
    }
  })
}
