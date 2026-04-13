<template>
  <!-- 迷你播放器 + 展开面板 -->
  <div class="music-player">
    <!-- ====== 迷你播放器底部栏 ====== -->
    <div class="mini-player" @click="toggleExpand">
      <!-- 封面 -->
      <div class="cover-wrap">
        <img
          v-if="currentSong && currentSong.cover"
          :src="currentSong.cover"
          class="cover-img"
          :class="{ spinning: isPlaying }"
        />
        <div v-else class="cover-placeholder">
          <i class="el-icon-headset"></i>
        </div>
      </div>

      <!-- 歌曲信息 -->
      <div class="song-info" v-if="currentSong">
        <div class="song-name">{{ currentSong.name }}</div>
        <div class="song-artist">{{ currentSong.artist }}</div>
      </div>
      <div class="song-info" v-else>
        <div class="song-name">暂无歌曲</div>
      </div>

      <!-- 控制按钮 -->
      <div class="controls" @click.stop>
        <i class="el-icon-arrow-up control-btn expand-btn" @click.stop="toggleExpand"></i>
        <i class="el-icon-caret-left control-btn" @click="prev"></i>
        <div class="play-btn" @click="togglePlay">
          <i :class="isPlaying ? 'el-icon-video-pause' : 'el-icon-video-play'"></i>
        </div>
        <i class="el-icon-caret-right control-btn" @click="next"></i>
        <i
          class="el-icon-refresh control-btn"
          :class="{ active: isRepeat }"
          @click="toggleRepeat"
          title="单曲循环"
        ></i>
      </div>

      <!-- 进度条（小） -->
      <div class="progress-mini" @click.stop>
        <span class="time-label">{{ formatTime(currentTime) }}</span>
        <div class="progress-bar-mini" @click="seekMini">
          <div class="progress-track-mini">
            <div
              class="progress-fill-mini"
              :style="{ width: progress + '%' }"
            ></div>
          </div>
        </div>
        <span class="time-label">{{ formatTime(duration) }}</span>
      </div>

      <!-- 音量 & 播放列表 -->
      <div class="right-controls" @click.stop>
        <el-popover
          placement="top"
          width="200"
          trigger="click"
          popper-class="volume-popover"
        >
          <div class="volume-panel">
            <i
              class="el-icon-turn-off-microphone vol-icon"
              :class="{ muted: isMuted }"
              @click="toggleMute"
            ></i>
            <el-slider
              v-model="volumePct"
              :show-tooltip="false"
              @input="setVolumeVal"
            ></el-slider>
            <span class="vol-pct">{{ Math.round(volumePct * 100) }}%</span>
          </div>
          <i slot="reference" class="el-icon-set-up vol-btn"></i>
        </el-popover>
        <i class="el-icon-tickets playlist-btn" @click.stop="toggleExpand"></i>
      </div>
    </div>

    <!-- ====== 展开播放面板 ====== -->
    <transition name="slide-up">
      <div class="player-panel" v-show="isExpanded">
        <!-- 关闭按钮 -->
        <div class="panel-header">
          <span class="panel-title">播放列表</span>
          <div class="panel-controls">
            <i
              class="el-icon-refresh control-btn"
              :class="{ active: isRepeat }"
              @click="toggleRepeat"
              title="单曲循环"
            ></i>
            <i
              class="el-icon-s-unfold control-btn"
              :class="{ active: isShuffle }"
              @click="toggleShuffle"
              title="随机播放"
            ></i>
          </div>
          <i class="el-icon-arrow-down close-btn" @click="setExpand(false)"></i>
        </div>

        <!-- 当前播放信息 -->
        <div class="now-playing" v-if="currentSong">
          <div class="np-cover-wrap">
            <img :src="currentSong.cover" class="np-cover" />
            <div class="np-disc" :class="{ spinning: isPlaying }">
              <img :src="currentSong.cover" class="disc-img" />
            </div>
          </div>
          <div class="np-text">
            <div class="np-song-name">{{ currentSong.name }}</div>
            <div class="np-artist">{{ currentSong.artist }}</div>
          </div>
        </div>

        <!-- 大进度条 -->
        <div class="progress-panel" @click="seekPanel">
          <span class="time-label">{{ formatTime(currentTime) }}</span>
          <div class="progress-track-panel">
            <div class="progress-fill-panel" :style="{ width: progress + '%' }">
              <div class="progress-thumb"></div>
            </div>
          </div>
          <span class="time-label">{{ formatTime(duration) }}</span>
        </div>

        <!-- 大控制按钮 -->
        <div class="big-controls">
          <i
            class="el-icon-refresh control-btn"
            :class="{ active: isRepeat }"
            @click="toggleRepeat"
          ></i>
          <i class="el-icon-caret-left big-ctrl" @click="prev"></i>
          <div class="big-play-btn" @click="togglePlay">
            <i :class="isPlaying ? 'el-icon-video-pause' : 'el-icon-video-play'"></i>
          </div>
          <i class="el-icon-caret-right big-ctrl" @click="next"></i>
          <i
            class="el-icon-s-unfold control-btn"
            :class="{ active: isShuffle }"
            @click="toggleShuffle"
          ></i>
        </div>

        <!-- 播放列表 -->
        <div class="playlist-panel">
          <div class="playlist-header">
            <span>共 {{ playlist.length }} 首</span>
          </div>
          <div class="playlist-items">
            <div
              v-for="(song, idx) in playlist"
              :key="song.id"
              class="playlist-item"
              :class="{ playing: idx === currentIndex }"
              @click="playByIndex(idx)"
            >
              <div class="pli-info">
                <span class="pli-name">
                  <span v-if="idx === currentIndex && isPlaying" class="playing-bar">
                    <span></span><span></span><span></span>
                  </span>
                  {{ song.name }}
                </span>
                <span class="pli-artist">{{ song.artist }}</span>
              </div>
              <span class="pli-duration">{{ formatTime(song.duration) }}</span>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- 隐藏的 audio 标签 -->
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
  name: 'MusicPlayer',
  data() {
    return {
      volumePct: 0.8
    }
  },
  computed: {
    ...mapState('music', ['playlist', 'currentIndex', 'isPlaying', 'isMuted',
      'volume', 'currentTime', 'duration', 'isRepeat', 'isShuffle', 'isExpanded']),
    ...mapGetters('music', ['currentSong', 'progress'])
  },
  watch: {
    isPlaying(val) {
      this.$nextTick(() => {
        const audio = this.$refs.audioRef
        if (!audio) return
        if (val) {
          audio.play().catch(() => {})
        } else {
          audio.pause()
        }
      })
    },
    currentSong() {
      this.$nextTick(() => {
        const audio = this.$refs.audioRef
        if (!audio) return
        if (this.isPlaying) {
          audio.play().catch(() => {})
        }
      })
    }
  },
  mounted() {
    this.volumePct = this.volume
    if (this.isPlaying) {
      this.$nextTick(() => {
        this.$refs.audioRef && this.$refs.audioRef.play().catch(() => {})
      })
    }
  },
  methods: {
    ...mapActions('music', [
      'togglePlay', 'playNext', 'playPrev', 'playByIndex',
      'updateTime', 'seek', 'setVolume', 'toggleMute',
      'toggleRepeat', 'toggleShuffle', 'toggleExpanded', 'setExpanded'
    ]),

    toggleExpand() {
      this.$store.dispatch('music/toggleExpanded')
    },

    prev() {
      this.$store.dispatch('music/playPrev')
    },

    next() {
      this.$store.dispatch('music/playNext')
    },

    setVolumeVal(val) {
      this.volumePct = val
      this.$store.dispatch('music/setVolume', val)
    },

    seekMini(e) {
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

    seekPanel(e) {
      const bar = e.currentTarget.querySelector('.progress-track-panel')
      const rect = bar.getBoundingClientRect()
      const ratio = Math.max(0, Math.min(1, (e.clientX - rect.left) / rect.width))
      const targetTime = ratio * this.duration
      const audio = this.$refs.audioRef
      if (audio) {
        audio.currentTime = targetTime
        this.updateTime({ currentTime: targetTime, duration: this.duration })
      }
    },

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
        if (this.isPlaying) {
          audio.play().catch(() => {})
        }
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
.music-player {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 9999;
  user-select: none;
}

/* ====== 迷你播放器 ====== */
.mini-player {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 16px;
  height: 56px;
  background: rgba(10, 14, 30, 0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.3);
  cursor: pointer;

  .cover-wrap {
    flex-shrink: 0;
    width: 38px;
    height: 38px;
    border-radius: 6px;
    overflow: hidden;
    background: rgba(255, 255, 255, 0.08);
    display: flex;
    align-items: center;
    justify-content: center;

    .cover-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s;

      &.spinning {
        animation: spin-cover 8s linear infinite;
      }
    }

    .cover-placeholder {
      color: rgba(255, 255, 255, 0.3);
      font-size: 18px;
    }
  }

  .song-info {
    flex-shrink: 0;
    min-width: 100px;
    max-width: 140px;

    .song-name {
      font-size: 13px;
      color: rgba(255, 255, 255, 0.9);
      font-weight: 600;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .song-artist {
      font-size: 11px;
      color: rgba(255, 255, 255, 0.4);
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }

  .controls {
    display: flex;
    align-items: center;
    gap: 4px;
    flex-shrink: 0;

    .control-btn {
      color: rgba(255, 255, 255, 0.6);
      font-size: 18px;
      cursor: pointer;
      padding: 4px;
      transition: color 0.2s;

      &:hover { color: rgba(255, 255, 255, 0.9); }
      &.active { color: #ec4141; }
    }

    .expand-btn {
      font-size: 16px;
      color: rgba(255, 255, 255, 0.4);
      &:hover { color: rgba(255, 255, 255, 0.7); }
    }

    .play-btn {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.2s;
      margin: 0 4px;

      i {
        font-size: 20px;
        color: rgba(255, 255, 255, 0.85);
        margin-left: 1px;
      }

      &:hover {
        background: rgba(255, 255, 255, 0.18);
        transform: scale(1.05);
      }
    }
  }

  .progress-mini {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 8px;
    min-width: 0;

    .time-label {
      font-size: 11px;
      color: rgba(255, 255, 255, 0.4);
      flex-shrink: 0;
      font-variant-numeric: tabular-nums;
    }

    .progress-bar-mini {
      flex: 1;
      height: 20px;
      display: flex;
      align-items: center;
      cursor: pointer;

      .progress-track-mini {
        width: 100%;
        height: 3px;
        background: rgba(255, 255, 255, 0.12);
        border-radius: 2px;
        overflow: hidden;
        position: relative;

        &:hover { height: 5px; }
      }

      .progress-fill-mini {
        height: 100%;
        background: linear-gradient(90deg, #ec4141 0%, #ff6b6b 100%);
        border-radius: 2px;
        transition: width 0.3s linear;
        position: relative;

        &::after {
          content: '';
          position: absolute;
          right: -1px;
          top: 50%;
          transform: translateY(-50%) scale(0);
          width: 9px;
          height: 9px;
          border-radius: 50%;
          background: #ec4141;
          transition: transform 0.2s;
        }
      }
    }

    &:hover .progress-fill-mini::after {
      transform: translateY(-50%) scale(1);
    }
  }

  .right-controls {
    display: flex;
    align-items: center;
    gap: 2px;
    flex-shrink: 0;

    .vol-btn, .playlist-btn {
      color: rgba(255, 255, 255, 0.5);
      font-size: 16px;
      cursor: pointer;
      padding: 5px;
      transition: color 0.2s;
      &:hover { color: rgba(255, 255, 255, 0.85); }
    }
  }
}

/* ====== 展开面板 ====== */
.player-panel {
  position: absolute;
  bottom: 56px;
  left: 0;
  right: 0;
  height: 500px;
  background: rgba(8, 10, 22, 0.97);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  overflow: hidden;
  display: flex;
  flex-direction: column;

  .panel-header {
    display: flex;
    align-items: center;
    padding: 12px 20px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.05);

    .panel-title {
      font-size: 13px;
      color: rgba(255, 255, 255, 0.5);
      font-weight: 600;
      flex: 1;
    }

    .panel-controls {
      display: flex;
      gap: 8px;
      margin-right: 12px;
    }

    .control-btn {
      color: rgba(255, 255, 255, 0.4);
      font-size: 15px;
      cursor: pointer;
      padding: 3px;
      transition: color 0.2s;
      &:hover { color: rgba(255, 255, 255, 0.8); }
      &.active { color: #ec4141; }
    }

    .close-btn {
      color: rgba(255, 255, 255, 0.4);
      font-size: 16px;
      cursor: pointer;
      transition: color 0.2s;
      &:hover { color: rgba(255, 255, 255, 0.8); }
    }
  }

  /* 当前播放区 */
  .now-playing {
    display: flex;
    align-items: center;
    padding: 24px 20px 16px;
    gap: 28px;

    .np-cover-wrap {
      position: relative;
      width: 140px;
      height: 140px;
      flex-shrink: 0;

      .np-cover {
        width: 140px;
        height: 140px;
        border-radius: 12px;
        object-fit: cover;
        position: relative;
        z-index: 1;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
      }

      .np-disc {
        position: absolute;
        right: -16px;
        top: 50%;
        transform: translateY(-50%);
        width: 140px;
        height: 140px;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.04);
        overflow: hidden;
        z-index: 0;

        &.spinning {
          animation: spin-disc 20s linear infinite;
        }

        .disc-img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          border-radius: 50%;
        }
      }
    }

    .np-text {
      flex: 1;
      min-width: 0;

      .np-song-name {
        font-size: 22px;
        font-weight: 700;
        color: rgba(255, 255, 255, 0.9);
        margin-bottom: 8px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .np-artist {
        font-size: 13px;
        color: rgba(255, 255, 255, 0.4);
      }
    }
  }

  /* 大进度条 */
  .progress-panel {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 0 20px 20px;

    .time-label {
      font-size: 11px;
      color: rgba(255, 255, 255, 0.35);
      flex-shrink: 0;
      font-variant-numeric: tabular-nums;
    }

    .progress-track-panel {
      flex: 1;
      height: 4px;
      background: rgba(255, 255, 255, 0.1);
      border-radius: 2px;
      cursor: pointer;
      position: relative;

      &:hover { height: 6px; }
    }

    .progress-fill-panel {
      height: 100%;
      background: linear-gradient(90deg, #ec4141 0%, #ff6b6b 100%);
      border-radius: 2px;
      transition: width 0.3s linear;
      position: relative;

      .progress-thumb {
        position: absolute;
        right: -5px;
        top: 50%;
        transform: translateY(-50%) scale(0);
        width: 10px;
        height: 10px;
        border-radius: 50%;
        background: #ec4141;
        transition: transform 0.2s;
      }
    }

    &:hover .progress-thumb {
      transform: translateY(-50%) scale(1);
    }
  }

  /* 大控制按钮 */
  .big-controls {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 24px;
    padding-bottom: 20px;

    .control-btn {
      color: rgba(255, 255, 255, 0.5);
      font-size: 18px;
      cursor: pointer;
      transition: color 0.2s;
      &:hover { color: rgba(255, 255, 255, 0.85); }
      &.active { color: #ec4141; }
    }

    .big-ctrl {
      font-size: 28px;
      color: rgba(255, 255, 255, 0.6);
      cursor: pointer;
      transition: all 0.2s;
      &:hover { color: rgba(255, 255, 255, 0.9); transform: scale(1.1); }
    }

    .big-play-btn {
      width: 52px;
      height: 52px;
      border-radius: 50%;
      background: linear-gradient(135deg, #ec4141 0%, #d93b3b 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      box-shadow: 0 4px 16px rgba(236, 65, 65, 0.4);
      transition: all 0.2s;

      i {
        font-size: 26px;
        color: #fff;
        margin-left: 2px;
      }

      &:hover {
        transform: scale(1.08);
        box-shadow: 0 6px 24px rgba(236, 65, 65, 0.55);
      }
    }
  }

  /* 播放列表 */
  .playlist-panel {
    flex: 1;
    overflow-y: auto;
    border-top: 1px solid rgba(255, 255, 255, 0.05);
    padding: 0 8px 8px;

    &::-webkit-scrollbar { width: 4px; }
    &::-webkit-scrollbar-track { background: transparent; }
    &::-webkit-scrollbar-thumb { background: rgba(255, 255, 255, 0.12); border-radius: 2px; }

    .playlist-header {
      padding: 10px 12px 6px;
      font-size: 12px;
      color: rgba(255, 255, 255, 0.3);
    }

    .playlist-items {
      padding-bottom: 10px;
    }

    .playlist-item {
      display: flex;
      align-items: center;
      padding: 8px 12px;
      border-radius: 6px;
      cursor: pointer;
      transition: background 0.15s;

      &:hover {
        background: rgba(255, 255, 255, 0.05);
      }

      &.playing {
        background: rgba(236, 65, 65, 0.12);
      }

      .pli-info {
        flex: 1;
        min-width: 0;

        .pli-name {
          font-size: 13px;
          color: rgba(255, 255, 255, 0.85);
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          display: flex;
          align-items: center;
          gap: 6px;

          .playing-bar {
            display: inline-flex;
            align-items: flex-end;
            gap: 1px;
            height: 12px;

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
        }

        &.playing .pli-name { color: #ec4141; }

        .pli-artist {
          font-size: 11px;
          color: rgba(255, 255, 255, 0.35);
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }

      .pli-duration {
        font-size: 11px;
        color: rgba(255, 255, 255, 0.25);
        flex-shrink: 0;
        margin-left: 10px;
        font-variant-numeric: tabular-nums;
      }

      &.playing .pli-duration { color: rgba(236, 65, 65, 0.6); }
    }
  }
}

/* ====== 音量面板 ====== */
::v-deep .volume-popover {
  background: rgba(10, 14, 30, 0.95) !important;
  backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  padding: 12px 14px !important;
}

.volume-panel {
  display: flex;
  align-items: center;
  gap: 10px;

  .vol-icon {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.6);
    cursor: pointer;
    flex-shrink: 0;
    &.muted { color: rgba(255, 255, 255, 0.25); }
    &:hover { color: rgba(255, 255, 255, 0.9); }
  }

  .el-slider {
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
    color: rgba(255, 255, 255, 0.4);
    flex-shrink: 0;
    min-width: 30px;
    text-align: right;
  }
}

/* ====== 动画 ====== */
@keyframes spin-cover {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes spin-disc {
  from { transform: translateY(-50%) rotate(0deg); }
  to { transform: translateY(-50%) rotate(360deg); }
}

@keyframes sound-bar {
  from { transform: scaleY(0.4); }
  to { transform: scaleY(1); }
}

/* ====== 展开动画 ====== */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1),
              opacity 0.3s;
}
.slide-up-enter,
.slide-up-leave-to {
  transform: translateY(100%);
  opacity: 0;
}
</style>
