import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

// Lazy-load pages/components
const Home = () => import('../pages/Home.vue')
const GaokaoMockFill = () => import('../components/GaokaoMockFill.vue')
const GradAdvisorSelect = () => import('../components/GradAdvisorSelect.vue')
const SchoolInfo = () => import('../components/SchoolInfo.vue')

const routes: RouteRecordRaw[] = [
  { path: '/', name: 'Home', component: Home },
  { path: '/gaokao', name: 'GaokaoMockFill', component: GaokaoMockFill, meta: { title: '高考模拟填报' } },
  { path: '/grad', name: 'GradAdvisorSelect', component: GradAdvisorSelect, meta: { title: '研究生择导' } },
  { path: '/school', name: 'SchoolInfo', component: SchoolInfo, meta: { title: '学校信息' } },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

export default router
