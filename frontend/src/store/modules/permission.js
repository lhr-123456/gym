import { constantRoutes } from '@/router'

const state = {
  routes: [],
  addRoutes: []
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes
    state.routes = constantRoutes.concat(routes)
  }
}

const actions = {
  generateRoutes({ commit }, userType) {
    return new Promise(resolve => {
      let accessedRoutes
      if (userType === 1) {
        accessedRoutes = adminRoutes
      } else if (userType === 2) {
        accessedRoutes = coachRoutes
      } else {
        accessedRoutes = memberRoutes
      }
      commit('SET_ROUTES', accessedRoutes)
      resolve(accessedRoutes)
    })
  }
}

const adminRoutes = [
  {
    path: '/dashboard',
    component: () => import('@/views/layout/index'),
    redirect: '/dashboard/index',
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
  }
]

const coachRoutes = [
  {
    path: '/coach-dashboard',
    component: () => import('@/views/layout/index'),
    redirect: '/coach-dashboard/index',
    meta: { title: '工作台', icon: 'dashboard' },
    children: [
      {
        path: 'index',
        component: () => import('@/views/coach/dashboard'),
        name: 'CoachDashboard',
        meta: { title: '工作台', icon: 'dashboard' }
      }
    ]
  },
  {
    path: '/coach-schedule',
    component: () => import('@/views/layout/index'),
    redirect: '/coach-schedule/list',
    meta: { title: '我的排课', icon: 'date' },
    children: [
      {
        path: 'list',
        component: () => import('@/views/coach/mySchedule'),
        name: 'CoachMySchedule',
        meta: { title: '排课列表', icon: 'list' }
      }
    ]
  },
  {
    path: '/coach-members',
    component: () => import('@/views/layout/index'),
    redirect: '/coach-members/list',
    meta: { title: '学员管理', icon: 'user' },
    children: [
      {
        path: 'list',
        component: () => import('@/views/coach/members'),
        name: 'CoachMembers',
        meta: { title: '我的学员', icon: 'list' }
      }
    ]
  },
  {
    path: '/coach-reviews',
    component: () => import('@/views/layout/index'),
    redirect: '/coach-reviews/list',
    meta: { title: '课程评价', icon: 'comment' },
    children: [
      {
        path: 'list',
        component: () => import('@/views/coach/myReviews'),
        name: 'CoachMyReviews',
        meta: { title: '我的评价', icon: 'list' }
      }
    ]
  },
  {
    path: '/coach-bodytest',
    component: () => import('@/views/layout/index'),
    redirect: '/coach-bodytest/add',
    meta: { title: '体测管理', icon: 'data-analysis' },
    children: [
      {
        path: 'add',
        component: () => import('@/views/coach/bodyTest'),
        name: 'CoachBodyTest',
        meta: { title: '录入体测', icon: 'plus' }
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

const memberRoutes = [
  {
    path: '/dashboard',
    component: () => import('@/views/layout/index'),
    redirect: '/dashboard/index',
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
    path: '/course',
    component: () => import('@/views/layout/index'),
    redirect: '/course/available',
    meta: { title: '课程预约', icon: 'documentation' },
    children: [
      {
        path: 'available',
        component: () => import('@/views/course/available'),
        name: 'AvailableCourse',
        meta: { title: '可预约课程', icon: 'list' }
      }
    ]
  }
]

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
