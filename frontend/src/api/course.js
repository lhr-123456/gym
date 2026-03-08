import request from '@/utils/request'

export function getCourseList(params) {
  return request({
    url: '/course/list',
    method: 'get',
    params
  })
}

export function getCoursePage(params) {
  return request({
    url: '/course/page',
    method: 'get',
    params
  })
}

export function getCourseById(id) {
  return request({
    url: `/course/${id}`,
    method: 'get'
  })
}

export function addCourse(data) {
  return request({
    url: '/course',
    method: 'post',
    data
  })
}

export function updateCourse(data) {
  return request({
    url: '/course',
    method: 'put',
    data
  })
}

export function deleteCourse(id) {
  return request({
    url: `/course/${id}`,
    method: 'delete'
  })
}

export function bookCourse(courseId, params) {
  return request({
    url: `/course/book/${courseId}`,
    method: 'post',
    params
  })
}

export function cancelBooking(bookingId) {
  return request({
    url: `/course/cancel/${bookingId}`,
    method: 'post'
  })
}

export function getAvailableCourses(params) {
  return request({
    url: '/course/available',
    method: 'get',
    params
  })
}

export function getCourseBookings(courseId) {
  return request({
    url: `/course/bookings/${courseId}`,
    method: 'get'
  })
}