import Vue from 'vue'
import Router from 'vue-router'
import store from '@/store'
import { getToken } from '@/utils/auth'

Vue.use(Router)

export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },
  {
    path: '/',
    redirect: '/dashboard/index',
    hidden: true
  },
  // 管理员路由
  {
    path: '/dashboard',
    component: () => import('@/views/layout/index'),
    redirect: '/dashboard/index',
    hidden: true,
    children: [
      {
        path: 'index',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: { title: '首页', icon: 'dashboard' }
      }
    ]
  },
  {
    path: '/member',
    component: () => import('@/views/layout/index'),
    redirect: '/member/list',
    meta: { title: '会员管理', icon: 'user' },
    children: [
      {
        path: 'list',
        component: () => import('@/views/member/list'),
        name: 'MemberList',
        meta: { title: '会员列表', icon: 'list' }
      }
    ]
  },
  {
    path: '/coach',
    component: () => import('@/views/layout/index'),
    redirect: '/coach/list',
    meta: { title: '教练管理', icon: 'user' },
    children: [
      {
        path: 'list',
        component: () => import('@/views/coach/list'),
        name: 'CoachList',
        meta: { title: '教练列表', icon: 'list' }
      }
    ]
  },
  {
    path: '/course',
    component: () => import('@/views/layout/index'),
    redirect: '/course/list',
    meta: { title: '课程管理', icon: 'documentation' },
    children: [
      {
        path: 'list',
        component: () => import('@/views/course/list'),
        name: 'CourseList',
        meta: { title: '课程列表', icon: 'list' }
      },
      {
        path: 'my',
        component: () => import('@/views/course/my'),
        name: 'MyCourse',
        meta: { title: '我的课程', icon: 'list' }
      },
      {
        path: 'available',
        component: () => import('@/views/course/available'),
        name: 'AvailableCourse',
        meta: { title: '可预约课程', icon: 'list' }
      }
    ]
  },
  {
    path: '/equipment',
    component: () => import('@/views/layout/index'),
    redirect: '/equipment/list',
    meta: { title: '器材管理', icon: 'nested' },
    children: [
      {
        path: 'list',
        component: () => import('@/views/equipment/list'),
        name: 'EquipmentList',
        meta: { title: '器材列表', icon: 'list' }
      }
    ]
  },
  {
    path: '/profile',
    component: () => import('@/views/layout/index'),
    redirect: '/profile/index',
    meta: { title: '个人中心', icon: 'user' },
    children: [
      {
        path: 'index',
        component: () => import('@/views/profile/index'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  }
]

const createRouter = () => new Router({
  mode: 'hash',
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// 路由守卫
router.beforeEach((to, from, next) => {
  const hasToken = getToken()

  if (hasToken) {
    if (to.path === '/login') {
      // 已登录用户访问登录页，跳转到首页
      next({ path: '/' })
    } else {
      // 检查是否有用户信息
      if (store.getters.userInfo && store.getters.userInfo.userType) {
        next()
      } else {
        // 获取用户信息
        store.dispatch('user/getInfo').then(() => {
          next()
        }).catch(() => {
          store.dispatch('user/logout')
          next(`/login?redirect=${to.path}`)
        })
      }
    }
  } else {
    // 没有token
    if (to.path === '/login' || to.path === '/404') {
      next()
    } else {
      // 跳转到登录页
      next(`/login?redirect=${to.path}`)
    }
  }
})

export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher
}

export default router
