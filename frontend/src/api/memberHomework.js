import request from '@/utils/request'

export function getHomeworkList(memberId) {
  return request({
    url: '/member/homework/list',
    method: 'get',
    params: { memberId }
  })
}

export function getPendingHomework(memberId) {
  return request({
    url: '/member/homework/pending',
    method: 'get',
    params: { memberId }
  })
}

export function completeHomework(id, memberId) {
  return request({
    url: `/member/homework/complete/${id}`,
    method: 'put',
    params: { memberId }
  })
}
