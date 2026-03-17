import request from '@/utils/request'

export function getEquipmentBookingList(params) {
  return request({
    url: '/equipment-booking/list',
    method: 'get',
    params
  })
}

export function getEquipmentBookingPage(params) {
  return request({
    url: '/equipment-booking/page',
    method: 'get',
    params
  })
}

export function getEquipmentBookingById(id) {
  return request({
    url: `/equipment-booking/${id}`,
    method: 'get'
  })
}

export function addEquipmentBooking(data) {
  return request({
    url: '/equipment-booking',
    method: 'post',
    data
  })
}

export function updateEquipmentBooking(data) {
  return request({
    url: '/equipment-booking',
    method: 'put',
    data
  })
}

export function deleteEquipmentBooking(id) {
  return request({
    url: `/equipment-booking/${id}`,
    method: 'delete'
  })
}
