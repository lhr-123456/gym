import request from '@/utils/request'

export function getMemberConsumptionList() {
  return request({
    url: '/member/consumption/list',
    method: 'get'
  })
}

export function getMemberConsumptionPage(params) {
  return request({
    url: '/member/consumption/page',
    method: 'get',
    params
  })
}

export function getMemberConsumption(id) {
  return request({
    url: `/member/consumption/${id}`,
    method: 'get'
  })
}

export function getMemberConsumptionByMember(memberId) {
  return request({
    url: `/member/consumption/member/${memberId}`,
    method: 'get'
  })
}

export function addMemberConsumption(data) {
  return request({
    url: '/member/consumption',
    method: 'post',
    data
  })
}

export function updateMemberConsumption(data) {
  return request({
    url: '/member/consumption',
    method: 'put',
    data
  })
}

export function deleteMemberConsumption(id) {
  return request({
    url: `/member/consumption/${id}`,
    method: 'delete'
  })
}

export function getConsumptionStatistics(memberId) {
  return request({
    url: `/member/consumption/statistics/${memberId}`,
    method: 'get'
  })
}

export function getMemberPurchases(memberId) {
  return request({
    url: `/member/consumption/purchases/${memberId}`,
    method: 'get'
  })
}
