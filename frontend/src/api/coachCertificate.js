import request from '@/utils/request'

export function getCoachCertificateList(params) {
  return request({
    url: '/coach-certificate/list',
    method: 'get',
    params
  })
}

export function getCoachCertificatePage(params) {
  return request({
    url: '/coach-certificate/page',
    method: 'get',
    params
  })
}

export function getCoachCertificateById(id) {
  return request({
    url: `/coach-certificate/${id}`,
    method: 'get'
  })
}

export function addCoachCertificate(data) {
  return request({
    url: '/coach-certificate',
    method: 'post',
    data
  })
}

export function updateCoachCertificate(data) {
  return request({
    url: '/coach-certificate',
    method: 'put',
    data
  })
}

export function deleteCoachCertificate(id) {
  return request({
    url: `/coach-certificate/${id}`,
    method: 'delete'
  })
}
