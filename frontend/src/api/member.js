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

export function adjustPoints(id, data) {
  return request({
    url: `/member/points/${id}`,
    method: 'put',
    data
  })
}

export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/member/avatar',
    method: 'post',
    data: formData
  })
}

export function assignCoach(data) {
  return request({
    url: '/member/assign-coach',
    method: 'put',
    data
  })
}
