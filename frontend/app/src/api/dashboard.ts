import { getActivities } from './activity'
import { getClubs, getManagedApplications } from './club'
import { getMessageContacts } from './messages'
import { getUsers } from './user'
import type { ActivitySummary } from '../types/activity'
import type { ClubSummary, JoinApplication } from '../types/club'
import type { MessageContact } from '../types/message'
import type { UserItem } from '../types/system'
import type {
  DashboardMetricsCard,
  DashboardQuickAction,
  DashboardSummary,
  DashboardTodoItem,
  RankItem,
  TrendPoint,
} from '../types/dashboard'

function parseDate(value: string) {
  const safe = value?.replace(' ', 'T')
  const date = safe ? new Date(safe) : new Date('')
  return Number.isNaN(date.getTime()) ? null : date
}

function buildRecentDays(days: number) {
  const today = new Date()
  today.setHours(0, 0, 0, 0)

  return Array.from({ length: days }).map((_, index) => {
    const date = new Date(today)
    date.setDate(today.getDate() - (days - index - 1))
    const label = `${date.getMonth() + 1}/${date.getDate()}`
    const key = date.toISOString().slice(0, 10)
    return { key, label }
  })
}

function buildDailyTrend(
  activities: ActivitySummary[],
  days: number,
  valueResolver: (activity: ActivitySummary) => number,
): TrendPoint[] {
  const buckets = new Map(buildRecentDays(days).map((item) => [item.key, { label: item.label, value: 0 }]))

  activities.forEach((activity) => {
    const date = parseDate(activity.activeTime)
    if (!date) return

    const key = date.toISOString().slice(0, 10)
    const bucket = buckets.get(key)
    if (!bucket) return
    bucket.value += valueResolver(activity)
  })

  return Array.from(buckets.values())
}

function buildApprovalTrend(applications: JoinApplication[], days: number): TrendPoint[] {
  const buckets = new Map(buildRecentDays(days).map((item) => [item.key, { label: item.label, value: 0 }]))

  applications.forEach((item) => {
    const date = parseDate(item.createdAt)
    if (!date) return

    const key = date.toISOString().slice(0, 10)
    const bucket = buckets.get(key)
    if (!bucket) return
    bucket.value += 1
  })

  return Array.from(buckets.values())
}

function buildClubRanking(clubs: ClubSummary[], activities: ActivitySummary[]): RankItem[] {
  const activityCountMap = activities.reduce<Record<string, number>>((acc, item) => {
    acc[item.clubId] = (acc[item.clubId] ?? 0) + 1
    return acc
  }, {})

  return [...clubs]
    .sort((left, right) => {
      const leftScore = left.totalMembers * 3 + (activityCountMap[left.id] ?? 0) * 8
      const rightScore = right.totalMembers * 3 + (activityCountMap[right.id] ?? 0) * 8
      return rightScore - leftScore
    })
    .slice(0, 5)
    .map((club) => ({
      name: club.name,
      value: club.totalMembers,
      extra: `${activityCountMap[club.id] ?? 0} 场活动`,
    }))
}

function buildCategoryDistribution(clubs: ClubSummary[]): RankItem[] {
  const categoryMap = clubs.reduce<Record<string, number>>((acc, club) => {
    const category = club.category || '未分类'
    acc[category] = (acc[category] ?? 0) + club.totalMembers
    return acc
  }, {})

  return Object.entries(categoryMap)
    .map(([name, value]) => ({ name, value }))
    .sort((left, right) => right.value - left.value)
}

function buildCompletionOverview(activities: ActivitySummary[]): RankItem[] {
  return [
    { name: '进行中', value: activities.filter((item) => item.completionStatus === 0).length },
    { name: '待教师确认', value: activities.filter((item) => item.completionStatus === 1).length },
    { name: '已确认', value: activities.filter((item) => item.completionStatus === 2).length },
    { name: '需补充', value: activities.filter((item) => item.completionStatus === 3).length },
  ]
}

