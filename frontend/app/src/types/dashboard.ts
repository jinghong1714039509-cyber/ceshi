export interface DashboardMetrics {
  totalUsers: number
  activeClubs: number
  monthlyActivities: number
  pendingFunds: number
}

export interface TrendPoint {
  label: string
  value: number
}

export interface RankItem {
  name: string
  value: number
  extra?: string
}

export interface DashboardQuickAction {
  title: string
  description: string
  path: string
  permission?: string
}

export interface DashboardTodoItem {
  title: string
  value: number
  tone: 'warning' | 'danger' | 'primary' | 'success'
  path: string
}

export interface DashboardMetricsCard {
  key: string
  label: string
  value: number
  delta: string
  tone: 'orange' | 'green' | 'blue' | 'gold'
}

export interface DashboardSummary {
  metrics: DashboardMetrics
  cards: DashboardMetricsCard[]
  signupsTrend: TrendPoint[]
  approvalTrend: TrendPoint[]
  activeClubRanking: RankItem[]
  clubCategoryDistribution: RankItem[]
  completionOverview: RankItem[]
  pendingItems: DashboardTodoItem[]
  quickActions: DashboardQuickAction[]
}
