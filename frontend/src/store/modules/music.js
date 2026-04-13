// 健身房背景音乐播放器 — Vuex store
// 歌曲数据来自免费公开资源，音乐链接需自行替换为有效 URL
// 推荐：可将 mp3 文件放入 public/music/ 目录，使用 /music/xxx.mp3 引用

const defaultPlaylist = [
  {
    id: 1,
    name: 'Gym Workout Mix',
    artist: 'Fitness Beats',
    duration: 214,
    cover: 'https://p2.music.126.net/5c1eO0PF4AlL89L3Mhc0PQ==/109951163963218691.jpg',
    url: 'https://music.163.com/song/media/outer/url?id=1857920861.mp3'
  },
  {
    id: 2,
    name: 'Power Training',
    artist: 'Energy Fitness',
    duration: 198,
    cover: 'https://p2.music.126.net/Ve9LnMIaJSVQFPBvKPIvYw==/109951163962966347.jpg',
    url: 'https://music.163.com/song/media/outer/url?id=1894942089.mp3'
  },
  {
    id: 3,
    name: 'Cardio Rush',
    artist: 'Workout Academy',
    duration: 236,
    cover: 'https://p2.music.126.net/H4Mm0FxEYSZC8MBHGXtXag==/109951163962972097.jpg',
    url: 'https://music.163.com/song/media/outer/url?id=1858882019.mp3'
  },
  {
    id: 4,
    name: 'Muscle Pump',
    artist: 'Iron Gym',
    duration: 182,
    cover: 'https://p2.music.126.net/5l2VVN-KsPY7vRNkTFJKQA==/109951163963218694.jpg',
    url: 'https://music.163.com/song/media/outer/url?id=1857920870.mp3'
  },
  {
    id: 5,
    name: 'Endurance Zone',
    artist: 'FitLife Music',
    duration: 265,
    cover: 'https://p2.music.126.net/6gAJPZOHmRrqYKKKKKK==/109951163963218697.jpg',
    url: 'https://music.163.com/song/media/outer/url?id=1857920875.mp3'
  }
]

const state = {
  playlist: defaultPlaylist,
  currentIndex: 0,
  isPlaying: false,
  isMuted: false,
  volume: 0.8,
  currentTime: 0,
  duration: 0,
  isRepeat: false,
  isShuffle: false,
  // 迷你播放器展开状态
  isExpanded: false
}

const getters = {
  currentSong: state => state.playlist[state.currentIndex] || null,
  progress: state => state.duration > 0 ? (state.currentTime / state.duration) * 100 : 0
}

const mutations = {
  SET_PLAYLIST(state, list) {
    state.playlist = list
  },
  SET_CURRENT_INDEX(state, index) {
    state.currentIndex = index
  },
  SET_IS_PLAYING(state, val) {
    state.isPlaying = val
  },
  SET_MUTED(state, val) {
    state.isMuted = val
  },
  SET_VOLUME(state, val) {
    state.volume = val
  },
  SET_CURRENT_TIME(state, val) {
    state.currentTime = val
  },
  SET_DURATION(state, val) {
    state.duration = val
  },
  SET_REPEAT(state) {
    state.isRepeat = !state.isRepeat
  },
  SET_SHUFFLE(state) {
    state.isShuffle = !state.isShuffle
  },
  SET_EXPANDED(state, val) {
    state.isExpanded = val
  }
}

const actions = {
  play({ commit, state }) {
    commit('SET_IS_PLAYING', true)
  },
  pause({ commit }) {
    commit('SET_IS_PLAYING', false)
  },
  togglePlay({ commit, state }) {
    commit('SET_IS_PLAYING', !state.isPlaying)
  },
  playNext({ commit, state }) {
    if (state.isShuffle) {
      const nextIndex = Math.floor(Math.random() * state.playlist.length)
      commit('SET_CURRENT_INDEX', nextIndex)
    } else {
      const next = (state.currentIndex + 1) % state.playlist.length
      commit('SET_CURRENT_INDEX', next)
    }
    commit('SET_IS_PLAYING', true)
  },
  playPrev({ commit, state }) {
    const prev = state.currentIndex === 0 ? state.playlist.length - 1 : state.currentIndex - 1
    commit('SET_CURRENT_INDEX', prev)
    commit('SET_IS_PLAYING', true)
  },
  playByIndex({ commit }, index) {
    commit('SET_CURRENT_INDEX', index)
    commit('SET_IS_PLAYING', true)
  },
  updateTime({ commit }, { currentTime, duration }) {
    commit('SET_CURRENT_TIME', currentTime)
    commit('SET_DURATION', duration)
  },
  seek({ commit }, progress) {
    commit('SET_CURRENT_TIME', progress)
  },
  setVolume({ commit }, volume) {
    commit('SET_VOLUME', volume)
    if (volume > 0) commit('SET_MUTED', false)
  },
  toggleMute({ commit, state }) {
    commit('SET_MUTED', !state.isMuted)
  },
  toggleRepeat({ commit }) {
    commit('SET_REPEAT')
  },
  toggleShuffle({ commit }) {
    commit('SET_SHUFFLE')
  },
  toggleExpanded({ commit, state }) {
    commit('SET_EXPANDED', !state.isExpanded)
  },
  setExpanded({ commit }, val) {
    commit('SET_EXPANDED', val)
  }
}

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions
}
