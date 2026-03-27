<template>
  <div class="coach-performance-container">
    <!-- 绩效概览卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">本月评价均分</div>
          <div class="stat-value large">{{ avgRating || '—' }}</div>
          <div class="stat-sub">满分 5.0</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">本月课时数</div>
          <div class="stat-value large">{{ monthHours || 0 }}</div>
          <div class="stat-sub">已完成</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">出勤率</div>
          <div class="stat-value large">{{ attendanceRate || '—' }}</div>
          <div class="stat-sub">本月</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 多维评分雷达图 + 评价分布 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card>
          <div slot="header" class="card-header"><i class="el-icon-data-analysis"></i> 多维评分</div>
          <div ref="radarChartRef" style="height: 280px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header" class="card-header"><i class="el-icon-pie-chart"></i> 评价分布</div>
          <div ref="pieChartRef" style="height: 280px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 绩效记录列表 -->
    <el-card>
      <div slot="header" class="card-header">
        <span><i class="el-icon-tickets"></i> 绩效记录</span>
        <el-select v-model="filterMonth" size="small" placeholder="按月份筛选" clearable @change="handleQuery" style="width: 140px">
          <el-option v-for="m in monthOptions" :key="m" :label="m" :value="m"></el-option>
        </el-select>
      </div>
      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="evalMonth" label="评估月份" width="110"></el-table-column>
        <el-table-column prop="attendanceScore" label="出勤评分" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.attendanceScore || '-' }}</template>
        </el-table-column>
        <el-table-column prop="teachingScore" label="教学评分" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.teachingScore || '-' }}</template>
        </el-table-column>
        <el-table-column prop="serviceScore" label="服务评分" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.serviceScore || '-' }}</template>
        </el-table-column>
        <el-table-column prop="salesScore" label="销售评分" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.salesScore || '-' }}</template>
        </el-table-column>
        <el-table-column prop="studentFeedback" label="学员反馈" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.studentFeedback || '-' }}</template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总分" width="80" align="center">
          <template slot-scope="scope">
            <strong>{{ scope.row.totalScore || '-' }}</strong>
          </template>
        </el-table-column>
        <el-table-column prop="evalLevel" label="评级" width="80" align="center">
          <template slot-scope="scope">
            <el-tag size="mini"
              :type="scope.row.evalLevel === 'A' ? 'success' : scope.row.evalLevel === 'B' ? 'warning' : 'danger'">
              {{ scope.row.evalLevel || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="evalRemarks" label="备注" min-width="120" show-overflow-tooltip></el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          :current-page="pageNum" :page-size="pageSize" :total="total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getCoachPerformancePage } from '@/api/coachPerformance'
import { getCoachReviews } from '@/api/coachDashboard'
import { getCoachPerformanceSummary } from '@/api/statistics'
import * as echarts from 'echarts'

export default {
  name: 'CoachPerformance',
  data() {
    return {
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      filterMonth: '',
      monthOptions: [],
      avgRating: null,
      monthHours: 0,
      attendanceRate: null,
      radarChart: null,
      pieChart: null,
      reviewStats: { 5: 0, 4: 0, 3: 0, 2: 0, 1: 0 }
    }
  },
  computed: {
    ...mapGetters(['userId', 'coachId'])
  },
  mounted() {
    this.$nextTick(() => {
      this.initCharts()
    })
  },
  beforeDestroy() {
    if (this.radarChart) this.radarChart.dispose()
    if (this.pieChart) this.pieChart.dispose()
  },
  created() {
    this.fetchSummary()
    this.fetchReviews()
    this.fetchList()
  },
  methods: {
    fetchSummary() {
      getCoachPerformanceSummary(this.coachId || null).then(res => {
        if (res && res.code === 200) {
          const d = res.data || {}
          this.avgRating = d.avgRating ? d.avgRating.toFixed(1) : null
          this.monthHours = d.monthHours || 0
          this.attendanceRate = d.attendanceRate || null
        }
      }).catch(() => {})
    },
    fetchReviews() {
      getCoachReviews(null).then(res => {
        if (res && res.code === 200) {
          const reviews = res.data || []
          const stats = { 5: 0, 4: 0, 3: 0, 2: 0, 1: 0 }
          reviews.forEach(r => {
            const rating = Math.round(r.rating || 0)
            if (rating >= 1 && rating <= 5) stats[rating]++
          })
          this.reviewStats = stats
          this.$nextTick(() => this.updatePieChart())
        }
      }).catch(() => {})
    },
    fetchList() {
      this.loading = true
      const params = { pageNum: this.pageNum, pageSize: this.pageSize }
      if (this.filterMonth) params.evalMonth = this.filterMonth
      getCoachPerformancePage(params).then(res => {
        if (res && res.code === 200) {
          const page = res.data || {}
          let records = page.records || []
          if (this.coachId) {
            records = records.filter(r => r.coachId === this.coachId)
          }
          this.tableData = records
          this.total = records.length
          const months = [...new Set(records.map(r => r.evalMonth).filter(Boolean))]
          this.monthOptions = months.sort().reverse()
          this.updateRadar()
        }
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    handleQuery() {
      this.pageNum = 1
      this.fetchList()
    },
    handlePageChange(page) {
      this.pageNum = page
      this.fetchList()
    },
    initCharts() {
      this.radarChart = echarts.init(this.$refs.radarChartRef)
      this.pieChart = echarts.init(this.$refs.pieChartRef)
    },
    updateRadar() {
      if (!this.radarChart || this.tableData.length === 0) return
      const latest = this.tableData[0]
      const radarOption = {
        tooltip: {},
        radar: {
          indicator: [
            { name: '出勤', max: 100 },
            { name: '教学', max: 100 },
            { name: '服务', max: 100 },
            { name: '销售', max: 100 },
            { name: '学员反馈', max: 100 }
          ],
          radius: '65%'
        },
        series: [{
          type: 'radar',
          data: [{
            value: [
              latest.attendanceScore || 0,
              latest.teachingScore || 0,
              latest.serviceScore || 0,
              latest.salesScore || 0,
              latest.studentFeedback || 0
            ],
            name: '本月评分',
            areaStyle: { color: 'rgba(64,158,255,0.3)' },
            lineStyle: { color: '#409EFF' },
            itemStyle: { color: '#409EFF' }
          }]
        }]
      }
      this.radarChart.setOption(radarOption)
    },
    updatePieChart() {
      if (!this.pieChart) return
      const s = this.reviewStats
      const hasData = Object.values(s).some(v => v > 0)
      if (!hasData) return
      const pieOption = {
        tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
        legend: { bottom: 0 },
        color: ['#67C23A', '#409EFF', '#E6A23C', '#F56C6C', '#909399'],
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          data: [
            { value: s[5], name: '5星' },
            { value: s[4], name: '4星' },
            { value: s[3], name: '3星' },
            { value: s[2], name: '2星' },
            { value: s[1], name: '1星' }
          ]
        }]
      }
      this.pieChart.setOption(pieOption)
    }
  }
}
</script>

<style lang="scss" scoped>
.coach-performance-container {
  .stat-row { margin-bottom: 20px; }
  .chart-row { margin-bottom: 20px; }
  .stat-card {
    text-align: center; padding: 20px 0;
    .stat-label { font-size: 13px; color: #909399; margin-bottom: 10px; }
    .stat-value { font-size: 32px; font-weight: bold; color: #303133; }
    .stat-value.large { color: #409EFF; }
    .stat-sub { font-size: 12px; color: #c0c4cc; margin-top: 4px; }
  }
  .card-header {
    font-size: 15px; font-weight: bold;
    display: flex; justify-content: space-between; align-items: center;
    i { margin-right: 6px; }
  }
  .pagination { margin-top: 20px; text-align: right; }
}
</style>
