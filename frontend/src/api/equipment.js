import request from '@/utils/request'

export function getEquipmentList(params) {
  return request({
    url: '/equipment/list',
    method: 'get',
    params
  })
}

export function getEquipmentPage(params) {
  return request({
    url: '/equipment/page',
    method: 'get',
    params
  })
}

export function getEquipmentById(id) {
  return request({
    url: `/equipment/${id}`,
    method: 'get'
  })
}

export function addEquipment(data) {
  return request({
    url: '/equipment',
    method: 'post',
    data
  })
}

export function updateEquipment(data) {
  return request({
    url: '/equipment',
    method: 'put',
    data
  })
}

export function deleteEquipment(id) {
  return request({
    url: `/equipment/${id}`,
    method: 'delete'
  })
}

export function uploadEquipmentImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/equipment/upload',
    method: 'post',
    data: formData
    // 不手动设置 Content-Type，由浏览器带上 multipart 边界；手动写 multipart/form-data 会缺 boundary
  })
}

