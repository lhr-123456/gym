import request from '@/utils/request'

export function getCoachPerformanceList(params) {
  return request({
    url: '/coach-performance/list',
    method: 'get',
    params
  })
}

export function getCoachPerformancePage(params) {
  return request({
    url: '/coach-performance/page',
    method: 'get',
    params
  })
}

export function getCoachPerformanceById(id) {
  return request({
    url: `/coach-performance/${id}`,
    method: 'get'
  })
}

export function addCoachPerformance(data) {
  return request({
    url: '/coach-performance',
    method: 'post',
    data
  })
}

export function updateCoachPerformance(data) {
  return request({
    url: '/coach-performance',
    method: 'put',
    data
  })
}

export function deleteCoachPerformance(id) {
  return request({
    url: `/coach-performance/${id}`,
    method: 'delete'
  })
}
