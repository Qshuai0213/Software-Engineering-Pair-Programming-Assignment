import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  { path: '/seats', name: 'SeatList', component: () => import('../views/SeatList.vue'), meta: { auth: true } },
  { path: '/seats/:id', name: 'SeatDetail', component: () => import('../views/SeatDetail.vue'), meta: { auth: true } },
  { path: '/my-reservations', name: 'MyReservations', component: () => import('../views/MyReservations.vue'), meta: { auth: true } },
  { path: '/admin', name: 'Admin', component: () => import('../views/Admin.vue'), meta: { auth: true, admin: true } },
  { path: '/', redirect: '/seats' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.auth && !userStore.token) {
    next('/login')
  } else if (to.meta.admin && userStore.role !== 'admin') {
    next('/seats')
  } else {
    next()
  }
})

export default router
