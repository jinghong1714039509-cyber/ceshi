import { createRouter, createWebHistory } from 'vue-router'
import { useSessionStore } from '../stores/session'
import ActivitiesView from '../views/ActivitiesView.vue'
import ActivityDetailView from '../views/ActivityDetailView.vue'
import ClubApplicationsView from '../views/ClubApplicationsView.vue'
import ClubDetailView from '../views/ClubDetailView.vue'
import ClubsView from '../views/ClubsView.vue'
import ClubSpaceView from '../views/ClubSpaceView.vue'
import DashboardView from '../views/DashboardView.vue'
import ExploreView from '../views/ExploreView.vue'
import ForumView from '../views/ForumView.vue'
import LoginView from '../views/LoginView.vue'
import MessagesView from '../views/MessagesView.vue'
import MembersView from '../views/MembersView.vue'
import NoticesView from '../views/NoticesView.vue'
import ProfileView from '../views/ProfileView.vue'
import RegisterView from '../views/RegisterView.vue'
import UsersView from '../views/UsersView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'dashboard', component: DashboardView, meta: { requiresAuth: true } },
    { path: '/clubs', name: 'clubs', component: ClubsView, meta: { requiresAuth: true } },
    { path: '/clubs/:clubId', name: 'club-detail', component: ClubDetailView, meta: { requiresAuth: true } },
    { path: '/clubs/:clubId/space', name: 'club-space', component: ClubSpaceView, meta: { requiresAuth: true } },
    { path: '/join-applications', name: 'join-applications', component: ClubApplicationsView, meta: { requiresAuth: true } },
    { path: '/activities', name: 'activities', component: ActivitiesView, meta: { requiresAuth: true } },
    { path: '/activities/:activityId', name: 'activity-detail', component: ActivityDetailView, meta: { requiresAuth: true } },
    { path: '/forum', name: 'forum', component: ForumView, meta: { requiresAuth: true } },
    { path: '/messages', name: 'messages', component: MessagesView, meta: { requiresAuth: true } },
    { path: '/members', name: 'members', component: MembersView, meta: { requiresAuth: true } },
    { path: '/notices', name: 'notices', component: NoticesView, meta: { requiresAuth: true } },
    { path: '/login', name: 'login', component: LoginView },
    { path: '/register', name: 'register', component: RegisterView },
    { path: '/users', name: 'users', component: UsersView, meta: { requiresAuth: true } },
    { path: '/explore', name: 'explore', component: ExploreView },
    { path: '/profile', name: 'profile', component: ProfileView, meta: { requiresAuth: true } },
  ],
})

router.beforeEach((to) => {
  const session = useSessionStore()

  if (to.meta.requiresAuth && !session.isAuthenticated) {
    return { name: 'login' }
  }

  if (to.name === 'login' && session.isAuthenticated) {
    return { name: 'dashboard' }
  }

  return true
})

export default router
