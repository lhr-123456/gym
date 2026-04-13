<template>
  <div class="music-page">
    <!-- 顶部标题 -->
    <div class="page-header">
      <div class="header-left">
        <div class="music-icon">
          <i class="el-icon-headset"></i>
        </div>
        <div>
          <h2 class="page-title">健身房音乐</h2>
          <p class="page-subtitle">运动BGM · 燃脂每一刻</p>
        </div>
      </div>
      <div class="header-right">
        <span class="total-badge">
          <i class="el-icon-music-note"></i>
          {{ playlist.length }} 首歌曲
        </span>
      </div>
    </div>

    <!-- 主播放区域 -->
    <div class="player-main">
      <!-- 左侧：唱片 + 控制器 -->
      <div class="player-left">
        <!-- 唱片区域 -->
        <div class="disc-area">
          <div class="disc-needle" :class="{ playing: isPlaying }"></div>
          <div class="disc-wrapper" :class="{ playing: isPlaying }">
            <div class="disc">
              <div class="disc-center"></div>
              <img v-if="currentSong" :src="currentSong.cover" class="disc-cover" />
              <div v-else class="disc-cover-placeholder"></div>
            </div>
          </div>
        </div>

        <!-- 进度条 -->
        <div class="progress-section">
          <div class="progress-bar" @click="seek">
            <div class="progress-track">
              <div class="progress-fill" :style="{ width: progress + '%' }">
                <div class="progress-thumb"></div>
              </div>
            </div>
          </div>
          <div class="progress-time">
            <span class="current-time">{{ formatTime(currentTime) }}</span>
            <span class="total-time">{{ formatTime(duration) }}</span>
          </div>
        </div>

        <!-- 控制器 -->
        <div class="control-panel">
          <i
            class="el-icon-refresh control-icon"
            :class="{ active: isRepeat }"
            @click="toggleRepeat"
            title="单曲循环"
          ></i>
          <i class="el-icon-caret-left control-icon big" @click="prev"></i>
          <div class="play-btn-main" @click="togglePlay">
            <i :class="isPlaying ? 'el-icon-video-pause' : 'el-icon-video-play'"></i>
          </div>
          <i class="el-icon-caret-right control-icon big" @click="next"></i>
          <i
            class="el-icon-s-unfold control-icon"
            :class="{ active: isShuffle }"
            @click="toggleShuffle"
            title="随机播放"
          ></i>
        </div>

        <!-- 音量控制 -->
        <div class="volume-section">
          <i
            class="el-icon-turn-off-microphone vol-icon"
            :class="{ muted: isMuted }"
            @click="toggleMute"
          ></i>
          <el-slider
            v-model="volumePct"
            :show-tooltip="false"
            class="vol-slider"
            @input="setVolumeVal"
          ></el-slider>
          <span class="vol-pct">{{ Math.round(volumePct * 100) }}%</span>
        </div>
      </div>

      <!-- 右侧：播放列表 -->
      <div class="player-right">
        <div class="playlist-header">
          <span class="plh-title">
            <i class="el-icon-tickets"></i> 播放列表
          </span>
          <span class="plh-count">共 {{ playlist.length }} 首</span>
        </div>
        <div class="playlist-scroll">
          <div
            v-for="(song, idx) in playlist"
            :key="song.id"
            class="song-item"
            :class="{ active: idx === currentIndex }"
            @click="playByIndex(idx)"
          >
            <div class="song-item-left">
              <span v-if="idx === currentIndex && isPlaying" class="playing-indicator">
                <span></span><span></span><span></span>
              </span>
              <i v-else class="el-icon-video-play song-play-icon"></i>
              <div class="song-cover-wrap">
                <img v-if="song.cover" :src="song.cover" class="song-cover-img" />
                <div v-else class="song-cover-placeholder">
                  <i class="el-icon-headset"></i>
                </div>
              </div>
              <div class="song-info-text">
                <span class="st-name" :class="{ highlight: idx === currentIndex }">{{ song.name }}</span>
                <span class="st-artist">{{ song.artist }}</span>
              </div>
            </div>
            <span class="st-duration">{{ formatTime(song.duration) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 隐藏 audio -->
    <audio
      ref="audioRef"
      :src="currentSong ? currentSong.url : ''"
      :volume="isMuted ? 0 : volume"
      @timeupdate="onTimeUpdate"
      @loadedmetadata="onLoaded"
      @ended="onEnded"
      @error="onAudioError"
    ></audio>
  </div>
</template>

<script>
import { mapState, mapGetters, mapActions } from 'vuex'

export default {
  name: 'MusicView',
  data() {
    return {
      volumePct: 0.8
    }
  },
  computed: {
    ...mapState('music', ['playlist', 'currentIndex', 'isPlaying', 'isMuted',
      'volume', 'currentTime', 'duration', 'isRepeat', 'isShuffle']),
    ...mapGetters('music', ['currentSong', 'progress'])
  },
  watch: {
    isPlaying(val) {
      this.$nextTick(() => {
        const audio = this.$refs.audioRef
        if (!audio) return
        if (val) audio.play().catch(() => {})
        else audio.pause()
      })
    },
    currentSong() {
      this.$nextTick(() => {
        const audio = this.$refs.audioRef
        if (!audio) return
        if (this.isPlaying) audio.play().catch(() => {})
      })
    }
  },
  mounted() {
    this.volumePct = this.volume
    this.$nextTick(() => {
      if (this.isPlaying && this.$refs.audioRef) {
        this.$refs.audioRef.play().catch(() => {})
      }
    })
  },
  methods: {
    ...mapActions('music', [
      'togglePlay', 'playNext', 'playPrev', 'playByIndex',
      'updateTime', 'setVolume', 'toggleMute', 'toggleRepeat', 'toggleShuffle'
    ]),

    setVolumeVal(val) {
      this.volumePct = val
      this.setVolume(val)
    },

    seek(e) {
      const bar = e.currentTarget
      const rect = bar.getBoundingClientRect()
      const ratio = Math.max(0, Math.min(1, (e.clientX - rect.left) / rect.width))
      const targetTime = ratio * this.duration
      const audio = this.$refs.audioRef
      if (audio) {
        audio.currentTime = targetTime
        this.updateTime({ currentTime: targetTime, duration: this.duration })
      }
    },

    prev() { this.$store.dispatch('music/playPrev') },
    next() { this.$store.dispatch('music/playNext') },

    onTimeUpdate() {
      const audio = this.$refs.audioRef
      if (audio) {
        this.updateTime({ currentTime: audio.currentTime, duration: audio.duration || 0 })
      }
    },

    onLoaded() {
      const audio = this.$refs.audioRef
      if (audio) {
        this.updateTime({ currentTime: audio.currentTime, duration: audio.duration })
        audio.volume = this.isMuted ? 0 : this.volume
        if (this.isPlaying) audio.play().catch(() => {})
      }
    },

    onEnded() {
      if (this.isRepeat) {
        const audio = this.$refs.audioRef
        if (audio) { audio.currentTime = 0; audio.play().catch(() => {}) }
      } else {
        this.$store.dispatch('music/playNext')
      }
    },

    onAudioError() {
      this.$store.dispatch('music/playNext')
    },

    formatTime(seconds) {
      if (!seconds || isNaN(seconds)) return '00:00'
      const s = Math.floor(seconds)
      const m = Math.floor(s / 60)
      const ss = s % 60
      return `${String(m).padStart(2, '0')}:${String(ss).padStart(2, '0')}`
    }
  }
}
</script>

<style lang="scss" scoped>
.music-page {
  min-height: calc(100vh - 60px - 40px);
  background: rgba(8, 10, 22, 0.82);
  border-radius: 12px;
  overflow: hidden;
  backdrop-filter: blur(16px);
  display: flex;
  flex-direction: column;
  border: 1px solid rgba(255, 255, 255, 0.06);
}

/* 顶部标题 */
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 28px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  background: rgba(255, 255, 255, 0.02);

  .header-left {
    display: flex;
    align-items: center;
    gap: 14px;

    .music-icon {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      background: linear-gradient(135deg, #ec4141 0%, #d93b3b 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      i {
        font-size: 24px;
        color: #fff;
      }
    }

    .page-title {
      margin: 0 0 2px;
      font-size: 18px;
      font-weight: 700;
      color: rgba(255, 255, 255, 0.9);
    }

    .page-subtitle {
      margin: 0;
      font-size: 12px;
      color: rgba(255, 255, 255, 0.35);
    }
  }

  .header-right {
    .total-badge {
      display: inline-flex;
      align-items: center;
      gap: 5px;
      padding: 6px 14px;
      border-radius: 20px;
      background: rgba(236, 65, 65, 0.12);
      color: rgba(236, 65, 65, 0.8);
      font-size: 12px;
      i { font-size: 13px; }
    }
  }
}

/* 主播放区域 */
.player-main {
  display: flex;
  flex: 1;
  min-height: 0;
}

/* 左侧：唱片 + 控制 */
.player-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 28px 20px 24px;
  border-right: 1px solid rgba(255, 255, 255, 0.05);
  gap: 0;

  /* 唱片 */
  .disc-area {
    position: relative;
    width: 260px;
    height: 260px;
    margin-bottom: 20px;

    /* 唱臂 */
    .disc-needle {
      position: absolute;
      top: -10px;
      right: 18px;
      width: 14px;
      height: 80px;
      background: linear-gradient(180deg, rgba(200,200,200,0.9) 0%, rgba(150,150,150,0.8) 100%);
      border-radius: 7px 7px 2px 2px;
      transform-origin: top right;
      transform: rotate(-25deg);
      transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
      z-index: 2;
      box-shadow: 2px 2px 8px rgba(0,0,0,0.3);

      &::after {
        content: '';
        position: absolute;
        top: -2px;
        left: 50%;
        transform: translateX(-50%);
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background: rgba(180,180,180,0.9);
      }

      &.playing {
        transform: rotate(10deg);
      }
    }

    .disc-wrapper {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;

      &.playing .disc {
        animation: rotate-disc 20s linear infinite;
      }
    }

    .disc {
      width: 240px;
      height: 240px;
      border-radius: 50%;
      background: radial-gradient(circle at center, #1a1a1a 0%, #0a0a0a 60%, #1a1a1a 100%);
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6), inset 0 0 60px rgba(255,255,255,0.03);
      position: relative;
      overflow: hidden;

      .disc-center {
        position: absolute;
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: radial-gradient(circle at 30% 30%, #555, #222);
        border: 2px solid #333;
        z-index: 2;
        box-shadow: 0 2px 8px rgba(0,0,0,0.5);
      }

      .disc-cover {
        position: absolute;
        inset: 0;
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 50%;
        z-index: 1;
      }

      .disc-cover-placeholder {
        width: 240px;
        height: 240px;
        border-radius: 50%;
        background: rgba(255,255,255,0.04);
        display: flex;
        align-items: center;
        justify-content: center;
        i {
          font-size: 60px;
          color: rgba(255,255,255,0.1);
        }
      }
    }
  }

  /* 进度条 */
  .progress-section {
    width: 100%;
    max-width: 380px;
    margin-bottom: 16px;

    .progress-bar {
      width: 100%;
      height: 20px;
      display: flex;
      align-items: center;
      cursor: pointer;

      .progress-track {
        width: 100%;
        height: 4px;
        background: rgba(255, 255, 255, 0.1);
        border-radius: 2px;
        overflow: visible;
        position: relative;

        &:hover { height: 6px; }
      }

      .progress-fill {
        height: 100%;
        background: linear-gradient(90deg, #ec4141 0%, #ff6b6b 100%);
        border-radius: 2px;
        transition: width 0.3s linear;
        position: relative;

        .progress-thumb {
          position: absolute;
          right: -7px;
          top: 50%;
          transform: translateY(-50%) scale(0);
          width: 12px;
          height: 12px;
          border-radius: 50%;
          background: #ec4141;
          transition: transform 0.2s;
          box-shadow: 0 2px 6px rgba(236, 65, 65, 0.5);
        }
      }

      &:hover .progress-thumb {
        transform: translateY(-50%) scale(1);
      }
    }

    .progress-time {
      display: flex;
      justify-content: space-between;
      margin-top: 4px;

      span {
        font-size: 11px;
        color: rgba(255, 255, 255, 0.35);
        font-variant-numeric: tabular-nums;
      }
    }
  }

  /* 控制器 */
  .control-panel {
    display: flex;
    align-items: center;
    gap: 20px;
    margin-bottom: 18px;

    .control-icon {
      color: rgba(255, 255, 255, 0.5);
      font-size: 18px;
      cursor: pointer;
      transition: color 0.2s, transform 0.2s;
      &:hover { color: rgba(255, 255, 255, 0.85); transform: scale(1.1); }
      &.active { color: #ec4141; }
      &.big { font-size: 28px; }
    }

    .play-btn-main {
      width: 58px;
      height: 58px;
      border-radius: 50%;
      background: linear-gradient(135deg, #ec4141 0%, #d93b3b 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      box-shadow: 0 6px 24px rgba(236, 65, 65, 0.5);
      transition: all 0.2s;
      margin: 0 4px;

      i {
        font-size: 28px;
        color: #fff;
        margin-left: 2px;
      }

      &:hover {
        transform: scale(1.08);
        box-shadow: 0 8px 32px rgba(236, 65, 65, 0.65);
      }
    }
  }

  /* 音量 */
  .volume-section {
    display: flex;
    align-items: center;
    gap: 10px;
    width: 100%;
    max-width: 300px;

    .vol-icon {
      font-size: 17px;
      color: rgba(255, 255, 255, 0.5);
      cursor: pointer;
      flex-shrink: 0;
      transition: color 0.2s;
      &:hover { color: rgba(255, 255, 255, 0.85); }
      &.muted { color: rgba(255, 255, 255, 0.2); }
    }

    .vol-slider {
      flex: 1;
      ::v-deep .el-slider__runway {
        background: rgba(255, 255, 255, 0.12);
        height: 3px;
      }
      ::v-deep .el-slider__bar {
        background: #ec4141;
        height: 3px;
      }
      ::v-deep .el-slider__button {
        width: 10px;
        height: 10px;
        background: #ec4141;
        border: none;
      }
    }

    .vol-pct {
      font-size: 11px;
      color: rgba(255, 255, 255, 0.35);
      flex-shrink: 0;
      min-width: 32px;
      text-align: right;
    }
  }
}

/* 右侧：播放列表 */
.player-right {
  width: 340px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;

  .playlist-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 20px 12px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.05);

    .plh-title {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 14px;
      font-weight: 600;
      color: rgba(255, 255, 255, 0.75);
      i { color: #ec4141; }
    }

    .plh-count {
      font-size: 11px;
      color: rgba(255, 255, 255, 0.3);
    }
  }

  .playlist-scroll {
    flex: 1;
    overflow-y: auto;
    padding: 6px 8px;

    &::-webkit-scrollbar { width: 3px; }
    &::-webkit-scrollbar-track { background: transparent; }
    &::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 2px; }
  }

  .song-item {
    display: flex;
    align-items: center;
    padding: 8px 12px;
    border-radius: 8px;
    cursor: pointer;
    transition: background 0.15s;
    gap: 8px;

    &:hover {
      background: rgba(255, 255, 255, 0.05);
    }

    &.active {
      background: rgba(236, 65, 65, 0.1);
    }

    .song-item-left {
      flex: 1;
      display: flex;
      align-items: center;
      gap: 10px;
      min-width: 0;

      .playing-indicator {
        display: inline-flex;
        align-items: flex-end;
        gap: 1px;
        height: 12px;
        flex-shrink: 0;

        span {
          width: 2px;
          background: #ec4141;
          border-radius: 1px;
          animation: sound-bar 0.5s ease-in-out infinite alternate;

          &:nth-child(1) { height: 5px; animation-delay: 0s; }
          &:nth-child(2) { height: 10px; animation-delay: 0.15s; }
          &:nth-child(3) { height: 7px; animation-delay: 0.3s; }
        }
      }

      .song-play-icon {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.25);
        flex-shrink: 0;
        transition: color 0.2s;
      }

      &:hover .song-play-icon { color: rgba(255, 255, 255, 0.6); }
      &.active .song-play-icon { color: #ec4141; }

      .song-cover-wrap {
        width: 38px;
        height: 38px;
        border-radius: 6px;
        overflow: hidden;
        flex-shrink: 0;
        background: rgba(255,255,255,0.05);

        .song-cover-img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .song-cover-placeholder {
          width: 100%;
          height: 100%;
          display: flex;
          align-items: center;
          justify-content: center;
          i {
            font-size: 14px;
            color: rgba(255,255,255,0.15);
          }
        }
      }

      .song-info-text {
        flex: 1;
        min-width: 0;

        .st-name {
          display: block;
          font-size: 13px;
          color: rgba(255, 255, 255, 0.75);
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          margin-bottom: 2px;
          &.highlight { color: #ec4141; }
        }

        .st-artist {
          display: block;
          font-size: 11px;
          color: rgba(255, 255, 255, 0.3);
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
    }

    .st-duration {
      font-size: 11px;
      color: rgba(255, 255, 255, 0.25);
      flex-shrink: 0;
      font-variant-numeric: tabular-nums;
    }
  }
}

@keyframes rotate-disc {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes sound-bar {
  from { transform: scaleY(0.4); }
  to { transform: scaleY(1); }
}
</style>
