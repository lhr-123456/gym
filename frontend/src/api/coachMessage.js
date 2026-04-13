import request from '@/utils/request'

// 发送消息给教练（会员端）
export function sendMessageToCoach(data) {
  return request({
    url: '/coach/message/send',
    method: 'post',
    data
  })
}

// 获取教练消息分页（教练端）
export function getCoachMessagePage(params) {
  return request({
    url: '/coach/message/page',
    method: 'get',
    params
  })
}

// 获取教练消息列表（教练端）
export function getCoachMessageList() {
  return request({
    url: '/coach/message/list',
    method: 'get'
  })
}

// 获取教练未读消息数（教练端）
export function getCoachMessageUnreadCount() {
  return request({
    url: '/coach/message/unread-count',
    method: 'get'
  })
}

// 标记单条消息已读（教练端）
export function markCoachMessageRead(id) {
  return request({
    url: `/coach/message/read/${id}`,
    method: 'put'
  })
}

// 全部标记已读（教练端）
export function markAllCoachMessagesRead() {
  return request({
    url: '/coach/message/read-all',
    method: 'put'
  })
}
