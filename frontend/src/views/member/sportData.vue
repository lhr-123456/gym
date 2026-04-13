<template>
  <div class="sport-data-container">
    <el-card>
      <div slot="header" class="card-header">
        <span><i class="el-icon-data-line"></i> 运动数据</span>
        <el-button size="mini" type="primary" icon="el-icon-refresh" @click="fetchAll" :loading="loading">刷新</el-button>
      </div>

      <!-- 今日概览 -->
      <div class="overview-row">
        <div class="overview-card">
          <div class="overview-label">今日步数</div>
          <div class="overview-value">{{ overview.stepsToday | numberFormat }} <span class="unit">步</span></div>
          <el-progress
            :percentage="overview.stepsPercent"
            :stroke-width="8"
            :color="overview.stepsPercent >= 100 ? '#67C23A' : '#409EFF'"
          ></el-progress>
          <div class="overview-target">目标 {{ overview.stepsTarget | numberFormat }} 步</div>
        </div>
        <div class="overview-card">
          <div class="overview-label">今日消耗</div>
          <div class="overview-value">{{ overview.caloriesToday }} <span class="unit">千卡</span></div>
        </div>
        <div class="overview-card">
          <div class="overview-label">本周运动天数</div>
          <div class="overview-value">{{ overview.weekActiveDays }} <span class="unit">天</span></div>
        </div>
        <div class="overview-card">
          <div class="overview-label">本周总时长</div>
          <div class="overview-value">{{ overview.weekTotalMinutes }} <span class="unit">分钟</span></div>
        </div>
      </div>

      <!-- 最新体测指标 -->
      <div v-if="latestTest" class="section-title">最新体测（ {{ latestTest.testDate }} ）</div>
      <div v-if="latestTest" class="body-test-row">
        <div class="body-test-card">
          <div class="bmi-label">BMI</div>
          <div class="bmi-value" :class="bmiClass">{{ latestTest.bmi != null ? latestTest.bmi.toFixed(1) : '-' }}</div>
          <div class="bmi-desc">{{ bmiDesc }}</div>
        </div>
        <div class="body-test-item">
          <div class="item-label">体脂率</div>
          <div class="item-value">{{ latestTest.bodyFatRate != null ? latestTest.bodyFatRate + '%' : '-' }}</div>
        </div>
        <div class="body-test-item">
          <div class="item-label">肌肉量</div>
          <div class="item-value">{{ latestTest.muscleMass != null ? latestTest.muscleMass + ' kg' : '-' }}</div>
        </div>
        <div class="body-test-item">
          <div class="item-label">健康评分</div>
          <div class="item-value" :class="healthScoreClass(latestTest.healthScore)">
            {{ latestTest.healthScore ?? '-' }}
          </div>
        </div>
        <div class="body-test-item">
          <div class="item-label">体重</div>
          <div class="item-value">{{ latestTest.weight != null ? latestTest.weight + ' kg' : '-' }}</div>
        </div>
        <div class="body-test-item">
          <div class="item-label">身高</div>
          <div class="item-value">{{ latestTest.height != null ? latestTest.height + ' cm' : '-' }}</div>
        </div>
      </div>
      <div v-else class="empty-tip small">
        <i class="el-icon-data-analysis"></i>
        <p>暂无体测记录，请教练为您安排体测</p>
      </div>

      <!-- 体测趋势图表 -->
      <div v-if="bodyTestHistory.length > 1" class="section-title" style="margin-top: 20px;">体测趋势</div>
      <div v-if="bodyTestHistory.length > 1" class="chart-container">
        <div ref="trendChartRef" class="trend-chart"></div>
      </div>
      <div v-else-if="bodyTestHistory.length === 1" class="chart-tip">
        <i class="el-icon-info"></i> 体测趋势需至少 2 条记录方可展示
      </div>

      <!-- 近期运动记录（来自课程签到） -->
      <div class="section-title" style="margin-top: 20px;">近期运动记录</div>
      <el-table :data="sportRecords" border v-loading="recordsLoading" class="sport-table">
        <el-table-column prop="classTime" label="日期" width="120">
          <template slot-scope="scope">{{ scope.row.classTime | dateFormat }}</template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程名称" width="150"></el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" width="120" align="center">
          <template slot-scope="scope">{{ scope.row.duration ?? '-' }}</template>
        </el-table-column>
        <el-table-column prop="calories" label="消耗(千卡)" width="120" align="center">
          <template slot-scope="scope">{{ scope.row.calories ?? '-' }}</template>
        </el-table-column>
        <el-table-column prop="coachName" label="教练" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" :type="statusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注"></el-table-column>
      </el-table>
      <div v-if="sportRecords.length === 0 && !recordsLoading" class="empty-tip">
        <i class="el-icon-data-analysis"></i>
        <p>暂无运动记录，完成课程后数据将同步展示</p>
      </div>
    </el-card>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import * as echarts from 'echarts'
