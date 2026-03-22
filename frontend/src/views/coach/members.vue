<template>
  <div class="coach-members-container">
    <el-card>
      <div class="header">
        <h2>我的学员</h2>
      </div>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="memberId" label="会员ID" width="80"></el-table-column>
        <el-table-column prop="memberName" label="姓名" width="120"></el-table-column>
        <el-table-column prop="gender" label="性别" width="60">
          <template slot-scope="scope">
            {{ scope.row.gender === 'M' ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="phoneNum" label="手机号" width="130"></el-table-column>
        <el-table-column prop="totalCourses" label="累计课程" width="100"></el-table-column>
        <el-table-column prop="completedCourses" label="已完成" width="100"></el-table-column>
        <el-table-column prop="remainingCourses" label="剩余" width="100"></el-table-column>
        <el-table-column prop="lastCourseDate" label="最近上课" width="120"></el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleView(scope.row)">查看详情</el-button>
            <el-button size="mini" type="success" @click="handleBodyTest(scope.row)">体测</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          :current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </el-card>

    <!-- 学员详情弹窗 -->
    <el-dialog
      :visible.sync="detailVisible"
      :title="`学员详情 — ${detailBasic.memberName || ''}`"
      width="900px"
      top="2vh"
      custom-class="member-detail-dialog"
      :close-on-click-modal="false"
    >
      <el-tabs v-model="activeTab" v-loading="detailLoading">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="会员ID">{{ detailBasic.memberId }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ detailBasic.memberName }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ detailBasic.gender === 'M' ? '男' : detailBasic.gender === 'F' ? '女' : '-' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ detailBasic.phoneNum }}</el-descriptions-item>
            <el-descriptions-item label="会员等级">
              <el-tag size="mini" :type="levelType(detailBasic.memberLevel)">{{ levelText(detailBasic.memberLevel) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="健身水平">{{ detailBasic.fitnessLevel || '-' }}</el-descriptions-item>
            <el-descriptions-item label="积分">{{ detailBasic.points ?? 0 }}</el-descriptions-item>
            <el-descriptions-item label="余额">¥ {{ detailBasic.balance ?? '0.00' }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ detailBasic.regTime | dateFormat }}</el-descriptions-item>
            <el-descriptions-item label="最近访问">{{ detailBasic.lastVisit | dateFormat }}</el-descriptions-item>
            <el-descriptions-item label="账户状态">
              <el-tag size="mini" :type="detailBasic.accountStatus === 0 ? 'success' : 'danger'" >
                {{ detailBasic.accountStatus === 0 ? '正常' : '冻结' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>

          <!-- 课程统计卡片 -->
          <div class="stat-cards">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-num">{{ courseStats.totalCourses }}</div>
              <div class="stat-label">累计课程</div>
            </el-card>
            <el-card shadow="hover" class="stat-card">
              <div class="stat-num completed">{{ courseStats.completedCourses }}</div>
              <div class="stat-label">已完成</div>
            </el-card>
            <el-card shadow="hover" class="stat-card">
              <div class="stat-num remaining">{{ courseStats.remainingCourses }}</div>
              <div class="stat-label">剩余</div>
            </el-card>
            <el-card shadow="hover" class="stat-card">
              <div class="stat-num">{{ courseStats.lastCourseDate || '-' }}</div>
              <div class="stat-label">最近上课</div>
            </el-card>
          </div>
        </el-tab-pane>

        <!-- 课程记录 -->
        <el-tab-pane label="课程记录" name="courses">
          <el-table :data="recentBookings" border size="small" max-height="380" v-loading="detailLoading">
            <el-table-column prop="courseName" label="课程名称" min-width="140"></el-table-column>
            <el-table-column prop="classTime" label="上课时间" width="170">
              <template slot-scope="scope">{{ scope.row.classTime | datetimeFormat }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template slot-scope="scope">
                <el-tag size="mini" :type="statusType(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="recentBookings.length === 0 && !detailLoading" class="empty-tip">暂无课程记录</div>
        </el-tab-pane>

        <!-- 体测记录 -->
        <el-tab-pane label="体测记录" name="bodyTest">
          <el-table :data="bodyTests" border size="small" max-height="380" v-loading="detailLoading">
            <el-table-column prop="testDate" label="测试日期" width="120"></el-table-column>
            <el-table-column label="身高/体重" width="120">
              <template slot-scope="scope">
                {{ scope.row.height || '-' }} cm / {{ scope.row.weight || '-' }} kg
              </template>
            </el-table-column>
            <el-table-column prop="bmi" label="BMI" width="80" align="center"></el-table-column>
            <el-table-column prop="bodyFatRate" label="体脂率" width="80" align="center">
              <template slot-scope="scope">{{ scope.row.bodyFatRate != null ? scope.row.bodyFatRate + '%' : '-' }}</template>
            </el-table-column>
            <el-table-column prop="muscleMass" label="肌肉量" width="90" align="center">
              <template slot-scope="scope">{{ scope.row.muscleMass != null ? scope.row.muscleMass + 'kg' : '-' }}</template>
            </el-table-column>
            <el-table-column prop="healthScore" label="健康评分" width="90" align="center">
              <template slot-scope="scope">
                <el-tag size="mini" :type="healthScoreType(scope.row.healthScore)">{{ scope.row.healthScore ?? '-' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="remarks" label="备注" min-width="100" show-overflow-tooltip></el-table-column>
          </el-table>
          <div v-if="bodyTests.length === 0 && !detailLoading" class="empty-tip">暂无体测记录</div>
        </el-tab-pane>

        <!-- 评价记录 -->
        <el-tab-pane label="评价记录" name="reviews">
          <el-table :data="reviews" border size="small" max-height="380" v-loading="detailLoading">
            <el-table-column prop="courseName" label="课程名称" width="140"></el-table-column>
            <el-table-column prop="rating" label="评分" width="100" align="center">
              <template slot-scope="scope">
                <el-rate v-model="scope.row.rating" disabled text-color="#ff9900" style="display:inline-block"></el-rate>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="评价内容" min-width="180" show-overflow-tooltip></el-table-column>
            <el-table-column prop="createTime" label="评价时间" width="170">
              <template slot-scope="scope">{{ scope.row.createTime | datetimeFormat }}</template>
            </el-table-column>
            <el-table-column prop="reply" label="教练回复" min-width="150" show-overflow-tooltip>
              <template slot-scope="scope">
                <span :class="{ 'has-reply': !!scope.row.reply }">{{ scope.row.reply || '暂无回复' }}</span>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="reviews.length === 0 && !detailLoading" class="empty-tip">暂无评价记录</div>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getCoachMembersPage, getCoachMemberDetail } from '@/api/coachDashboard'

export default {
  name: 'CoachMembers',
  data() {
    return {
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,

      // 详情弹窗
      detailVisible: false,
      detailLoading: false,
      activeTab: 'basic',
      currentMemberId: null,

      // 详情数据
      detailBasic: {},
      courseStats: {},
      recentBookings: [],
      bodyTests: [],
      reviews: []
    }
  },
  computed: {
    ...mapGetters(['userId'])
  },
  filters: {
    dateFormat(val) {
      if (!val) return '-'
      return val.substring ? val.substring(0, 10) : val
    },
    datetimeFormat(val) {
      if (!val) return '-'
      if (typeof val === 'string') return val.substring(0, 19).replace('T', ' ')
      return val
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getCoachMembersPage({
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }).then(res => {
        const page = res && res.data ? res.data : {}
        this.tableData = page.records || []
        this.total = page.total || 0
      }).catch(() => {
        this.tableData = []
        this.total = 0
        this.$message.error('获取学员列表失败')
      }).finally(() => {
        this.loading = false
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.getList()
    },

    handleView(row) {
      this.currentMemberId = row.memberId
      this.detailVisible = true
      this.activeTab = 'basic'
      this.fetchDetail()
    },

    fetchDetail() {
      this.detailLoading = true
      getCoachMemberDetail(this.currentMemberId).then(res => {
        if (res.code === 200 || res.code === 0) {
          const d = res.data
          this.detailBasic = d.basic || {}
          this.courseStats = d.courseStats || {}
          this.recentBookings = d.recentBookings || []
          this.bodyTests = d.bodyTests || []
          this.reviews = d.reviews || []
        } else {
          this.$message.error(res.message || res.msg || '获取详情失败')
          this.detailVisible = false
        }
      }).catch(err => {
        this.$message.error((err && err.message) ? err.message : '获取详情失败')
        this.detailVisible = false
      }).finally(() => {
        this.detailLoading = false
      })
    },

    handleBodyTest(row) {
      this.$router.push({
        path: '/coach-bodytest/add',
        query: { memberId: row.memberId, memberName: row.memberName }
      })
    },

    // ---- 辅助函数 ----
    levelText(level) {
      const map = { 1: '普通会员', 2: '银卡会员', 3: '金卡会员', 4: '钻石会员' }
      return map[level] || '普通会员'
    },
    levelType(level) {
      const map = { 1: 'info', 2: 'success', 3: 'warning', 4: 'danger' }
      return map[level] || 'info'
    },
    statusType(status) {
      const map = { '已签到': 'success', '已完成': 'primary', '已取消': 'info', '未签到': 'warning' }
      return map[status] || 'info'
    },
    healthScoreType(score) {
      if (score == null) return 'info'
      if (score >= 80) return 'success'
      if (score >= 60) return 'warning'
      return 'danger'
    }
  }
}
</script>

<style lang="scss" scoped>
.coach-members-container {
  .header {
    margin-bottom: 20px;
    h2 { margin: 0; }
  }
  .pagination {
    margin-top: 20px;
    text-align: right;
  }
}

.stat-cards {
  display: flex;
  gap: 12px;
  margin-top: 20px;
  .stat-card {
    flex: 1;
    text-align: center;
    .stat-num {
      font-size: 22px;
      font-weight: bold;
      color: #409EFF;
      &.completed { color: #67C23A; }
      &.remaining { color: #E6A23C; }
    }
    .stat-label {
      font-size: 12px;
      color: #909399;
      margin-top: 4px;
    }
  }
}

.empty-tip {
  text-align: center;
  color: #909399;
  padding: 20px 0;
  font-size: 14px;
}

.has-reply {
  color: #67C23A;
}
</style>

<style lang="scss">
// 全局覆盖 el-dialog 高度，避免内容溢出
.member-detail-dialog .el-dialog__body {
  max-height: calc(92vh - 130px);
  overflow-y: auto;
}
</style>
