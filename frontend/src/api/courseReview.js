import request from '@/utils/request'

export function getReviewList(params) {
  return request({
    url: '/course/review/list',
    method: 'get',
    params
  })
}

export function getReviewPage(params) {
  return request({
    url: '/course/review/page',
    method: 'get',
    params
  })
}

export function getReviewById(id) {
  return request({
    url: `/course/review/${id}`,
    method: 'get'
  })
}

export function getCourseReviews(courseId) {
  return request({
    url: `/course/review/course/${courseId}`,
    method: 'get'
  })
}

export function addReview(data) {
  return request({
    url: '/course/review',
    method: 'post',
    data
  })
}

export function updateReview(data) {
  return request({
    url: '/course/review',
    method: 'put',
    data
  })
}

export function deleteReview(id) {
  return request({
    url: `/course/review/${id}`,
    method: 'delete'
  })
}

export function replyReview(id, reply) {
  return request({
    url: `/course/review/reply/${id}`,
    method: 'post',
    params: { reply }
  })
}
