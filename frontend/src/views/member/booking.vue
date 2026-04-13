<template>
  <div class="member-booking-container">
    <el-card>
      <div class="header">
        <h2>我的预约</h2>
      </div>

      <!-- 标签页筛选 -->
      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane label="全部" name="all"></el-tab-pane>
        <el-tab-pane label="今日" name="today"></el-tab-pane>
        <el-tab-pane label="未来" name="future"></el-tab-pane>
        <el-tab-pane label="历史" name="history"></el-tab-pane>
      </el-tabs>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="courseName" label="课程名称" min-width="120">
          <template slot-scope="scope">
            <div class="course-name">{{ scope.row.courseName || '课程' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="courseType" label="类型" width="80">
          <template slot-scope="scope">
            <el-tag size="small" :type="scope.row.courseType === '私教课' ? 'warning' : 'primary'">
              {{ scope.row.courseType || '团课' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="上课时间" min-width="160">
          <template slot-scope="scope">
            <div class="time-cell">
              <i class="el-icon-date"></i>
              {{ formatDateTime(scope.row.classTime) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="coachName" label="教练" width="100"></el-table-column>
        <el-table-column prop="room" label="教室" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status || '待确认' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="180">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status === '已预约'"
              size="mini"
              type="primary"
              @click="handleSignIn(scope.row)"
            >签到</el-button>
            <el-button
              v-if="scope.row.status === '已预约'"
              size="mini"
              type="danger"
              @click="handleCancel(scope.row)"
            >取消</el-button>
            <el-button
              v-if="scope.row.status === '已完成' || scope.row.status === '已签到'"
              size="mini"
              @click="handleReview(scope.row)"
            >评价</el-button>
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

    <!-- 评价对话框 -->
    <el-dialog title="课程评价" :visible.sync="reviewDialogVisible" width="500px">
      <el-form ref="reviewForm" :model="reviewForm" :rules="reviewRules" label-width="80px">
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="reviewForm.rating" :colors="['#99A9BF', '#F7BA2A', '#FF9900']"></el-rate>
        </el-form-item>
        <el-form-item label="评价" prop="content">
          <el-input v-model="reviewForm.content" type="textarea" :rows="4" placeholder="请输入评价内容"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReview">提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getCourseBookingList, cancelBooking, signInBooking } from '@/api/courseBooking'
import { addReview } from '@/api/courseReview'

export default {
  name: 'MemberBooking',
  data() {
    return {
      activeTab: 'all',
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      reviewDialogVisible: false,
      reviewForm: {
        rating: 5,
        content: ''
      },
      reviewRules: {
        rating: [{ required: true, message: '请选择评分', trigger: 'change' }],
        content: [{ required: true, message: '请输入评价内容', trigger: 'blur' }]
      },
      currentBooking: null
    }
  },
  computed: {
    ...mapGetters(['userId', 'memberId']),
    currentMemberId() {
      return this.memberId || this.userId
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    fetchList() {
      if (!this.currentMemberId) return
      this.loading = true
      const params = {
        memberId: this.currentMemberId,
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }
      getCourseBookingList(params).then(res => {
        if (res && res.code === 200) {
          let allData = res.data || []
          // Apply tab filter
          const todayStr = new Date().toISOString().slice(0, 10)
          
          if (this.activeTab === 'today') {
            allData = allData.filter(b => this.formatDate(b.classTime) === todayStr)
          } else if (this.activeTab === 'future') {
            allData = allData.filter(b => this.formatDate(b.classTime) > todayStr)
          } else if (this.activeTab === 'history') {
            allData = allData.filter(b => this.formatDate(b.classTime) < todayStr)
          }
          
          this.total = allData.length
          // 分页处理
          const start = (this.pageNum - 1) * this.pageSize
          const end = start + this.pageSize
          this.tableData = allData.slice(start, end)
        }
      }).catch(() => {
        this.tableData = []
        this.total = 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleTabChange() {
      this.pageNum = 1
      this.fetchList()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchList()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.fetchList()
    },
    formatDateTime(dateTime) {
      if (!dateTime) return '-'
      const d = new Date(dateTime)
      const date = d.toISOString().slice(0, 10)
      const time = d.toTimeString().slice(0, 5)
      return `${date} ${time}`
    },
    formatDate(date) {
      if (!date) return ''
      const d = new Date(date)
      return d.toISOString().slice(0, 10)
    },
    getStatusType(status) {
      const map = {
        '待审核': 'warning',
        '已预约': 'primary',
        '已签到': 'success',
        '已完成': 'info',
        '已取消': 'danger',
        '已拒绝': 'danger'
      }
      return map[status] || 'info'
    },
    handleSignIn(row) {
      this.$confirm('确认签到吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        signInBooking(row.bookingId).then(() => {
          this.$message.success('签到成功')
          this.fetchList()
        }).catch(err => {
          this.$message.error(err.message || '签到失败')
        })
      })
    },
    handleCancel(row) {
      this.$confirm('确认取消预约吗？取消后可能影响您的积分。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        cancelBooking(row.bookingId).then(() => {
          this.$message.success('取消成功')
          this.fetchList()
        }).catch(err => {
          this.$message.error(err.message || '取消失败')
        })
      })
    },
    handleReview(row) {
      this.currentBooking = row
      this.reviewForm = { rating: 5, content: '' }
      this.reviewDialogVisible = true
    },
    handleSubmitReview() {
      this.$refs.reviewForm.validate(valid => {
        if (valid) {
          const data = {
            courseId: this.currentBooking.courseId,
            coachId: this.currentBooking.coachId,
            memberId: this.currentMemberId,
            rating: this.reviewForm.rating,
            content: this.reviewForm.content
          }
          addReview(data).then(() => {
            this.$message.success('评价提交成功')
            this.reviewDialogVisible = false
          }).catch(err => {
            this.$message.error(err.message || '评价提交失败')
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.member-booking-container {
  .header {
    margin-bottom: 20px;
    h2 { margin: 0; color: #1e3a8a; font-weight: 700; }
  }
  .course-name {
    font-weight: 600;
    color: #1e3a8a;
  }
  .time-cell {
    color: #2563eb;
    font-weight: 500;
    i { margin-right: 4px; color: #2563eb; }
  }
  .pagination {
    margin-top: 20px;
    text-align: right;
  }
  ::v-deep .el-table__header-cell { color: #1e3a8a !important; font-weight: 700; }
  ::v-deep .el-table__row:hover td { background: rgba(37, 99, 235, 0.04) !important; }
}
</style>
