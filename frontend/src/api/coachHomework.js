import request from '@/utils/request'

export function getHomeworkList(params) {
  return request({
    url: '/coach/homework/list',
    method: 'get',
    params
  })
}

export function getHomeworkPage(params) {
  return request({
    url: '/coach/homework/page',
    method: 'get',
    params
  })
}

export function saveHomework(data) {
  return request({
    url: '/coach/homework',
    method: 'post',
    data
  })
}

export function updateHomework(data) {
  return request({
    url: '/coach/homework',
    method: 'put',
    data
  })
}

export function deleteHomework(id) {
  return request({
    url: `/coach/homework/${id}`,
    method: 'delete'
  })
}
