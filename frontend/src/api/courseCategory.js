import request from '@/utils/request'

export function getCategoryList(params) {
  return request({
    url: '/course/category/list',
    method: 'get',
    params
  })
}

export function getCategoryPage(params) {
  return request({
    url: '/course/category/page',
    method: 'get',
    params
  })
}

export function getAllCategories() {
  return request({
    url: '/course/category/all',
    method: 'get'
  })
}

export function getCategoryById(id) {
  return request({
    url: `/course/category/${id}`,
    method: 'get'
  })
}

export function addCategory(data) {
  return request({
    url: '/course/category',
    method: 'post',
    data
  })
}

export function updateCategory(data) {
  return request({
    url: '/course/category',
    method: 'put',
    data
  })
}

export function deleteCategory(id) {
  return request({
    url: `/course/category/${id}`,
    method: 'delete'
  })
}