function buildMetricsCards(
  users: UserItem[],
  clubs: ClubSummary[],
  activities: ActivitySummary[],
  applications: JoinApplication[],
): DashboardMetricsCard[] {
  const currentMonth = new Date().getMonth()
  const monthlyActivities = activities.filter((item) => parseDate(item.activeTime)?.getMonth() === currentMonth).length
  const pendingApprovals = applications.filter((item) => item.status === 0).length

  return [
    {
      key: 'total-users',
      label: '总用户数',
      value: users.length,
      delta: `学生 ${users.filter((item) => item.roleLabel.includes('学生')).length} 人`,
      tone: 'blue',
    },
    {
      key: 'active-clubs',
      label: '活跃社团数',
      value: clubs.filter((item) => item.totalMembers > 0).length,
      delta: `已加入成员 ${clubs.filter((item) => item.joined).length} 个社团`,
      tone: 'green',
    },
    {
      key: 'monthly-activities',
      label: '本月活动数',
      value: monthlyActivities,
      delta: `待结束确认 ${activities.filter((item) => item.completionStatus === 1).length} 场`,
      tone: 'orange',
    },
    {
      key: 'pending-funds',
      label: '待审批事项',
      value: pendingApprovals,
      delta: `入社审批 ${pendingApprovals} 条`,
      tone: 'gold',
    },
  ]
}

function buildPendingItems(applications: JoinApplication[], contacts: MessageContact[], activities: ActivitySummary[]): DashboardTodoItem[] {
  return [
    {
      title: '待审核入社申请',
      value: applications.filter((item) => item.status === 0).length,
      tone: 'warning',
      path: '/join-applications',
    },
    {
      title: '未读私信',
      value: contacts.reduce((total, item) => total + item.unreadCount, 0),
      tone: 'danger',
      path: '/messages',
    },
    {
      title: '待教师确认活动',
      value: activities.filter((item) => item.completionStatus === 1).length,
      tone: 'primary',
      path: '/activities',
    },
    {
      title: '已完成待复盘',
      value: activities.filter((item) => item.completionStatus === 2).length,
      tone: 'success',
      path: '/activities',
    },
  ]
}

function buildQuickActions(): DashboardQuickAction[] {
  return [
    {
      title: '审批中心',
      description: '集中处理入社申请与活动结束确认',
      path: '/join-applications',
      permission: 'approval:view',
    },
    {
      title: '活动管理',
      description: '查看活动全周期与当前完成状态',
      path: '/activities',
      permission: 'activity:view',
    },
    {
      title: '社团管理',
      description: '查看自己管理的社团与成员情况',
      path: '/clubs',
      permission: 'club:view',
    },
    {
      title: '消息中心',
      description: '优先处理社团私信与管理消息',
      path: '/messages',
      permission: 'message:view',
    },
  ]
}

export async function getDashboardSummary(): Promise<DashboardSummary> {
  const [usersRes, clubsRes, activitiesRes, applicationsRes, contactsRes] = await Promise.all([
    getUsers(),
    getClubs(),
    getActivities(),
    getManagedApplications(),
    getMessageContacts(),
  ])

  const users = usersRes.code === 0 ? usersRes.data : []
  const clubs = clubsRes.code === 0 ? clubsRes.data : []
  const activities = activitiesRes.code === 0 ? activitiesRes.data : []
  const applications = applicationsRes.code === 0 ? applicationsRes.data : []
  const contacts = contactsRes.code === 0 ? contactsRes.data : []

  return {
    metrics: {
      totalUsers: users.length,
      activeClubs: clubs.filter((item) => item.totalMembers > 0).length,
      monthlyActivities: activities.filter((item) => parseDate(item.activeTime)?.getMonth() === new Date().getMonth()).length,
      pendingFunds: applications.filter((item) => item.status === 0).length,
    },
    cards: buildMetricsCards(users, clubs, activities, applications),
    signupsTrend: buildDailyTrend(activities, 30, (item) => item.total),
    approvalTrend: buildApprovalTrend(applications, 7),
    activeClubRanking: buildClubRanking(clubs, activities),
    clubCategoryDistribution: buildCategoryDistribution(clubs),
    completionOverview: buildCompletionOverview(activities),
    pendingItems: buildPendingItems(applications, contacts, activities),
    quickActions: buildQuickActions(),
  }
}
