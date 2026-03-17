import request from '@/utils/request'

export function getCoachSalaryList(params) {
  return request({
    url: '/coach-salary/list',
    method: 'get',
    params
  })
}

export function getCoachSalaryPage(params) {
  return request({
    url: '/coach-salary/page',
    method: 'get',
    params
  })
}

export function getCoachSalaryById(id) {
  return request({
    url: `/coach-salary/${id}`,
    method: 'get'
  })
}

export function addCoachSalary(data) {
  return request({
    url: '/coach-salary',
    method: 'post',
    data
  })
}

export function updateCoachSalary(data) {
  return request({
    url: '/coach-salary',
    method: 'put',
    data
  })
}

export function deleteCoachSalary(id) {
  return request({
    url: `/coach-salary/${id}`,
    method: 'delete'
  })
}
