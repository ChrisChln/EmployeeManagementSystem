import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import DepartmentManagement from '../views/DepartmentManagement.vue'
import Login from '../views/Login.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/department-management',
    name: 'DepartmentManagement',
    component: DepartmentManagement,
    meta: { requiresAuth: true }
  },
  {
    path: '/employee-management',
    name: 'EmployeeManagement',
    component: () => import('../views/EmployeeManagement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/employee-onboarding',
    name: 'EmployeeOnboarding',
    component: () => import('../views/EmployeeOnboarding.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/position-management',
    name: 'PositionManagement',
    component: () => import('../views/PositionManagement.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 添加路由守卫，检查用户是否已登录
router.beforeEach((to, from, next) => {
  // 检查路由是否需要认证
  if (to.meta.requiresAuth !== false) {
    // 获取token
    const token = localStorage.getItem('token')
    
    if (!token) {
      // 如果没有token，重定向到登录页面
      next({ name: 'Login' })
    } else {
      // 有token，继续导航
      next()
    }
  } else {
    // 不需要认证的路由，直接通过
    next()
  }
})

export default router 