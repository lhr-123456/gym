import axios from 'axios'
import { Message } from 'element-ui'
import store from '@/store'
import router from '@/router'

function formatDateTime(date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

function convertData(data) {
  if (data && typeof data === 'object') {
    const newData = {}
    for (const key in data) {
      if (data[key] instanceof Date) {
        newData[key] = formatDateTime(data[key])
      } else if (data[key] !== null && typeof data[key] === 'object' && !(data[key] instanceof Array)) {
        newData[key] = convertData(data[key])
      } else {
        newData[key] = data[key]
      }
    }
    return newData
  }
  return data
}

const service = axios.create({
  baseURL: '/api',
  timeout: 15000,
  withCredentials: true
})

service.interceptors.request.use(
  config => {
    if (store.getters.token) {
      config.headers['Authorization'] = 'Bearer ' + store.getters.token
    }
    // 转换请求数据中的日期格式
    if (config.data) {
      config.data = convertData(config.data)
    }
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data

    if (res.code !== 200) {
      Message({
        message: res.message || '请求失败',
        type: 'error',
        duration: 5 * 1000
      })

      if (res.code === 401) {
        store.dispatch('user/resetToken').then(() => {
          location.reload()
        })
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    console.error('Response error:', error)
    
    if (error.response) {
      switch (error.response.status) {
        case 401:
          Message({
            message: '未授权，请重新登录',
            type: 'error',
            duration: 5 * 1000
          })
          store.dispatch('user/resetToken').then(() => {
            location.reload()
          })
          break
        case 403:
          Message({
            message: '拒绝访问',
            type: 'error',
            duration: 5 * 1000
          })
          break
        case 404:
          Message({
            message: '请求地址不存在',
            type: 'error',
            duration: 5 * 1000
          })
          break
        case 500:
          Message({
            message: '服务器内部错误',
            type: 'error',
            duration: 5 * 1000
          })
          break
        default:
          Message({
            message: error.response.data.message || '请求失败',
            type: 'error',
            duration: 5 * 1000
          })
      }
    } else {
      Message({
        message: '网络连接失败，请检查网络',
        type: 'error',
        duration: 5 * 1000
      })
    }

    return Promise.reject(error)
  }
)

export default service
