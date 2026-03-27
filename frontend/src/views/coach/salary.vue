<template>
  <div class="coach-salary-container">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon base"><i class="el-icon-money"></i></div>
          <div class="stat-info">
            <div class="stat-value">¥{{ summary.baseSalary }}</div>
            <div class="stat-label">基本工资</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon course"><i class="el-icon-reading"></i></div>
          <div class="stat-info">
            <div class="stat-value">¥{{ summary.courseFee }}</div>
            <div class="stat-label">本月课时费</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon bonus"><i class="el-icon-star-on"></i></div>
          <div class="stat-info">
            <div class="stat-value">¥{{ summary.performanceBonus }}</div>
            <div class="stat-label">绩效奖金</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card highlight">
          <div class="stat-icon total"><i class="el-icon-wallet"></i></div>
          <div class="stat-info">
            <div class="stat-value">¥{{ summary.totalSalary }}</div>
            <div class="stat-label">本月总收入</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-info">
            <div class="stat-value">{{ summary.courseHours }}h</div>
            <div class="stat-label">本月课时数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-info">
            <div class="stat-value">{{ summary.monthEvalScore || '—' }}</div>
            <div class="stat-label">本月评价均分</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="chart-card" v-if="salaryTrendChart.length > 0">
      <div slot="header" class="card-header">
        <span><i class="el-icon-data-line"></i> 收入趋势（近6个月）</span>
      </div>
      <div ref="salaryChartRef" style="height: 260px;"></div>
    </el-card>

    <el-card>
      <div slot="header" class="card-header">
        <span><i class="el-icon-tickets"></i> 工资明细</span>
        <el-select v-model="filterMonth" size="small" placeholder="按月份筛选" clearable @change="handleQuery" style="width: 140px">
          <el-option v-for="m in monthOptions" :key="m" :label="m" :value="m"></el-option>
        </el-select>
      </div>
      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="salaryMonth" label="月份" width="110"></el-table-column>
        <el-table-column prop="baseSalary" label="基本工资" width="110">
          <template slot-scope="scope">¥{{ scope.row.baseSalary }}</template>
        </el-table-column>
        <el-table-column prop="courseBonus" label="课时费" width="110">
          <template slot-scope="scope">¥{{ scope.row.courseBonus }}</template>
        </el-table-column>
        <el-table-column prop="performanceBonus" label="绩效奖金" width="110">
          <template slot-scope="scope">¥{{ scope.row.performanceBonus }}</template>
        </el-table-column>
        <el-table-column label="应发工资" width="120">
          <template slot-scope="scope">
            <strong style="color:#67C23A;">¥{{ (scope.row.totalSalary || 0).toFixed(2) }}</strong>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : scope.row.status === 2 ? 'danger' : 'warning'" size="mini">
              {{ ['', '已发放', '已扣款'][scope.row.status] || '待发放' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="evalLevel" label="评级" width="80" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.evalLevel" size="mini"
              :type="scope.row.evalLevel === 'A' ? 'success' : scope.row.evalLevel === 'B' ? 'warning' : 'danger'">
              {{ scope.row.evalLevel }}级
            </el-tag>
            <span v-else style="color:#c0c4cc;">—</span>
          </template>
        </el-table-column>
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
import { getCoachSalaryPage } from '@/api/coachSalary'
import { getCoachSalarySummary } from '@/api/statistics'
import * as echarts from 'echarts'

export default {
  name: 'CoachSalary',
  data() {
    return {
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      filterMonth: '',
      monthOptions: [],
      salaryTrendChart: [],
      summary: {
        baseSalary: 0,
        courseFee: 0,
        performanceBonus: 0,
        totalSalary: 0,
        courseHours: 0,
        monthEvalScore: null
      }
    }
  },
  computed: {
    ...mapGetters(['userId', 'coachId'])
  },
  created() {
    this.fetchSummary()
    this.fetchList()
  },
  beforeDestroy() {
    this.salaryChart = null
  },
  methods: {
    fetchSummary() {
      getCoachSalarySummary(this.coachId || null).then(res => {
        if (res && res.code === 200) {
          const d = res.data || {}
          this.summary.baseSalary = (d.baseSalary || 0).toFixed(2)
          this.summary.courseFee = (d.monthCourseFee || 0).toFixed(2)
          this.summary.performanceBonus = (d.monthPerformanceBonus || 0).toFixed(2)
          this.summary.totalSalary = (d.monthTotalSalary || 0).toFixed(2)
          this.summary.courseHours = d.monthHours || 0
          this.summary.monthEvalScore = d.avgRating ? d.avgRating.toFixed(1) : null
        }
      }).catch(() => {})
    },
    fetchList() {
      this.loading = true
      const params = { pageNum: this.pageNum, pageSize: this.pageSize }
      if (this.filterMonth) params.salaryMonth = this.filterMonth
      getCoachSalaryPage(params).then(res => {
        if (res && res.code === 200) {
          const page = res.data || {}
          let records = page.records || []
          // 自动筛选当前教练（从薪资记录中匹配）
          if (this.coachId) {
            records = records.filter(r => r.coachId === this.coachId)
          }
          this.tableData = records
          this.total = records.length
          // 收集月份选项
          const months = [...new Set(records.map(r => r.salaryMonth).filter(Boolean))]
          this.monthOptions = months.sort().reverse()
          // 趋势图数据
          const trend = [...records].sort((a, b) => String(a.salaryMonth).localeCompare(String(b.salaryMonth))).slice(-6)
          this.salaryTrendChart = trend
          this.$nextTick(() => this.updateChart())
        }
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.pageNum = 1
      this.fetchList()
    },
    handlePageChange(page) {
      this.pageNum = page
      this.fetchList()
    },
    updateChart() {
      if (this.salaryTrendChart.length === 0) return
      const dom = this.$refs.salaryChartRef
      if (!dom) return
      if (!this.salaryChart) {
        this.salaryChart = echarts.init(dom)
      }
      const data = this.salaryTrendChart
      const option = {
        tooltip: { trigger: 'axis', formatter: p => `${p[0].name}<br/>总收入：¥${p[0].value}` },
        grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
        xAxis: { type: 'category', data: data.map(d => d.salaryMonth || ''), axisLabel: { fontSize: 11 } },
        yAxis: { type: 'value', axisLabel: { formatter: '¥{value}' } },
        series: [{
          data: data.map(d => d.totalSalary || 0),
          type: 'line',
          smooth: true,
          lineStyle: { width: 3, color: '#409EFF' },
          itemStyle: { color: '#409EFF' },
          areaStyle: { color: 'rgba(64,158,255,0.15)' }
        }]
      }
      this.salaryChart.setOption(option, { notMerge: true })
    }
  }
}
</script>

<style lang="scss" scoped>
.coach-salary-container {
  .stat-row { margin-bottom: 20px; }
  .stat-card {
    display: flex; align-items: center; gap: 16px;
    .stat-icon {
      width: 48px; height: 48px; border-radius: 10px;
      display: flex; align-items: center; justify-content: center;
      font-size: 22px; color: #fff;
      &.base { background: linear-gradient(135deg, #667eea, #764ba2); }
      &.course { background: linear-gradient(135deg, #4facfe, #00f2fe); }
      &.bonus { background: linear-gradient(135deg, #43e97b, #38f9d7); }
      &.total { background: linear-gradient(135deg, #f093fb, #f5576c); }
    }
    .stat-info {
      .stat-value { font-size: 20px; font-weight: bold; color: #303133; }
      .stat-label { font-size: 12px; color: #909399; margin-top: 4px; }
    }
    &.highlight .stat-value { color: #67C23A; }
  }
  .chart-card { margin-bottom: 20px; }
  .card-header {
    display: flex; justify-content: space-between; align-items: center;
    font-size: 15px; font-weight: bold;
    i { margin-right: 6px; }
  }
  .pagination { margin-top: 20px; text-align: right; }
}
</style>
