import request from '@/utils/request'

export function getMemberLevelList() {
  return request({
    url: '/member/level/list',
    method: 'get'
  })
}

export function getMemberLevelPage(params) {
  return request({
    url: '/member/level/page',
    method: 'get',
    params
  })
}

export function getMemberLevel(id) {
  return request({
    url: `/member/level/${id}`,
    method: 'get'
  })
}

export function addMemberLevel(data) {
  return request({
    url: '/member/level',
    method: 'post',
    data
  })
}

export function updateMemberLevel(data) {
  return request({
    url: '/member/level',
    method: 'put',
    data
  })
}

export function deleteMemberLevel(id) {
  return request({
    url: `/member/level/${id}`,
    method: 'delete'
  })
}

export function initMemberLevel() {
  return request({
    url: '/member/level/init',
    method: 'post'
  })
}
