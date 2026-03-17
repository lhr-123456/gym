import request from '@/utils/request'

export function getCoachReviewList(params) {
  return request({
    url: '/coach-review/list',
    method: 'get',
    params
  })
}

export function getCoachReviewPage(params) {
  return request({
    url: '/coach-review/page',
    method: 'get',
    params
  })
}

export function getCoachReviewById(id) {
  return request({
    url: `/coach-review/${id}`,
    method: 'get'
  })
}

export function addCoachReview(data) {
  return request({
    url: '/coach-review',
    method: 'post',
    data
  })
}

export function updateCoachReview(data) {
  return request({
    url: '/coach-review',
    method: 'put',
    data
  })
}

export function deleteCoachReview(id) {
  return request({
    url: `/coach-review/${id}`,
    method: 'delete'
  })
}
