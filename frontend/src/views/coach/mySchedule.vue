<template>
  <div class="coach-schedule-container">
    <el-card>
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="开始日期">
            <el-date-picker
              v-model="queryForm.startDate"
              type="date"
              placeholder="选择开始日期"
              value-format="yyyy-MM-dd"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="结束日期">
            <el-date-picker
              v-model="queryForm.endDate"
              type="date"
              placeholder="选择结束日期"
              value-format="yyyy-MM-dd"
            ></el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="scheduleDate" label="日期" width="120">
          <template slot-scope="scope">
            {{ formatDate(scope.row.scheduleDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="时间" width="150">
          <template slot-scope="scope">
            {{ scope.row.startTime }} - {{ scope.row.endTime }}
          </template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程名称" width="150"></el-table-column>
        <el-table-column prop="location" label="地点" width="120"></el-table-column>
        <el-table-column prop="scheduleType" label="类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="getTypeTag(scope.row.scheduleType)">{{ scope.row.scheduleType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currentCapacity" label="已预约" width="80">
          <template slot-scope="scope">
            {{ scope.row.currentCapacity || 0 }} / {{ scope.row.maxCapacity || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleViewBookings(scope.row)">
              查看预约
            </el-button>
            <el-button
              v-if="scope.row.status === 1"
              size="mini"
              type="success"
              @click="handleStartCourse(scope.row)"
            >开始</el-button>
            <el-button
              v-if="scope.row.status === 2"
              size="mini"
              type="warning"
              @click="handleEndCourse(scope.row)"
            >结束</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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

    <!-- 预约详情对话框 -->
    <el-dialog title="课程预约详情" :visible.sync="bookingDialogVisible" width="700px">
      <div v-if="currentSchedule" class="schedule-info">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="课程名称">{{ currentSchedule.courseName }}</el-descriptions-item>
          <el-descriptions-item label="时间">{{ currentSchedule.startTime }} - {{ currentSchedule.endTime }}</el-descriptions-item>
          <el-descriptions-item label="地点">{{ currentSchedule.location }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <el-table :data="bookingList" border style="width: 100%; margin-top: 20px;">
        <el-table-column prop="memberId" label="会员ID" width="80"></el-table-column>
        <el-table-column prop="memberName" label="会员姓名" width="120"></el-table-column>
        <el-table-column prop="bookingTime" label="预约时间" width="160"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getBookingStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="signinTime" label="签到时间" width="100"></el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status === '已预约'"
              size="mini"
              type="success"
              @click="handleSignin(scope.row)"
            >签到</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer">
        <el-button @click="bookingDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getCoachSchedulePage, getScheduleBookings, coachSignin, startSchedule, endSchedule } from '@/api/coachDashboard'

export default {
  name: 'CoachSchedule',
  data() {
    return {
      queryForm: {
        startDate: '',
        endDate: ''
      },
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      bookingDialogVisible: false,
      currentSchedule: null,
      bookingList: []
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getCoachSchedulePage({
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        startDate: this.queryForm.startDate,
        endDate: this.queryForm.endDate
      }).then(response => {
        this.tableData = response.data.records || []
        this.total = response.data.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.pageNum = 1
      this.getList()
    },
    handleReset() {
      this.queryForm = {
        startDate: '',
        endDate: ''
      }
      this.handleQuery()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.getList()
    },
    handleViewBookings(row) {
      this.currentSchedule = row
      this.bookingList = []
      getScheduleBookings(row.scheduleId).then(response => {
        this.bookingList = response.data || []
      }).catch(() => {
        this.bookingList = []
      })
      this.bookingDialogVisible = true
    },
    handleSignin(row) {
      this.$confirm('确认对该学员进行签到吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        coachSignin(row.bookingId).then(() => {
          this.$message.success('签到成功')
          this.handleViewBookings(this.currentSchedule)
        })
      })
    },
    handleStartCourse(row) {
      this.$confirm('确认开始课程吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        startSchedule(row.scheduleId).then(() => {
          this.$message.success('课程已开始')
          this.getList()
        })
      })
    },
    handleEndCourse(row) {
      this.$confirm('确认结束课程吗？结束后已签到学员将标记为已完成。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        endSchedule(row.scheduleId).then(() => {
          this.$message.success('课程已结束')
          this.getList()
        })
      })
    },
    formatDate(date) {
      if (!date) return ''
      return date
    },
    getTypeTag(type) {
      const map = {
        'class': 'primary',
        'training': 'success',
        'private': 'warning'
      }
      return map[type] || 'info'
    },
    getStatusType(status) {
      const map = {
        0: 'info',
        1: 'success',
        2: 'warning',
        3: ''
      }
      return map[status] || 'info'
    },
    getStatusText(status) {
      const map = {
        0: '待开始',
        1: '待上课',
        2: '进行中',
        3: '已完成'
      }
      return map[status] || '未知'
    },
    getBookingStatusType(status) {
      const map = {
        '待审核': 'warning',
        '已预约': 'primary',
        '已签到': 'success',
        '已完成': 'info',
        '已取消': 'danger'
      }
      return map[status] || 'info'
    }
  }
}
</script>

<style lang="scss" scoped>
.coach-schedule-container {
  .search-area {
    margin-bottom: 20px;
  }

  .pagination {
    margin-top: 20px;
    text-align: right;
  }

  .schedule-info {
    margin-bottom: 10px;
  }
}
</style>
