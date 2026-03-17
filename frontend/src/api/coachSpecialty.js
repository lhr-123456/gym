import request from '@/utils/request'

export function getCoachSpecialtyList(params) {
  return request({
    url: '/coach-specialty/list',
    method: 'get',
    params
  })
}

export function getCoachSpecialtyPage(params) {
  return request({
    url: '/coach-specialty/page',
    method: 'get',
    params
  })
}

export function getCoachSpecialtyById(id) {
  return request({
    url: `/coach-specialty/${id}`,
    method: 'get'
  })
}

export function addCoachSpecialty(data) {
  return request({
    url: '/coach-specialty',
    method: 'post',
    data
  })
}

export function updateCoachSpecialty(data) {
  return request({
    url: '/coach-specialty',
    method: 'put',
    data
  })
}

export function deleteCoachSpecialty(id) {
  return request({
    url: `/coach-specialty/${id}`,
    method: 'delete'
  })
}
