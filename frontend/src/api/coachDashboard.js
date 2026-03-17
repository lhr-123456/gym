import request from '@/utils/request'

// 获取教练首页统计数据
export function getCoachDashboard(coachId) {
  return request({
    url: '/coach/dashboard',
    method: 'get',
    params: { coachId }
  })
}

// 获取教练今日课程
export function getCoachTodayCourses(coachId) {
  return request({
    url: '/coach/courses/today',
    method: 'get',
    params: { coachId }
  })
}

// 获取教练今日学员
export function getCoachTodayMembers(coachId) {
  return request({
    url: '/coach/members/today',
    method: 'get',
    params: { coachId }
  })
}

// 获取教练学员列表（分页）
export function getCoachMembersPage(params) {
  return request({
    url: '/coach/members/page',
    method: 'get',
    params
  })
}

// 获取教练学员下拉列表（体测录入用）
export function getCoachMembersList() {
  return request({
    url: '/coach/members/list',
    method: 'get'
  })
}

// 开始上课
export function startCourse(bookingId) {
  return request({
    url: `/course-booking/start/${bookingId}`,
    method: 'put'
  })
}

// 课程签到
export function signinMember(bookingId) {
  return request({
    url: `/course-booking/signin/${bookingId}`,
    method: 'put'
  })
}

// 获取教练待办事项
export function getCoachTodos(coachId) {
  return request({
    url: '/coach/todos',
    method: 'get',
    params: { coachId }
  })
}

// 录入体测
export function addBodyTest(data) {
  return request({
    url: '/member/body-test',
    method: 'post',
    data
  })
}

// 获取教练评价
export function getCoachReviews(coachId) {
  return request({
    url: '/coach/reviews',
    method: 'get',
    params: { coachId }
  })
}

// 获取教练排课列表（分页）
export function getCoachSchedulePage(data) {
  return request({
    url: '/coach/schedule/page',
    method: 'get',
    params: data
  })
}

// 获取教练排课列表
export function getCoachScheduleList(data) {
  return request({
    url: '/coach/schedule/list',
    method: 'get',
    params: data
  })
}

// 获取排课预约详情
export function getScheduleBookings(scheduleId) {
  return request({
    url: `/coach/schedule/bookings/${scheduleId}`,
    method: 'get'
  })
}

// 学员签到
export function coachSignin(bookingId) {
  return request({
    url: `/coach/schedule/signin/${bookingId}`,
    method: 'put'
  })
}

// 开始课程
export function startSchedule(scheduleId) {
  return request({
    url: `/coach/schedule/start/${scheduleId}`,
    method: 'put'
  })
}

// 结束课程
export function endSchedule(scheduleId) {
  return request({
    url: `/coach/schedule/end/${scheduleId}`,
    method: 'put'
  })
}
