import request from '@/utils/request'

export function getMemberList(params) {
  return request({
    url: '/member/list',
    method: 'get',
    params
  })
}

export function getMemberPage(params) {
  return request({
    url: '/member/page',
    method: 'get',
    params
  })
}

export function getMemberById(id) {
  return request({
    url: `/member/${id}`,
    method: 'get'
  })
}

export function addMember(data) {
  return request({
    url: '/member',
    method: 'post',
    data
  })
}

export function updateMember(data) {
  return request({
    url: '/member',
    method: 'put',
    data
  })
}

export function deleteMember(id) {
  return request({
    url: `/member/${id}`,
    method: 'delete'
  })
}
