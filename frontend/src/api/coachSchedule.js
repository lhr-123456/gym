import request from '@/utils/request'

export function getCoachScheduleList(params) {
  return request({
    url: '/coach-schedule/list',
    method: 'get',
    params
  })
}

export function getCoachSchedulePage(params) {
  return request({
    url: '/coach-schedule/page',
    method: 'get',
    params
  })
}

export function getCoachScheduleById(id) {
  return request({
    url: `/coach-schedule/${id}`,
    method: 'get'
  })
}

export function addCoachSchedule(data) {
  return request({
    url: '/coach-schedule',
    method: 'post',
    data
  })
}

export function updateCoachSchedule(data) {
  return request({
    url: '/coach-schedule',
    method: 'put',
    data
  })
}

export function deleteCoachSchedule(id) {
  return request({
    url: `/coach-schedule/${id}`,
    method: 'delete'
  })
}