import { getCourseBookingList } from '@/api/courseBooking'
import { checkTodaySignin, getMemberSigninList } from '@/api/memberSignin'
import { getLatestBodyTest, getMemberBodyTestByMember } from '@/api/memberBodyTest'

export default {
  name: 'MemberSportData',
  filters: {
    numberFormat(val) {
      if (val == null) return '0'
      return Number(val).toLocaleString()
    },
    dateFormat(val) {
      if (!val) return '-'
      if (typeof val === 'string') return val.substring ? val.substring(0, 10) : val
      return val
    }
  },
  data() {
    return {
      loading: false,
      recordsLoading: false,
      // 概览数据
      overview: {
        stepsToday: 0,
        stepsTarget: 10000,
        caloriesToday: 0,
        weekActiveDays: 0,
        weekTotalMinutes: 0,
        stepsPercent: 0
      },
      // 运动记录
      sportRecords: [],
      // 最新体测
      latestTest: null,
      // 体测历史（用于趋势图表）
      bodyTestHistory: [],
      chart: null,
      // 体测列表（计算本周数据用）
      allBookings: []
    }
  },
  computed: {
    ...mapGetters(['userId', 'memberId']),
    currentMemberId() {
      return this.memberId || this.userId
    },
    bmiClass() {
      const bmi = this.latestTest?.bmi
      if (bmi == null) return ''
      if (bmi < 18.5) return 'bmi-low'
      if (bmi < 24) return 'bmi-normal'
      if (bmi < 28) return 'bmi-overweight'
      return 'bmi-obese'
    },
    bmiDesc() {
      const bmi = this.latestTest?.bmi
      if (bmi == null) return '暂无数据'
      if (bmi < 18.5) return '偏瘦'
      if (bmi < 24) return '正常'
      if (bmi < 28) return '超重'
      return '肥胖'
    }
  },
  created() {
    this.fetchAll()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    if (this.chart) {
      this.chart.dispose()
      this.chart = null
    }
  },
  methods: {
    fetchAll() {
      this.fetchBodyTest()
      this.fetchBodyTestHistory()
      this.fetchBookings()
      this.fetchTodaySignin()
    },

    // 获取体测历史（用于趋势图表）
    fetchBodyTestHistory() {
      const mid = this.currentMemberId
      if (!mid) return
      getMemberBodyTestByMember(mid).then(res => {
        if ((res.code === 200 || res.code === 0) && Array.isArray(res.data)) {
          // 按日期升序排列（从早到晚）
          this.bodyTestHistory = res.data.slice().sort((a, b) => {
            const da = a.testDate ? new Date(a.testDate) : new Date(0)
            const db = b.testDate ? new Date(b.testDate) : new Date(0)
            return da - db
          })
          this.$nextTick(() => this.renderTrendChart())
        }
      }).catch(() => {})
    },

    // 渲染体测趋势图表
    renderTrendChart() {
      const history = this.bodyTestHistory
      if (!history || history.length < 2 || !this.$refs.trendChartRef) return
      if (this.chart) {
        this.chart.dispose()
        this.chart = null
      }
      this.chart = echarts.init(this.$refs.trendChartRef)
      const dates = history.map(h => h.testDate ? h.testDate.substring(0, 10) : '')
      const weights = history.map(h => h.weight != null ? h.weight : null)
      const bodyFats = history.map(h => h.bodyFatRate != null ? h.bodyFatRate : null)
      const bmis = history.map(h => h.bmi != null ? h.bmi : null)
      const healthScores = history.map(h => h.healthScore != null ? h.healthScore : null)

      const option = {
        backgroundColor: '#fff',
        tooltip: {
          trigger: 'axis',
          confine: true,
          formatter: function(params) {
            if (!params.length) return ''
            let html = `<strong>${params[0].axisValue}</strong><br/>`
            params.forEach(p => {
              if (p.value != null) {
                const unit = p.seriesName === '健康评分' ? '' :
                             p.seriesName === '体脂率' || p.seriesName === '本周完成率' ? '%' : ''
                html += `${p.marker} ${p.seriesName}: <b>${p.value}${unit}</b><br/>`
              }
            })
            return html
          }
        },
        legend: {
          data: ['体重(kg)', '体脂率(%)', 'BMI', '健康评分'],
          top: 0,
          textStyle: { fontSize: 12 }
        },
        grid: { left: '3%', right: '4%', bottom: '3%', top: '42px', containLabel: true },
        xAxis: {
          type: 'category',
          data: dates,
          boundaryGap: false,
          axisLabel: { fontSize: 11 }
        },
        yAxis: [
          {
            type: 'value',
            name: '数值',
            position: 'left',
            axisLabel: { fontSize: 11 }
          },
          {
            type: 'value',
            name: '评分',
            position: 'right',
            min: 0,
            max: 100,
            axisLabel: { fontSize: 11 }
          }
        ],
        series: [
          {
            name: '体重(kg)',
            type: 'line',
            data: weights,
            smooth: true,
            lineStyle: { width: 2 },
            itemStyle: { color: '#409EFF' },
            connectNulls: false
          },
          {
            name: '体脂率(%)',
            type: 'line',
            yAxisIndex: 0,
            data: bodyFats,
            smooth: true,
            lineStyle: { width: 2 },
            itemStyle: { color: '#F56C6C' },
            connectNulls: false
          },
          {
            name: 'BMI',
            type: 'line',
            yAxisIndex: 0,
            data: bmis,
            smooth: true,
            lineStyle: { width: 2, type: 'dashed' },
            itemStyle: { color: '#67C23A' },
            connectNulls: false
          },
          {
            name: '健康评分',
            type: 'line',
            yAxisIndex: 1,
            data: healthScores,
            smooth: true,
            lineStyle: { width: 2 },
            itemStyle: { color: '#E6A23C' },
            connectNulls: false
          }
        ]
      }
      this.chart.setOption(option)
    },

    // 获取最新体测
    fetchBodyTest() {
      const mid = this.currentMemberId
      if (!mid) return
      getLatestBodyTest(mid).then(res => {
        if ((res.code === 200 || res.code === 0) && res.data) {
          this.latestTest = res.data
        }
      }).catch(() => {})
    },

    // 获取全部课程预约（用于计算本周数据）
    fetchBookings() {
      this.recordsLoading = true
      const mid = this.currentMemberId
      if (!mid) {
        this.recordsLoading = false
        return
      }
      getCourseBookingList({ memberId: mid, pageNum: 1, pageSize: 200 }).then(res => {
        const list = (res && res.data && res.data.records) ? res.data.records : []
        // 取最近 20 条已完成/已签到记录
        const recent = list
          .filter(b => ['已完成', '已签到'].includes(b.status))
          .slice(0, 20)
          .map(b => ({
            ...b,
            classTime: b.classTime,
            duration: b.duration || 60, // 无时长字段则默认60分钟
            calories: b.calories || Math.round((b.duration || 60) * 7) // 默认按每小时约420千卡估算
          }))
        this.sportRecords = recent

        // 计算本周概览
        this.computeWeekOverview(list)
      }).catch(() => {
        this.sportRecords = []
      }).finally(() => {
        this.recordsLoading = false
      })
    },

    // 获取今日签到状态（用于今日步数估算）
    fetchTodaySignin() {
      const mid = this.currentMemberId
      if (!mid) return
      checkTodaySignin(mid).then(res => {
        if ((res.code === 200 || res.code === 0)) {
          // 有今日签到，估算步数约6000步（对应约300千卡消耗）
          if (res.data === true || res.data === 1) {
            this.overview.stepsToday = 6000
            this.overview.caloriesToday = 300
          }
        }
      }).catch(() => {})
    },

    // 计算本周概览
    computeWeekOverview(bookings) {
      const now = new Date()
      const weekStart = new Date(now)
      weekStart.setDate(now.getDate() - now.getDay() + 1) // 本周一
      weekStart.setHours(0, 0, 0, 0)

      const todayStr = now.toISOString().substring(0, 10)
      const weekBookings = bookings.filter(b => {
        if (!b.classTime) return false
        const d = typeof b.classTime === 'string' ? b.classTime.substring(0, 10) : ''
        const bd = new Date(d)
        return bd >= weekStart && ['已完成', '已签到'].includes(b.status)
      })

      // 活跃天数（去重日期）
      const activeDates = new Set(weekBookings.map(b => {
        const d = typeof b.classTime === 'string' ? b.classTime.substring(0, 10) : ''
        return d
      }).filter(d => d))
      // 今日是否已运动
      const todayActive = activeDates.has(todayStr)

      // 总时长（分钟）
      const totalMinutes = weekBookings.reduce((sum, b) => sum + (b.duration || 60), 0)

      // 今日若已运动，加上今日数据
      let todayCalories = this.overview.caloriesToday
      if (todayActive) {
        const todayBookings = weekBookings.filter(b => {
          const d = typeof b.classTime === 'string' ? b.classTime.substring(0, 10) : ''
          return d === todayStr
        })
        todayCalories = todayBookings.reduce((s, b) => s + (b.calories || Math.round((b.duration || 60) * 7)), 0)
        this.overview.caloriesToday = todayCalories || 300
      }

      this.overview.weekActiveDays = activeDates.size
      this.overview.weekTotalMinutes = totalMinutes
      // 步数 = 今日消耗千卡 × 约30步/千卡 估算
      if (todayCalories > 0) {
        this.overview.stepsToday = Math.round(todayCalories * 20)
      } else {
        // 无今日运动，默认6000步（已由签到接口决定）
      }
      this.overview.stepsPercent = Math.min(100, Math.round((this.overview.stepsToday / this.overview.stepsTarget) * 100))
    },

    statusType(status) {
      const map = { '已签到': 'success', '已完成': 'primary', '已取消': 'info', '待审核': 'warning' }
      return map[status] || 'info'
    },
    healthScoreClass(score) {
      if (score == null) return ''
      if (score >= 80) return 'score-high'
      if (score >= 60) return 'score-mid'
      return 'score-low'
    },
    handleResize() {
      if (this.chart) this.chart.resize()
    }
  }
}
</script>

