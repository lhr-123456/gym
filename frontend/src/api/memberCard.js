import request from '@/utils/request'

export function getMemberCardList() {
  return request({
    url: '/member/card/list',
    method: 'get'
  })
}

export function getMemberCardPage(params) {
  return request({
    url: '/member/card/page',
    method: 'get',
    params
  })
}

export function getMemberCard(id) {
  return request({
    url: `/member/card/${id}`,
    method: 'get'
  })
}

export function getMemberCardByMember(memberId) {
  return request({
    url: `/member/card/member/${memberId}`,
    method: 'get'
  })
}

export function addMemberCard(data) {
  return request({
    url: '/member/card',
    method: 'post',
    data
  })
}

export function updateMemberCard(data) {
  return request({
    url: '/member/card',
    method: 'put',
    data
  })
}

export function deleteMemberCard(id) {
  return request({
    url: `/member/card/${id}`,
    method: 'delete'
  })
}

export function renewMemberCard(id, data) {
  return request({
    url: `/member/card/renew/${id}`,
    method: 'post',
    data
  })
}

export function reportLossMemberCard(id) {
  return request({
    url: `/member/card/reportLoss/${id}`,
    method: 'post'
  })
}

export function unreportLossMemberCard(id) {
  return request({
    url: `/member/card/unreportLoss/${id}`,
    method: 'post'
  })
}

export function reissueMemberCard(id) {
  return request({
    url: `/member/card/reissue/${id}`,
    method: 'post'
  })
}

export function useMemberCard(id) {
  return request({
    url: `/member/card/use/${id}`,
    method: 'post'
  })
}
