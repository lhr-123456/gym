import request from '@/utils/request'

// 获取商品列表
export function getGoodsList(params) {
  return request({
    url: '/points/goods/list',
    method: 'get',
    params
  })
}

// 获取商品分页
export function getGoodsPage(params) {
  return request({
    url: '/points/goods/page',
    method: 'get',
    params
  })
}

// 获取单个商品
export function getGoodsById(id) {
  return request({
    url: `/points/goods/${id}`,
    method: 'get'
  })
}

// 添加/编辑商品
export function saveGoods(data) {
  return request({
    url: '/points/goods',
    method: 'post',
    data
  })
}

// 更新商品
export function updateGoods(data) {
  return request({
    url: '/points/goods',
    method: 'put',
    data
  })
}

// 删除商品
export function deleteGoods(id) {
  return request({
    url: `/points/goods/${id}`,
    method: 'delete'
  })
}

// 兑换商品
export function exchangeGoods(data) {
  return request({
    url: '/points/exchange/goods',
    method: 'post',
    data
  })
}

// 兑换课程
export function exchangeCourse(data) {
  return request({
    url: '/points/exchange/course',
    method: 'post',
    data
  })
}

// 获取会员兑换记录
export function getExchangeList(memberId) {
  return request({
    url: '/points/exchange/list',
    method: 'get',
    params: { memberId }
  })
}

// 获取会员兑换记录分页
export function getExchangePage(params) {
  return request({
    url: '/points/exchange/page',
    method: 'get',
    params
  })
}

// 取消兑换
export function cancelExchange(id, memberId) {
  return request({
    url: `/points/exchange/cancel/${id}`,
    method: 'put',
    params: { memberId }
  })
}

// 获取会员当前积分
export function getMemberPoints(memberId) {
  return request({
    url: `/member/points/${memberId}`,
    method: 'get'
  })
}

// 后台分页查询所有兑换记录
export function getAdminExchangePage(params) {
  return request({
    url: '/points/exchange/admin/page',
    method: 'get',
    params
  })
}

// 后台查询某商品兑换记录
export function getExchangeListByGoods(goodsId) {
  return request({
    url: '/points/exchange/list/by-goods',
    method: 'get',
    params: { goodsId }
  })
}

// 获取会员积分明细（分页）
export function getPointsRecordPage(params) {
  return request({
    url: '/member/points/record/page',
    method: 'get',
    params
  })
}

// 获取会员积分明细列表
export function getPointsRecordList(memberId) {
  return request({
    url: '/member/points/record/list',
    method: 'get',
    params: { memberId }
  })
}

// 获取积分任务列表及完成状态
export function getPointsTaskList(memberId) {
  return request({
    url: '/member/points/task/list',
    method: 'get',
    params: { memberId }
  })
}

// 手动添加积分记录（管理员用）
export function addPointsRecord(data) {
  return request({
    url: '/member/points/record/add',
    method: 'post',
    params: data
  })
}
