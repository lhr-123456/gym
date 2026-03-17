import request from '@/utils/request'

export function getMemberBodyTestList() {
  return request({
    url: '/member/bodyTest/list',
    method: 'get'
  })
}

export function getMemberBodyTestPage(params) {
  return request({
    url: '/member/bodyTest/page',
    method: 'get',
    params
  })
}

export function getMemberBodyTest(id) {
  return request({
    url: `/member/bodyTest/${id}`,
    method: 'get'
  })
}

export function getMemberBodyTestByMember(memberId) {
  return request({
    url: `/member/bodyTest/member/${memberId}`,
    method: 'get'
  })
}

export function getLatestBodyTest(memberId) {
  return request({
    url: `/member/bodyTest/latest/${memberId}`,
    method: 'get'
  })
}

export function addMemberBodyTest(data) {
  return request({
    url: '/member/bodyTest',
    method: 'post',
    data
  })
}

export function updateMemberBodyTest(data) {
  return request({
    url: '/member/bodyTest',
    method: 'put',
    data
  })
}

export function deleteMemberBodyTest(id) {
  return request({
    url: `/member/bodyTest/${id}`,
    method: 'delete'
  })
}
