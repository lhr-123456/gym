<template>
  <div class="bento-dashboard">
    <!-- 欢迎 Hero 卡片 - 全宽 -->
    <section class="bento-hero">
      <div class="hero-content">
        <div class="hero-avatar">
          <img v-if="avatarUrl" :src="avatarUrl" :alt="displayName" @error="handleAvatarError" />
          <span v-else>{{ displayName ? displayName.charAt(0).toUpperCase() : 'M' }}</span>
        </div>
        <div class="hero-text">
          <h1 class="hero-greeting">{{ greeting }}，<span class="hero-name">{{ displayName }}</span></h1>
          <div class="hero-meta">
            <span class="hero-level">
              <i class="el-icon-medal"></i>
              {{ memberLevel }}
            </span>
            <span class="hero-date">{{ currentDate }}</span>
          </div>
        </div>
      </div>
      <div class="hero-decoration">
        <div class="deco-circle deco-1"></div>
        <div class="deco-circle deco-2"></div>
      </div>
    </section>

    <!-- 统计指标卡片组 - Bento 网格 -->
    <section class="bento-grid bento-stats">
      <div class="bento-card stat-card" @click="goTo('/member/card')">
        <div class="stat-icon card-icon">
          <i class="el-icon-postcard"></i>
        </div>
        <div class="stat-info">
          <span class="stat-label">会员卡</span>
          <span class="stat-value">{{ cardType }}</span>
        </div>
        <i class="el-icon-arrow-right stat-arrow"></i>
      </div>

      <div class="bento-card stat-card" @click="goTo('/course/available')">
        <div class="stat-icon times-icon">
          <i class="el-icon-tickets"></i>
        </div>
        <div class="stat-info">
          <span class="stat-label">剩余次数</span>
          <span class="stat-value accent">{{ remainingTimes }}<span class="stat-unit">次</span></span>
        </div>
        <i class="el-icon-arrow-right stat-arrow"></i>
      </div>

      <div class="bento-card stat-card" @click="goTo('/points-mall/goods')">
        <div class="stat-icon points-icon">
          <i class="el-icon-coin"></i>
        </div>
        <div class="stat-info">
          <span class="stat-label">我的积分</span>
          <span class="stat-value">{{ points }}</span>
        </div>
        <i class="el-icon-arrow-right stat-arrow"></i>
      </div>
    </section>

    <!-- 第二行：课程 + 快捷操作 -->
    <section class="bento-grid bento-main">
      <!-- 我的课程卡片 - 8列 -->
      <div class="bento-card course-card">
        <div class="card-header">
          <div class="header-left">
            <i class="el-icon-calendar header-icon"></i>
            <span class="header-title">我的课程</span>
          </div>
          <span class="header-badge" v-if="myCourses.length > 0">{{ myCourses.length }}个</span>
        </div>
        <div class="card-body" v-loading="coursesLoading">
          <template v-if="myCourses.length > 0">
            <div
              v-for="(item, idx) in myCourses.slice(0, 3)"
              :key="item.bookingId || idx"
              class="course-item"
            >
              <div class="course-time-badge" :class="{ today: item.isToday, tomorrow: item.isTomorrow }">
                <span class="badge-dot"></span>
                <span class="badge-text">{{ item.timeText }}</span>
              </div>
              <div class="course-details">
                <div class="course-name">{{ item.courseName || '课程' }}</div>
                <div class="course-meta">
                  <span><i class="el-icon-location"></i>{{ item.room || item.location || '-' }}</span>
                  <span><i class="el-icon-user"></i>{{ item.coachName || '-' }}</span>
                </div>
              </div>
              <div class="course-action">
                <span class="slots-text" v-if="item.slotsText">{{ item.slotsText }}</span>
                <span class="course-status" :class="{ today: item.isToday }">{{ item.isToday ? '今日' : item.isTomorrow ? '明日' : '' }}</span>
              </div>
            </div>
            <div class="view-more" @click="goTo('/course/my')">
              <span>查看全部课程</span>
              <i class="el-icon-arrow-right"></i>
            </div>
          </template>
          <div v-else class="empty-state">
            <i class="el-icon-calendar empty-icon"></i>
            <p class="empty-title">暂无预约课程</p>
            <p class="empty-desc">快去预约一门课程开始健身吧</p>
            <button class="apple-btn primary" @click="goTo('/course/available')">去预约课程</button>
          </div>
        </div>
      </div>

      <!-- 快捷操作 + 步数 - 4列 -->
      <div class="bento-stack">
        <!-- 快捷操作 -->
        <div class="bento-card actions-card">
          <div class="card-header compact">
            <div class="header-left">
              <i class="el-icon-grid header-icon"></i>
              <span class="header-title">快捷操作</span>
            </div>
          </div>
          <div class="actions-grid">
            <div class="action-btn" @click="goTo('/course/available')">
              <div class="action-icon book">
                <i class="el-icon-edit-outline"></i>
              </div>
              <span>预约课程</span>
            </div>
            <div class="action-btn" @click="handleBodyTest">
              <div class="action-icon test">
                <i class="el-icon-data-analysis"></i>
              </div>
              <span>体测记录</span>
            </div>
            <div class="action-btn" @click="handleContactCoach">
              <div class="action-icon coach">
                <i class="el-icon-chat-line-round"></i>
              </div>
              <span>联系教练</span>
            </div>
            <div class="action-btn" @click="handleSportData">
              <div class="action-icon data">
                <i class="el-icon-data-line"></i>
              </div>
              <span>运动数据</span>
            </div>
          </div>
        </div>

        <!-- 今日步数 -->
        <div class="bento-card steps-card">
          <div class="card-header compact">
            <div class="header-left">
              <i class="el-icon-wind header-icon"></i>
              <span class="header-title">今日步数</span>
            </div>
          </div>
          <div class="steps-content">
            <div class="steps-number">
              <span class="steps-current">{{ stepsCurrent.toLocaleString() }}</span>
              <span class="steps-separator">/</span>
              <span class="steps-target">{{ stepsTarget.toLocaleString() }}</span>
            </div>
            <el-progress
              :percentage="stepsPercent"
              :stroke-width="8"
              :color="stepsPercent >= 100 ? '#34C759' : '#0071E3'"
              :show-text="false"
            ></el-progress>
            <div class="steps-status" :class="{ completed: stepsPercent >= 100 }">
              {{ stepsPercent >= 100 ? '🎉 目标达成！' : `还差 ${(stepsTarget - stepsCurrent).toLocaleString()} 步` }}
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 健康数据卡片 -->
    <section class="bento-grid bento-health">
      <div class="bento-card health-card">
        <div class="card-header">
          <div class="header-left">
            <i class="el-icon-heart-fill header-icon"></i>
            <span class="header-title">健康数据</span>
          </div>
          <button class="apple-btn text" @click="handleHealthHistory">查看历史</button>
        </div>
        <div class="health-grid">
          <div class="health-item">
            <div class="health-chart">
              <svg viewBox="0 0 36 36" class="health-ring">
                <path class="ring-bg" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
                <path class="ring-fill weight" stroke-dasharray="75, 100" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
              </svg>
            </div>
            <div class="health-info">
              <span class="health-label">体重</span>
              <span class="health-value">{{ healthData.weight }}</span>
            </div>
          </div>
          <div class="health-item">
            <div class="health-chart">
              <svg viewBox="0 0 36 36" class="health-ring">
                <path class="ring-bg" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
                <path class="ring-fill bodyfat" stroke-dasharray="60, 100" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
              </svg>
            </div>
            <div class="health-info">
              <span class="health-label">体脂率</span>
              <span class="health-value">{{ healthData.bodyFat }}</span>
            </div>
          </div>
          <div class="health-item">
            <div class="health-chart">
              <svg viewBox="0 0 36 36" class="health-ring">
                <path class="ring-bg" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
                <path class="ring-fill muscle" stroke-dasharray="65, 100" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
              </svg>
            </div>
            <div class="health-info">
              <span class="health-label">肌肉量</span>
              <span class="health-value">{{ healthData.muscle }}</span>
            </div>
          </div>
          <div class="health-item">
            <div class="health-chart">
              <svg viewBox="0 0 36 36" class="health-ring">
                <path class="ring-bg" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
                <path class="ring-fill bmi" stroke-dasharray="70, 100" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
              </svg>
            </div>
            <div class="health-info">
              <span class="health-label">BMI</span>
              <span class="health-value">{{ healthData.bmi }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 智能推荐课程 -->
    <section class="bento-grid bento-recommend">
      <div class="bento-card recommend-card">
        <div class="card-header">
          <div class="header-left">
            <i class="el-icon-magic-stick header-icon"></i>
            <span class="header-title">智能推荐</span>
          </div>
          <el-tag size="small" effect="plain" class="recommend-tag">根据您的健身目标匹配</el-tag>
        </div>
        <div class="recommend-grid" v-loading="recommendLoading">
          <template v-if="recommendList.length > 0">
            <div
              v-for="(item, idx) in recommendList"
              :key="item.courseId || idx"
              class="recommend-item"
              @click="goTo('/course/available')"
            >
              <div class="recommend-image" :style="{ background: getCourseGradient(idx) }">
                <i class="el-icon-dish"></i>
              </div>
              <div class="recommend-content">
                <div class="recommend-type">
                  <el-tag size="mini" :type="item.courseType === '私教课' ? 'warning' : 'primary'" effect="plain">
                    {{ item.courseType || '团课' }}
                  </el-tag>
                </div>
                <h4 class="recommend-name">{{ item.courseName }}</h4>
                <p class="recommend-desc">{{ item.description || '专业课程' }}</p>
                <div class="recommend-meta">
                  <span><i class="el-icon-time"></i>{{ item.durationMin || 60 }}分钟</span>
                  <span><i class="el-icon-user"></i>{{ item.coachName || '待分配' }}</span>
                </div>
              </div>
              <div class="recommend-footer">
                <span class="recommend-price">{{ item.price > 0 ? '¥' + item.price : '免费' }}</span>
                <button class="apple-btn small primary" @click.stop="handleBookRecommend(item)">预约</button>
              </div>
            </div>
          </template>
          <div v-else-if="!recommendLoading" class="empty-state small">
            <i class="el-icon-goods empty-icon"></i>
            <p class="empty-title">暂无推荐课程</p>
            <button class="apple-btn primary" @click="goTo('/course/available')">浏览全部课程</button>
          </div>
        </div>
      </div>
    </section>

    <!-- 历史趋势对话框 -->
    <el-dialog
      title="健康数据历史趋势"
      :visible.sync="historyDialogVisible"
      width="720px"
      class="apple-dialog"
      @open="fetchHealthHistory"
    >
      <div v-loading="historyLoading" class="history-dialog-body">
        <el-table :data="healthHistoryList" border size="small" class="apple-table">
          <el-table-column prop="testDate" label="日期" width="120">
            <template slot-scope="scope">
              {{ formatHistoryDate(scope.row.testDate || scope.row.recordDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="weight" label="体重(kg)" width="100"></el-table-column>
          <el-table-column prop="bodyFatRate" label="体脂率(%)" width="100"></el-table-column>
          <el-table-column prop="muscleMass" label="肌肉量(kg)" width="110"></el-table-column>
          <el-table-column prop="bmi" label="BMI" width="80"></el-table-column>
        </el-table>
        <div v-if="healthHistoryList.length === 0 && !historyLoading" class="empty-history">
          暂无历史体测记录
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getMemberCardByMember } from '@/api/memberCard'
import { getCourseBookingList } from '@/api/courseBooking'
import { getLatestBodyTest, getMemberBodyTestByMember, getMemberBodyTestPage } from '@/api/memberBodyTest'
import { getCoachScheduleList } from '@/api/coachSchedule'
import { getAvailableCourses, bookCourse } from '@/api/course'
import { getMemberById } from '@/api/member'

export default {
  name: 'MemberDashboard',
  data() {
    return {
      cardType: '年卡会员',
      remainingTimes: 12,
      points: 580,
      memberLevel: '黄金会员',
      myCourses: [],
      coursesLoading: false,
      stepsCurrent: 6832,
      stepsTarget: 10000,
      healthData: {
        weight: '62.5kg',
        bodyFat: '23.5%',
        muscle: '42.3kg',
        bmi: '21.3'
      },
      recommendList: [],
      recommendLoading: false,
      fitnessLevel: null,
      historyDialogVisible: false,
      historyLoading: false,
      healthHistoryList: [],
      avatarUrl: ''
    }
  },
  computed: {
    ...mapGetters(['username', 'userId', 'memberId']),
    currentMemberId() {
      return this.memberId || this.userId
    },
    displayName() {
      return this.username || '会员'
    },
    greeting() {
      const hour = new Date().getHours()
      if (hour < 6) return '夜深了'
      if (hour < 9) return '早上好'
      if (hour < 12) return '上午好'
      if (hour < 14) return '中午好'
      if (hour < 18) return '下午好'
      if (hour < 22) return '晚上好'
      return '晚上好'
    },
    currentDate() {
      const now = new Date()
      const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      return `${now.getMonth() + 1}月${now.getDate()}日 ${weekdays[now.getDay()]}`
    },
    stepsPercent() {
      if (this.stepsTarget <= 0) return 0
      return Math.min(100, Math.round((this.stepsCurrent / this.stepsTarget) * 100))
    }
  },
  created() {
    this.fetchMemberCard()
    this.fetchMyCourses()
    this.fetchHealthData()
    this.fetchRecommend()
    this.avatarUrl = this.$store.state.user.avatar || ''
  },
  methods: {
    goTo(path) {
      if (path && this.$router) {
        this.$router.push(path).catch(() => {})
      }
    },
    fetchMemberCard() {
      const id = this.currentMemberId
      if (!id) return
      getMemberCardByMember(id).then(res => {
        if (res && res.code === 200 && res.data) {
          const d = res.data
          if (d.cardType != null || d.cardTypeName) this.cardType = d.cardTypeName || d.cardType || this.cardType
          if (d.remainingTimes != null) this.remainingTimes = d.remainingTimes
          if (d.points != null) this.points = d.points
          if (d.levelName) this.memberLevel = d.levelName
        }
      }).catch(() => {})
      getMemberById(id).then(res => {
        if (res && res.code === 200 && res.data) {
          if (res.data.fitnessLevel) this.fitnessLevel = res.data.fitnessLevel
          if (res.data.avatar) this.avatarUrl = res.data.avatar
        }
      }).catch(() => {})
    },
    fetchMyCourses() {
      const id = this.currentMemberId
      if (!id) return
      this.coursesLoading = true
      getCourseBookingList({ memberId: id }).then(res => {
        if (res && res.code === 200 && Array.isArray(res.data)) {
          const today = this.formatDate(new Date())
          const tomorrow = this.formatDate(new Date(Date.now() + 86400000))
          this.myCourses = (res.data || []).map(b => {
            const ct = b.classTime || ''
            const dateStr = ct ? this.formatDate(ct) : (b.scheduleDate || '')
            const start = this.getTimeFromClassTime(ct) || (b.startTime || '').toString().substring(0, 5)
            const isToday = dateStr === today
            const isTomorrow = dateStr === tomorrow
            let timeText = start || '-'
            if (isToday) timeText = '今日 ' + (start || '')
            else if (isTomorrow) timeText = '明日 ' + (start || '')
            return {
              ...b,
              timeText,
              isToday,
              isTomorrow,
              slotsText: b.maxCapacity != null ? `剩余${Math.max(0, (b.maxCapacity || 0) - (b.currentCapacity || 0))}名额` : ''
            }
          }).sort((a, b) => {
            const da = a.classTime || a.scheduleDate || ''
            const db = b.classTime || b.scheduleDate || ''
            return String(da).localeCompare(String(db))
          })
        }
      }).catch(() => {
        this.myCourses = []
      }).finally(() => {
        this.coursesLoading = false
      })
    },
    formatDate(d) {
      if (!d) return ''
      const date = new Date(d)
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${y}-${m}-${day}`
    },
    getTimeFromClassTime(ct) {
      if (!ct) return ''
      const s = typeof ct === 'string' ? ct : (ct && ct.toString ? ct.toString() : '')
      if (s.length >= 16) return s.substring(11, 16)
      const d = new Date(ct)
      if (!isNaN(d.getTime())) return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
      return ''
    },
    fetchHealthData() {
      const id = this.currentMemberId
      if (!id) return
      getLatestBodyTest(id).then(res => {
        if (res && res.code === 200 && res.data) {
          const d = res.data
          if (d.weight != null) this.healthData.weight = d.weight + 'kg'
          if (d.bodyFatRate != null) this.healthData.bodyFat = d.bodyFatRate + '%'
          if (d.muscleMass != null) this.healthData.muscle = d.muscleMass + 'kg'
          if (d.bmi != null) this.healthData.bmi = String(d.bmi)
        }
      }).catch(() => {})
    },
    fetchRecommend() {
      this.recommendLoading = true
      const params = { pageNum: 1, pageSize: 6 }
      if (this.fitnessLevel) {
        params.courseType = this.fitnessLevel
      }
      getAvailableCourses(params).then(res => {
        if (res && (res.code === 200 || res.code === '200')) {
          const page = res.data || {}
          const records = Array.isArray(page) ? page : (page.records || [])
          this.recommendList = records.slice(0, 6)
        }
      }).catch(() => {
        this.recommendList = []
      }).finally(() => {
        this.recommendLoading = false
      })
    },
    handleBookRecommend(item) {
      if (!this.currentMemberId) {
        this.$message.warning('请先登录')
        return
      }
      const remaining = (item.maxCapacity || 0) - (item.currentCapacity || 0)
      if (remaining <= 0) {
        this.$message.warning('该课程已满员')
        return
      }
      this.$confirm(`确认预约「${item.courseName}」吗？`, '预约课程', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        bookCourse(item.courseId, { memberId: this.currentMemberId, coachId: item.coachId }).then(res => {
          if (res && (res.code === 200 || res.code === '200')) {
            this.$message.success('预约成功！')
            this.fetchRecommend()
            this.fetchMyCourses()
          } else {
            this.$message.error(res.message || '预约失败')
          }
        }).catch(err => {
          this.$message.error(err.message || '预约失败')
        })
      }).catch(() => {})
    },
    handleBodyTest() {
      this.goTo('/member/bodyTest')
    },
    handleContactCoach() {
      this.goTo('/member/contactCoach')
    },
    handleSportData() {
      this.goTo('/member/sportData')
    },
    handleHealthHistory() {
      this.historyDialogVisible = true
    },
    fetchHealthHistory() {
      const id = this.currentMemberId
      if (!id) {
        this.healthHistoryList = []
        return
      }
      this.historyLoading = true
      const pagePromise = getMemberBodyTestPage({ memberId: id, pageNum: 1, pageSize: 50 }).catch(() => null)
      const listPromise = getMemberBodyTestByMember(id).catch(() => null)
      Promise.all([pagePromise, listPromise]).then(([pageRes, listRes]) => {
        let list = []
        if (pageRes && pageRes.code === 200 && pageRes.data) {
          const d = pageRes.data
          list = Array.isArray(d) ? d : (d.list || d.records || [])
        }
        if (list.length === 0 && listRes && listRes.code === 200 && listRes.data) {
          const d = listRes.data
          list = Array.isArray(d) ? d : [d]
        }
        if (list.length === 0) {
          list = [
            { testDate: this.formatDate(new Date()), weight: 62.5, bodyFatRate: 23.5, muscleMass: 42.3, bmi: 21.3 },
            { testDate: this.formatDate(new Date(Date.now() - 86400000 * 30)), weight: 63, bodyFatRate: 24, muscleMass: 42, bmi: 21.5 }
          ]
        }
        this.healthHistoryList = list
      }).finally(() => {
        this.historyLoading = false
      })
    },
    formatHistoryDate(val) {
      if (!val) return '-'
      const d = new Date(val)
      if (isNaN(d.getTime())) return String(val)
      return this.formatDate(d)
    },
    getCourseGradient(idx) {
      const gradients = [
        'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
        'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
        'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
        'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
        'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)'
      ]
      return gradients[idx % gradients.length]
    },
    handleAvatarError(e) {
      e.target.style.display = 'none'
    }
  }
}
</script>

<style lang="scss" scoped>
// ============================================================
// Apple Design System - Bento Grid Dashboard
// ============================================================

// Apple Design Tokens
$apple-primary: #0071E3;
$apple-secondary: #f5f5f7;
$apple-bg: #ffffff;
$apple-text: #1d1d1f;
$apple-text-secondary: #86868b;
$apple-border: rgba(0, 0, 0, 0.08);
$apple-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
$apple-shadow-hover: 0 8px 30px rgba(0, 0, 0, 0.12);
$apple-radius: 20px;
$apple-radius-sm: 12px;
$apple-font: -apple-system, BlinkMacSystemFont, "SF Pro Display", "SF Pro Text", "Helvetica Neue", Helvetica, Arial, sans-serif;

// Animations
$apple-transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);

// ============================================================
// Text Utilities (defined before use)
// ============================================================

@mixin text-truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@mixin text-clamp($lines: 2) {
  display: -webkit-box;
  -webkit-line-clamp: $lines;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

// ============================================================
// Layout Container
// ============================================================

.bento-dashboard {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: $apple-secondary;
  min-height: calc(100vh - 84px);

  @media (max-width: 768px) {
    padding: 16px;
    gap: 16px;
  }
}

// ============================================================
// Hero Section
// ============================================================

.bento-hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  border-radius: $apple-radius;
  padding: 32px;
  position: relative;
  overflow: hidden;
  box-shadow: $apple-shadow;

  .hero-content {
    position: relative;
    z-index: 2;
    display: flex;
    align-items: center;
    gap: 24px;
  }

  .hero-avatar {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32px;
    font-weight: 600;
    color: #fff;
    border: 3px solid rgba(255, 255, 255, 0.3);
    flex-shrink: 0;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .hero-text {
    .hero-greeting {
      font-size: 28px;
      font-weight: 600;
      color: #fff;
      margin: 0 0 8px;
      line-height: 1.2;

      .hero-name {
        font-weight: 700;
      }
    }

    .hero-meta {
      display: flex;
      align-items: center;
      gap: 16px;
      color: rgba(255, 255, 255, 0.9);

      .hero-level {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 14px;
        font-weight: 500;
        background: rgba(255, 255, 255, 0.2);
        padding: 4px 12px;
        border-radius: 20px;
      }

      .hero-date {
        font-size: 14px;
        opacity: 0.8;
      }
    }
  }

  .hero-decoration {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    width: 50%;
    pointer-events: none;

    .deco-circle {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);

      &.deco-1 {
        width: 300px;
        height: 300px;
        top: -100px;
        right: -50px;
      }

      &.deco-2 {
        width: 200px;
        height: 200px;
        bottom: -80px;
        right: 100px;
      }
    }
  }

  @media (max-width: 768px) {
    padding: 24px;

    .hero-content {
      flex-direction: column;
      text-align: center;
      gap: 16px;
    }

    .hero-avatar {
      width: 64px;
      height: 64px;
      font-size: 24px;
    }

    .hero-text {
      .hero-greeting {
        font-size: 22px;
      }

      .hero-meta {
        justify-content: center;
        flex-wrap: wrap;
      }
    }
  }
}

// ============================================================
// Bento Grid System
// ============================================================

.bento-grid {
  display: grid;
  gap: 20px;

  &.bento-stats {
    grid-template-columns: repeat(3, 1fr);
  }

  &.bento-main {
    grid-template-columns: 2fr 1fr;
  }

  &.bento-health {
    grid-template-columns: 1fr;
  }

  &.bento-recommend {
    grid-template-columns: 1fr;
  }

  @media (max-width: 1024px) {
    &.bento-main {
      grid-template-columns: 1fr;
    }
  }

  @media (max-width: 768px) {
    &.bento-stats {
      grid-template-columns: 1fr;
    }
  }
}

// ============================================================
// Bento Card Base
// ============================================================

.bento-card {
  background: $apple-bg;
  border-radius: $apple-radius;
  box-shadow: $apple-shadow;
  overflow: hidden;
  transition: $apple-transition;
  animation: cardFadeIn 0.5s ease-out forwards;
  opacity: 0;

  @for $i from 1 through 6 {
    &:nth-child(#{$i}) {
      animation-delay: #{$i * 0.08}s;
    }
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: $apple-shadow-hover;
  }

  &:active {
    transform: scale(0.98);
    opacity: 0.9;
  }
}

@keyframes cardFadeIn {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// ============================================================
// Card Header
// ============================================================

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);

  &.compact {
    padding: 16px 20px;
  }

  .header-left {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .header-icon {
    font-size: 20px;
    color: $apple-primary;
  }

  .header-title {
    font-size: 17px;
    font-weight: 600;
    color: $apple-text;
    letter-spacing: -0.3px;
  }

  .header-badge {
    font-size: 13px;
    color: $apple-text-secondary;
    background: $apple-secondary;
    padding: 4px 10px;
    border-radius: 10px;
  }
}

.card-body {
  padding: 20px 24px;
}

// ============================================================
// Stats Cards
// ============================================================

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px 24px;
  cursor: pointer;

  .stat-icon {
    width: 48px;
    height: 48px;
    border-radius: $apple-radius-sm;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 22px;
    margin-right: 16px;
    flex-shrink: 0;

    &.card-icon {
      background: linear-gradient(135deg, #667eea22, #764ba222);
      color: #667eea;
    }

    &.times-icon {
      background: linear-gradient(135deg, #f093fb22, #f5576c22);
      color: #f5576c;
    }

    &.points-icon {
      background: linear-gradient(135deg, #43e97b22, #38f9d722);
      color: #34C759;
    }
  }

  .stat-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 4px;

    .stat-label {
      font-size: 13px;
      color: $apple-text-secondary;
    }

    .stat-value {
      font-size: 20px;
      font-weight: 700;
      color: $apple-text;

      &.accent {
        color: $apple-primary;
      }

      .stat-unit {
        font-size: 14px;
        font-weight: 500;
      }
    }
  }

  .stat-arrow {
    font-size: 14px;
    color: $apple-text-secondary;
    transition: $apple-transition;
  }

  &:hover .stat-arrow {
    transform: translateX(4px);
    color: $apple-primary;
  }

  @media (max-width: 768px) {
    padding: 16px 20px;

    .stat-icon {
      width: 44px;
      height: 44px;
      font-size: 20px;
    }

    .stat-info .stat-value {
      font-size: 18px;
    }
  }
}

// ============================================================
// Course Card
// ============================================================

.course-card {
  .card-body {
    padding: 0;
  }
}

.course-item {
  display: flex;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  transition: $apple-transition;
  cursor: pointer;

  &:last-of-type {
    border-bottom: none;
  }

  &:hover {
    background: rgba(0, 113, 227, 0.03);
  }
}

.course-time-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 8px;
  background: $apple-secondary;
  margin-right: 16px;
  flex-shrink: 0;

  .badge-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: $apple-text-secondary;
  }

  .badge-text {
    font-size: 12px;
    font-weight: 500;
    color: $apple-text-secondary;
  }

  &.today {
    background: rgba(52, 199, 89, 0.1);

    .badge-dot {
      background: #34C759;
      animation: pulse 2s infinite;
    }

    .badge-text {
      color: #34C759;
      font-weight: 600;
    }
  }

  &.tomorrow {
    background: rgba(0, 113, 227, 0.1);

    .badge-dot {
      background: $apple-primary;
    }

    .badge-text {
      color: $apple-primary;
      font-weight: 500;
    }
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.course-details {
  flex: 1;
  min-width: 0;

  .course-name {
    font-size: 15px;
    font-weight: 600;
    color: $apple-text;
    margin-bottom: 4px;
    @include text-truncate;
  }

  .course-meta {
    display: flex;
    gap: 16px;
    font-size: 13px;
    color: $apple-text-secondary;

    span {
      display: flex;
      align-items: center;
      gap: 4px;

      i {
        font-size: 12px;
      }
    }
  }
}

.course-action {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;

  .slots-text {
    font-size: 12px;
    color: $apple-text-secondary;
  }

  .course-status {
    font-size: 11px;
    font-weight: 600;
    color: $apple-primary;
    opacity: 0;
    transition: $apple-transition;

    &.today {
      opacity: 1;
    }
  }
}

.view-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 14px;
  color: $apple-primary;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: $apple-transition;
  border-top: 1px solid rgba(0, 0, 0, 0.05);

  &:hover {
    background: rgba(0, 113, 227, 0.03);

    i {
      transform: translateX(4px);
    }
  }

  i {
    font-size: 12px;
    transition: $apple-transition;
  }
}

// ============================================================
// Bento Stack
// ============================================================

.bento-stack {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

// ============================================================
// Actions Card
// ============================================================

.actions-card {
  .actions-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    padding: 16px 20px;
  }
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 12px;
  background: $apple-secondary;
  border-radius: $apple-radius-sm;
  cursor: pointer;
  transition: $apple-transition;

  &:hover {
    background: rgba(0, 113, 227, 0.08);
    transform: translateY(-2px);

    .action-icon {
      transform: scale(1.1);
    }
  }

  &:active {
    transform: scale(0.96);
  }

  span {
    font-size: 13px;
    font-weight: 500;
    color: $apple-text;
  }
}

.action-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  transition: $apple-transition;

  &.book {
    background: linear-gradient(135deg, #667eea22, #764ba222);
    color: #667eea;
  }

  &.test {
    background: linear-gradient(135deg, #f093fb22, #f5576c22);
    color: #f5576c;
  }

  &.coach {
    background: linear-gradient(135deg, #4facfe22, #00f2fe22);
    color: #00a0e9;
  }

  &.data {
    background: linear-gradient(135deg, #43e97b22, #38f9d722);
    color: #34C759;
  }
}

// ============================================================
// Steps Card
// ============================================================

.steps-card {
  .steps-content {
    padding: 16px 20px;
  }

  .steps-number {
    display: flex;
    align-items: baseline;
    gap: 4px;
    margin-bottom: 12px;

    .steps-current {
      font-size: 28px;
      font-weight: 700;
      color: $apple-text;
      letter-spacing: -1px;
    }

    .steps-separator {
      font-size: 16px;
      color: $apple-text-secondary;
      margin: 0 2px;
    }

    .steps-target {
      font-size: 16px;
      color: $apple-text-secondary;
    }
  }

  .steps-status {
    margin-top: 10px;
    font-size: 13px;
    color: $apple-text-secondary;
    text-align: center;

    &.completed {
      color: #34C759;
      font-weight: 600;
    }
  }
}

// ============================================================
// Health Card
// ============================================================

.health-card {
  .health-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    padding: 24px;
  }

  @media (max-width: 768px) {
    .health-grid {
      grid-template-columns: repeat(2, 1fr);
      padding: 16px;
      gap: 12px;
    }
  }
}

.health-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: $apple-secondary;
  border-radius: $apple-radius-sm;
  transition: $apple-transition;

  &:hover {
    background: rgba(0, 113, 227, 0.05);
  }

  .health-chart {
    width: 64px;
    height: 64px;
  }

  .health-ring {
    width: 100%;
    height: 100%;
    transform: rotate(-90deg);

    .ring-bg {
      fill: none;
      stroke: rgba(0, 0, 0, 0.06);
      stroke-width: 3;
    }

    .ring-fill {
      fill: none;
      stroke-width: 3;
      stroke-linecap: round;
      transition: stroke-dasharray 1s ease-out;

      &.weight { stroke: #667eea; }
      &.bodyfat { stroke: #f5576c; }
      &.muscle { stroke: #34C759; }
      &.bmi { stroke: #00a0e9; }
    }
  }

  .health-info {
    text-align: center;

    .health-label {
      display: block;
      font-size: 12px;
      color: $apple-text-secondary;
      margin-bottom: 4px;
    }

    .health-value {
      font-size: 18px;
      font-weight: 700;
      color: $apple-text;
    }
  }
}

// ============================================================
// Recommend Card
// ============================================================

.recommend-card {
  .recommend-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
    padding: 24px;

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
      padding: 16px;
      gap: 16px;
    }
  }
}

.recommend-tag {
  font-size: 12px;
  background: rgba(0, 113, 227, 0.08);
  border: none;
  color: $apple-primary;
}

.recommend-item {
  background: $apple-secondary;
  border-radius: $apple-radius-sm;
  overflow: hidden;
  cursor: pointer;
  transition: $apple-transition;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $apple-shadow-hover;

    .recommend-image {
      transform: scale(1.05);
    }
  }

  &:active {
    transform: scale(0.98);
  }
}

.recommend-image {
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  color: rgba(255, 255, 255, 0.8);
  transition: $apple-transition;
}

.recommend-content {
  padding: 16px;

  .recommend-type {
    margin-bottom: 8px;
  }

  .recommend-name {
    font-size: 16px;
    font-weight: 600;
    color: $apple-text;
    margin: 0 0 6px;
    @include text-truncate;
  }

  .recommend-desc {
    font-size: 13px;
    color: $apple-text-secondary;
    margin: 0 0 12px;
    @include text-clamp(2);
    line-height: 1.4;
  }

  .recommend-meta {
    display: flex;
    gap: 16px;
    font-size: 12px;
    color: $apple-text-secondary;

    span {
      display: flex;
      align-items: center;
      gap: 4px;

      i {
        font-size: 11px;
      }
    }
  }
}

.recommend-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  background: rgba(255, 255, 255, 0.5);

  .recommend-price {
    font-size: 18px;
    font-weight: 700;
    color: #f5576c;
  }
}

// ============================================================
// Apple Buttons
// ============================================================

.apple-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 500;
  border-radius: $apple-radius-sm;
  border: none;
  cursor: pointer;
  transition: $apple-transition;

  &.primary {
    background: $apple-primary;
    color: #fff;

    &:hover {
      background: #0077ed;
      transform: translateY(-1px);
    }

    &:active {
      transform: scale(0.97);
    }
  }

  &.text {
    background: transparent;
    color: $apple-primary;
    padding: 6px 12px;

    &:hover {
      background: rgba(0, 113, 227, 0.08);
    }
  }

  &.small {
    padding: 6px 14px;
    font-size: 13px;
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
    transform: none;
  }
}

// ============================================================
// Empty State
// ============================================================

.empty-state {
  text-align: center;
  padding: 48px 24px;

  &.small {
    padding: 32px 24px;
  }

  .empty-icon {
    font-size: 48px;
    color: $apple-text-secondary;
    opacity: 0.5;
    margin-bottom: 16px;
  }

  .empty-title {
    font-size: 17px;
    font-weight: 600;
    color: $apple-text;
    margin: 0 0 8px;
  }

  .empty-desc {
    font-size: 14px;
    color: $apple-text-secondary;
    margin: 0 0 20px;
  }
}

// ============================================================
// Progress Bar Override
// ============================================================

::v-deep .el-progress {
  .el-progress-bar__outer {
    background: rgba(0, 0, 0, 0.06);
    border-radius: 4px;
  }

  .el-progress-bar__inner {
    border-radius: 4px;
  }
}

// ============================================================
// Dialog Override
// ============================================================

.apple-dialog {
  ::v-deep .el-dialog {
    border-radius: $apple-radius;
    overflow: hidden;

    .el-dialog__header {
      padding: 20px 24px;
      border-bottom: 1px solid rgba(0, 0, 0, 0.05);

      .el-dialog__title {
        font-size: 18px;
        font-weight: 600;
        color: $apple-text;
      }
    }

    .el-dialog__body {
      padding: 24px;
    }

    .el-dialog__footer {
      padding: 16px 24px;
      border-top: 1px solid rgba(0, 0, 0, 0.05);
    }
  }
}

.apple-table {
  ::v-deep .el-table__header-cell {
    background: $apple-secondary !important;
    color: $apple-text;
    font-weight: 600;
    font-size: 13px;
  }

  ::v-deep .el-table__row {
    &:hover > td {
      background: rgba(0, 113, 227, 0.03) !important;
    }
  }

  ::v-deep .el-table__cell {
    color: $apple-text-secondary;
  }
}

.empty-history {
  text-align: center;
  padding: 32px;
  color: $apple-text-secondary;
}

// ============================================================
// Responsive Adjustments
// ============================================================

@media (max-width: 768px) {
  .bento-hero {
    .hero-text .hero-greeting {
      font-size: 20px;
    }
  }

  .card-header {
    padding: 16px 20px;

    .header-title {
      font-size: 15px;
    }
  }

  .card-body {
    padding: 16px 20px;
  }

  .course-item {
    padding: 14px 20px;
    flex-wrap: wrap;
    gap: 8px;

    .course-details {
      flex-basis: calc(100% - 100px);
    }

    .course-action {
      flex-basis: 100%;
      flex-direction: row;
      justify-content: space-between;
      margin-top: 4px;
    }
  }
}

@media (max-width: 480px) {
  .bento-dashboard {
    padding: 12px;
    gap: 12px;
  }

  .bento-hero {
    padding: 20px;

    .hero-content {
      gap: 12px;
    }

    .hero-avatar {
      width: 56px;
      height: 56px;
      font-size: 24px;
    }

    .hero-text .hero-greeting {
      font-size: 18px;
    }
  }

  .stat-card {
    padding: 16px;

    .stat-icon {
      width: 40px;
      height: 40px;
      font-size: 18px;
    }

    .stat-info .stat-value {
      font-size: 16px;
    }
  }

  .recommend-card .recommend-grid {
    grid-template-columns: 1fr;
  }
}
</style>
