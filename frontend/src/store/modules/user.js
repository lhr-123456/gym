import { login, getInfo } from '@/api/user'
import { getToken, setToken, removeToken, getUserInfo, setUserInfo, removeUserInfo } from '@/utils/auth'

const state = {
  token: getToken(),
  username: '',
  userId: null,
  userType: null,
  role: '',
  permissions: [],
  /** 头像路径 */
  avatar: '',
  /** 会员ID（userType=3 时有效） */
  memberId: null,
  /** 教练ID（userType=2 时有效） */
  coachId: null
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
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  SET_PERMISSIONS: (state, permissions) => {
    state.permissions = permissions
  },
  SET_MEMBER_ID: (state, memberId) => {
    state.memberId = memberId
  },
  SET_COACH_ID: (state, coachId) => {
    state.coachId = coachId
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
        const info = {
          username: data.username,
          userId: data.userId,
          userType: data.userType,
          role: data.role,
          memberId: data.memberId,
          coachId: data.coachId,
          avatar: data.avatar || ''
        }
        setUserInfo(info)
        commit('SET_USERNAME', info.username)
        commit('SET_USER_ID', info.userId)
        commit('SET_USER_TYPE', info.userType)
        commit('SET_ROLE', info.role || '')
        commit('SET_AVATAR', info.avatar)
        commit('SET_MEMBER_ID', info.memberId || null)
        commit('SET_COACH_ID', info.coachId || null)
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
            commit('SET_AVATAR', userInfo.avatar || '')
            commit('SET_MEMBER_ID', userInfo.memberId || null)
            commit('SET_COACH_ID', userInfo.coachId || null)
            resolve(userInfo)
          } else {
            getInfo().then(response => {
              const { data } = response
              commit('SET_USERNAME', data.username)
              commit('SET_USER_ID', data.userId)
              commit('SET_USER_TYPE', data.userType)
              commit('SET_ROLE', data.role)
              commit('SET_AVATAR', data.avatar || '')
              commit('SET_MEMBER_ID', data.memberId || null)
              commit('SET_COACH_ID', data.coachId || null)
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
      commit('SET_AVATAR', '')
      commit('SET_PERMISSIONS', [])
      commit('SET_MEMBER_ID', null)
      commit('SET_COACH_ID', null)
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
      commit('SET_AVATAR', '')
      commit('SET_PERMISSIONS', [])
      commit('SET_MEMBER_ID', null)
      commit('SET_COACH_ID', null)
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
