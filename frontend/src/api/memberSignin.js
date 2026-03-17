import request from '@/utils/request'

export function getMemberSigninList() {
  return request({
    url: '/member/signin/list',
    method: 'get'
  })
}

export function getMemberSigninPage(params) {
  return request({
    url: '/member/signin/page',
    method: 'get',
    params
  })
}

export function memberSignin(data) {
  return request({
    url: '/member/signin/sign',
    method: 'post',
    data
  })
}

export function checkTodaySignin(memberId) {
  return request({
    url: `/member/signin/today/${memberId}`,
    method: 'get'
  })
}

export function getSigninStatistics(memberId) {
  return request({
    url: `/member/signin/statistics/${memberId}`,
    method: 'get'
  })
}

export function deleteMemberSignin(id) {
  return request({
    url: `/member/signin/${id}`,
    method: 'delete'
  })
}
