import { login, getInfo } from '@/api/user'
import { getToken, setToken, removeToken, getUserInfo, setUserInfo, removeUserInfo } from '@/utils/auth'

const state = {
  token: getToken(),
  username: '',
  userId: null,
  userType: null,
  role: '',
  permissions: []
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_USERNAME: (state, username) => {
    state.username = username
  },
  SET_USER_ID: (state, userId) => {
    state.userId = userId
  },
  SET_USER_TYPE: (state, userType) => {
    state.userType = userType
  },
  SET_ROLE: (state, role) => {
    state.role = role
  },
  SET_PERMISSIONS: (state, permissions) => {
    state.permissions = permissions
  }
}

const actions = {
  login({ commit }, userInfo) {
    const { username, password, userType } = userInfo
    return new Promise((resolve, reject) => {
      login({ username, password, userType }).then(response => {
        const { data } = response
        commit('SET_TOKEN', data.token)
        setToken(data.token)
        setUserInfo({
          username: data.username,
          userId: data.userId,
          userType: data.userType,
          role: data.role
        })
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      const userInfo = getUserInfo()
      if (userInfo) {
        commit('SET_USERNAME', userInfo.username)
        commit('SET_USER_ID', userInfo.userId)
        commit('SET_USER_TYPE', userInfo.userType)
        commit('SET_ROLE', userInfo.role)
        resolve(userInfo)
      } else {
        getInfo().then(response => {
          const { data } = response
          commit('SET_USERNAME', data.username)
          commit('SET_USER_ID', data.userId)
          commit('SET_USER_TYPE', data.userType)
          commit('SET_ROLE', data.role)
          resolve(data)
        }).catch(error => {
          reject(error)
        })
      }
    })
  },

  resetToken({ commit }) {
    return new Promise(resolve => {
      removeToken()
      removeUserInfo()
      commit('SET_TOKEN', '')
      commit('SET_USERNAME', '')
      commit('SET_USER_ID', null)
      commit('SET_USER_TYPE', null)
      commit('SET_ROLE', '')
      commit('SET_PERMISSIONS', [])
      resolve()
    })
  },

  logout({ commit }) {
    return new Promise(resolve => {
      removeToken()
      removeUserInfo()
      commit('SET_TOKEN', '')
      commit('SET_USERNAME', '')
      commit('SET_USER_ID', null)
      commit('SET_USER_TYPE', null)
      commit('SET_ROLE', '')
      commit('SET_PERMISSIONS', [])
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
