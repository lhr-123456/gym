import request from '@/utils/request'

export function getMemberMessageList(params) {
  return request({
    url: '/member/message/list',
    method: 'get',
    params
  })
}

export function getMemberMessagePage(params) {
  return request({
    url: '/member/message/page',
    method: 'get',
    params
  })
}

export function getMemberMessageUnreadCount() {
  return request({
    url: '/member/message/unread-count',
    method: 'get'
  })
}

export function markMessageRead(id) {
  return request({
    url: `/member/message/read/${id}`,
    method: 'put'
  })
}

export function markAllMessagesRead() {
  return request({
    url: '/member/message/read-all',
    method: 'put'
  })
}
