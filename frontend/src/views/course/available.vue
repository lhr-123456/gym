<template>
  <div class="available-course-container">
    <el-card>
      <div class="header">
        <h2>可预约课程</h2>
      </div>

      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="课程名称">
            <el-input v-model="queryForm.courseName" placeholder="请输入课程名称" clearable></el-input>
          </el-form-item>
          <el-form-item label="课程类型">
            <el-select v-model="queryForm.courseType" placeholder="请选择" clearable>
              <el-option label="团课" value="团课"></el-option>
              <el-option label="私教课" value="私教课"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="courseId" label="课程 ID" width="80"></el-table-column>
        <el-table-column prop="courseName" label="课程名称" width="150"></el-table-column>
        <el-table-column prop="courseType" label="课程类型" width="80"></el-table-column>
        <el-table-column prop="coachName" label="教练" width="100"></el-table-column>
        <el-table-column prop="durationMin" label="时长 (分钟)" width="100"></el-table-column>
        <el-table-column prop="price" label="价格" width="80"></el-table-column>
        <el-table-column prop="maxCapacity" label="最大容量" width="80"></el-table-column>
        <el-table-column prop="currentCapacity" label="已预约" width="80">
          <template slot-scope="scope">
            <span :class="{ 'full-capacity': scope.row.currentCapacity >= scope.row.maxCapacity }">
              {{ scope.row.currentCapacity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160"></el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="160"></el-table-column>
        <el-table-column prop="room" label="教室" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="120">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              :disabled="scope.row.currentCapacity >= scope.row.maxCapacity || scope.row.status !== 0"
              @click="handleBook(scope.row)"
            >
              预约
            </el-button>
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
        <el-descriptions-item label="教练">{{ currentCourse.coachName }}</el-descriptions-item>
        <el-descriptions-item label="课程时长">{{ currentCourse.durationMin }} 分钟</el-descriptions-item>
        <el-descriptions-item label="价格">{{ currentCourse.price }}</el-descriptions-item>
        <el-descriptions-item label="最大容量">{{ currentCourse.maxCapacity }}</el-descriptions-item>
        <el-descriptions-item label="已预约">{{ currentCourse.currentCapacity }}</el-descriptions-item>
        <el-descriptions-item label="剩余名额">{{ currentCourse.maxCapacity - currentCourse.currentCapacity }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ currentCourse.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ currentCourse.endTime }}</el-descriptions-item>
        <el-descriptions-item label="教室">{{ currentCourse.room }}</el-descriptions-item>
        <el-descriptions-item label="课程描述" :span="2">{{ currentCourse.description || '暂无描述' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          type="primary"
          :disabled="currentCourse && (currentCourse.currentCapacity >= currentCourse.maxCapacity || currentCourse.status !== 0)"
          @click="handleBook(currentCourse)"
        >
          立即预约
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAvailableCourses, bookCourse } from '@/api/course'
import { mapGetters } from 'vuex'

export default {
  name: 'AvailableCourse',
  data() {
    return {
      queryForm: {
        courseName: '',
        courseType: ''
      },
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      detailDialogVisible: false,
      currentCourse: null
    }
  },
  computed: {
    ...mapGetters([
      'userId'
    ])
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getAvailableCourses({
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        ...this.queryForm
      }).then(response => {
        this.tableData = response.data.records
        this.total = response.data.total
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
        courseName: '',
        courseType: ''
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
    handleBook(row) {
      this.$confirm('确定要预约该课程吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        bookCourse(row.courseId, {
          memberId: this.userId
        }).then(() => {
          this.$message.success('预约成功')
          this.detailDialogVisible = false
          this.getList()
        }).catch(err => {
          this.$message.error(err.message || '预约失败')
        })
      }).catch(() => {})
    },
    getStatusType(status) {
      const map = { 0: '', 1: 'danger', 2: 'warning' }
      return map[status] || ''
    },
    getStatusText(status) {
      const map = { 0: '可预约', 1: '已取消', 2: '已满员' }
      return map[status] || '未知'
    }
  }
}
</script>

<style lang="scss" scoped>
.available-course-container {
  .header {
    margin-bottom: 20px;
    h2 {
      margin: 0;
    }
  }

  .toolbar {
    margin-bottom: 20px;
  }

  .pagination {
    margin-top: 20px;
    text-align: right;
  }

  .full-capacity {
    color: #f56c6c;
    font-weight: bold;
  }
}
</style>
