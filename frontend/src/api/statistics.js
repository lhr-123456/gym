import request from '@/utils/request'

export function getDashboardStatistics() {
  return request({
    url: '/statistics/dashboard',
    method: 'get'
  })
}

export function getCoachSalarySummary(coachId) {
  return request({
    url: '/statistics/coach-salary-summary',
    method: 'get',
    params: coachId ? { coachId } : {}
  })
}

export function getCoachPerformanceSummary(coachId) {
  return request({
    url: '/statistics/coach-performance-summary',
    method: 'get',
    params: coachId ? { coachId } : {}
  })
}
