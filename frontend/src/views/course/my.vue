<template>
  <div class="my-course-container">
    <el-card>
      <div class="header">
        <h2>我的课程</h2>
      </div>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="courseId" label="课程 ID" width="80"></el-table-column>
        <el-table-column prop="courseName" label="课程名称" width="150"></el-table-column>
        <el-table-column prop="courseType" label="课程类型" width="80"></el-table-column>
        <el-table-column prop="durationMin" label="时长 (分钟)" width="100"></el-table-column>
        <el-table-column prop="price" label="价格" width="80"></el-table-column>
        <el-table-column prop="maxCapacity" label="最大容量" width="80"></el-table-column>
        <el-table-column prop="currentCapacity" label="已预约" width="80"></el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160"></el-table-column>
        <el-table-column prop="room" label="教室" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleView(scope.row)">查看详情</el-button>
            <el-button size="mini" type="primary" @click="handleManageBooking(scope.row)">管理预约</el-button>
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

    <el-dialog title="课程详情" :visible.sync="detailDialogVisible" width="600px">
      <el-descriptions :column="2" border v-if="currentCourse">
        <el-descriptions-item label="课程名称">{{ currentCourse.courseName }}</el-descriptions-item>
        <el-descriptions-item label="课程类型">{{ currentCourse.courseType }}</el-descriptions-item>
        <el-descriptions-item label="课程时长">{{ currentCourse.durationMin }} 分钟</el-descriptions-item>
        <el-descriptions-item label="价格">{{ currentCourse.price }}</el-descriptions-item>
        <el-descriptions-item label="最大容量">{{ currentCourse.maxCapacity }}</el-descriptions-item>
        <el-descriptions-item label="已预约">{{ currentCourse.currentCapacity }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ currentCourse.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ currentCourse.endTime }}</el-descriptions-item>
        <el-descriptions-item label="教室">{{ currentCourse.room }}</el-descriptions-item>
        <el-descriptions-item label="课程描述">{{ currentCourse.description || '无' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="管理预约" :visible.sync="bookingDialogVisible" width="600px">
      <div v-if="currentCourse">
        <h4>课程: {{ currentCourse.courseName }}</h4>
        <p>已预约会员: {{ currentCourse.currentCapacity }} / {{ currentCourse.maxCapacity }}</p>
      </div>
      <el-table :data="bookingList" border style="width: 100%; margin-top: 20px;">
        <el-table-column prop="bookingId" label="预约 ID" width="80"></el-table-column>
        <el-table-column prop="memberId" label="会员 ID" width="100"></el-table-column>
        <el-table-column prop="memberName" label="会员姓名" width="120"></el-table-column>
        <el-table-column prop="bookingTime" label="预约时间" width="160"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '已确认' : '已取消' }}
            </el-tag>
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
import { getCoursePage, getCourseBookings } from '@/api/course'

export default {
  name: 'MyCourse',
  data() {
    return {
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      detailDialogVisible: false,
      bookingDialogVisible: false,
      currentCourse: null,
      bookingList: []
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getCoursePage({
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }).then(response => {
        this.tableData = response.data.records
        this.total = response.data.total
        this.loading = false
      }).catch(() => {
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
      this.currentCourse = row
      this.detailDialogVisible = true
    },
    handleManageBooking(row) {
      this.currentCourse = row
      this.bookingList = []
      getCourseBookings(row.courseId).then(response => {
        this.bookingList = response.data || []
      }).catch(() => {
        this.bookingList = []
      })
      this.bookingDialogVisible = true
    },
    getStatusType(status) {
      const map = { 0: '', 1: 'danger', 2: 'warning' }
      return map[status] || ''
    },
    getStatusText(status) {
      const map = { 0: '正常', 1: '已取消', 2: '已满员' }
      return map[status] || '未知'
    }
  }
}
</script>

<style lang="scss" scoped>
.my-course-container {
  .header {
    margin-bottom: 20px;
    h2 {
      margin: 0;
    }
  }

  .pagination {
    margin-top: 20px;
    text-align: right;
  }
}
</style>
