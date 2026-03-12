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
    meta: { title: '会员管理', icon: 'user', roles: [1] },
    children: [
      {
        path: 'list',
        component: () => import('@/views/member/list'),
        name: 'MemberList',
        meta: { title: '会员列表', icon: 'list', roles: [1] }
      },
      {
        path: 'level',
        component: () => import('@/views/member/level'),
        name: 'MemberLevel',
        meta: { title: '会员等级', icon: 'medal', roles: [1] }
      },
      {
        path: 'card',
        component: () => import('@/views/member/card'),
        name: 'MemberCard',
        meta: { title: '会员卡管理', icon: 'credit-card', roles: [1] }
      },
      {
        path: 'signin',
        component: () => import('@/views/member/signin'),
        name: 'MemberSignin',
        meta: { title: '会员签到', icon: 'calendar', roles: [1] }
      },
      {
        path: 'bodyTest',
        component: () => import('@/views/member/bodyTest'),
        name: 'MemberBodyTest',
        meta: { title: '体测记录', icon: 'data-analysis', roles: [1] }
      },
      {
        path: 'consumption',
        component: () => import('@/views/member/consumption'),
        name: 'MemberConsumption',
        meta: { title: '消费记录', icon: 'shopping-cart', roles: [1] }
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
      },
      {
        path: 'certificate',
        component: () => import('@/views/coach/certificate'),
        name: 'CoachCertificate',
        meta: { title: '教练证书', icon: 'peoples' }
      },
      {
        path: 'specialty',
        component: () => import('@/views/coach/specialty'),
        name: 'CoachSpecialty',
        meta: { title: '教练专长', icon: 'star' }
      },
      {
        path: 'schedule',
        component: () => import('@/views/coach/schedule'),
        name: 'CoachSchedule',
        meta: { title: '教练排班', icon: 'date' }
      },
      {
        path: 'shift',
        component: () => import('@/views/coach/shift'),
        name: 'CoachShift',
        meta: { title: '教练调班', icon: 'swap' }
      },
      {
        path: 'salary',
        component: () => import('@/views/coach/salary'),
        name: 'CoachSalary',
        meta: { title: '教练工资', icon: 'money' }
      },
      {
        path: 'performance',
        component: () => import('@/views/coach/performance'),
        name: 'CoachPerformance',
        meta: { title: '教练绩效', icon: 'chart' }
      },
      {
        path: 'review',
        component: () => import('@/views/coach/review'),
        name: 'CoachReview',
        meta: { title: '教练评价', icon: 'comment' }
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
        path: 'category',
        component: () => import('@/views/course/category'),
        name: 'CourseCategory',
        meta: { title: '课程分类', icon: 'folder' }
      },
      {
        path: 'schedule',
        component: () => import('@/views/course/schedule'),
        name: 'CourseSchedule',
        meta: { title: '课程排期', icon: 'date' }
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
      },
      {
        path: 'booking',
        component: () => import('@/views/course/booking'),
        name: 'CourseBooking',
        meta: { title: '课程预约', icon: 'date' }
      },
      {
        path: 'review',
        component: () => import('@/views/course/review'),
        name: 'CourseReview',
        meta: { title: '课程评价', icon: 'comment' }
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
      },
      {
        path: 'booking',
        component: () => import('@/views/equipment/booking'),
        name: 'EquipmentBooking',
        meta: { title: '器材预约', icon: 'date' }
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
