import request from '@/utils/request'

export function getCourseBookingList(params) {
  return request({
    url: '/course-booking/list',
    method: 'get',
    params
  })
}

export function getCourseBookingPage(params) {
  return request({
    url: '/course-booking/page',
    method: 'get',
    params
  })
}

export function getCourseBookingById(id) {
  return request({
    url: `/course-booking/${id}`,
    method: 'get'
  })
}

export function addCourseBooking(data) {
  return request({
    url: '/course-booking',
    method: 'post',
    data
  })
}

export function updateCourseBooking(data) {
  return request({
    url: '/course-booking',
    method: 'put',
    data
  })
}

export function deleteCourseBooking(id) {
  return request({
    url: `/course-booking/${id}`,
    method: 'delete'
  })
}

export function approveBooking(id) {
  return request({
    url: `/course-booking/approve/${id}`,
    method: 'put'
  })
}

export function rejectBooking(id) {
  return request({
    url: `/course-booking/reject/${id}`,
    method: 'put'
  })
}

export function cancelBooking(id) {
  return request({
    url: `/course-booking/cancel/${id}`,
    method: 'put'
  })
}

export function signInBooking(id) {
  return request({
    url: `/course-booking/signin/${id}`,
    method: 'put'
  })
}
