import request from '@/utils/request'

export function getCoachShiftList(params) {
  return request({
    url: '/coach-shift/list',
    method: 'get',
    params
  })
}

export function getCoachShiftPage(params) {
  return request({
    url: '/coach-shift/page',
    method: 'get',
    params
  })
}

export function getCoachShiftById(id) {
  return request({
    url: `/coach-shift/${id}`,
    method: 'get'
  })
}

export function addCoachShift(data) {
  return request({
    url: '/coach-shift',
    method: 'post',
    data
  })
}

export function updateCoachShift(data) {
  return request({
    url: '/coach-shift',
    method: 'put',
    data
  })
}

export function deleteCoachShift(id) {
  return request({
    url: `/coach-shift/${id}`,
    method: 'delete'
  })
}