<style lang="scss" scoped>
.sport-data-container {
  .card-header {
    font-size: 16px;
    font-weight: bold;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #1e3a8a;
    i { margin-right: 8px; color: #2563eb; }
  }
  .overview-row {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 24px;
  }
  .overview-card {
    padding: 20px;
    background: rgba(255, 255, 255, 0.22) !important;
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    border: 1px solid rgba(255, 255, 255, 0.45) !important;
    border-radius: 12px;
    box-shadow: 0 8px 24px rgba(30, 58, 138, 0.1);
    transition: all 0.3s;
    &:hover {
      background: rgba(255, 255, 255, 0.38) !important;
      box-shadow: 0 14px 36px rgba(30, 58, 138, 0.16);
      transform: translateY(-2px);
    }
    .overview-label { font-size: 13px; color: #3b82f6; margin-bottom: 8px; font-weight: 600; }
    .overview-value {
      font-size: 24px; font-weight: bold; color: #1e3a8a;
      .unit { font-size: 14px; color: #2563eb; font-weight: normal; margin-left: 4px; }
    }
    .overview-target { font-size: 12px; color: #60a5fa; margin-top: 8px; font-weight: 500; }
    .el-progress { margin-top: 12px; }
  }

  .body-test-row {
    display: flex;
    gap: 16px;
    align-items: center;
    flex-wrap: wrap;
    background: rgba(255, 255, 255, 0.22) !important;
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.45) !important;
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 8px;
    box-shadow: 0 4px 16px rgba(30, 58, 138, 0.08);
  }
  .body-test-card {
    text-align: center;
    padding: 8px 20px;
    border-right: 1px solid rgba(30, 58, 138, 0.12);
    &:last-child { border-right: none; }
    .bmi-label { font-size: 12px; color: #3b82f6; font-weight: 600; }
    .bmi-value {
      font-size: 32px; font-weight: bold;
      &.bmi-low { color: #E6A23C; }
      &.bmi-normal { color: #67C23A; }
      &.bmi-overweight { color: #E6A23C; }
      &.bmi-obese { color: #F56C6C; }
    }
    .bmi-desc { font-size: 12px; color: #60a5fa; font-weight: 500; }
  }
  .body-test-item {
    .item-label { font-size: 12px; color: #3b82f6; font-weight: 600; }
    .item-value {
      font-size: 18px; font-weight: bold; color: #1e3a8a;
      &.score-high { color: #67C23A; }
      &.score-mid { color: #E6A23C; }
      &.score-low { color: #F56C6C; }
    }
  }

  .chart-container {
    margin-bottom: 16px;
    background: rgba(255, 255, 255, 0.22) !important;
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.45) !important;
    border-radius: 12px;
    padding: 12px;
    box-shadow: 0 4px 16px rgba(30, 58, 138, 0.08);
  }
  .trend-chart {
    width: 100%;
    height: 300px;
  }
  .chart-tip {
    font-size: 13px;
    color: #3b82f6;
    padding: 12px 16px;
    background: rgba(255, 255, 255, 0.22) !important;
    backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.45) !important;
    border-radius: 8px;
    margin-bottom: 8px;
    font-weight: 500;
    i { margin-right: 6px; color: #2563eb; }
  }

  .section-title {
    font-size: 15px; font-weight: 700; color: #1e3a8a; margin-bottom: 16px;
  }
  .sport-table {
    margin-bottom: 16px;
    ::v-deep .el-table__header-cell { color: #1e3a8a; font-weight: 700; }
    ::v-deep .el-table__cell { color: #2563eb; }
    ::v-deep .el-table__row:hover td { background: rgba(37, 99, 235, 0.04) !important; }
  }

  .empty-tip {
    text-align: center; padding: 40px; color: #2563eb;
    i { font-size: 48px; display: block; margin-bottom: 12px; color: #2563eb; }
    p { margin: 0; color: #3b82f6; }
    &.small { padding: 16px 40px; i { font-size: 32px; } }
  }
}
</style>
