<template>
  <div class="booking-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="会员ID">
            <el-input v-model.number="queryForm.memberId" placeholder="请输入会员ID" clearable></el-input>
          </el-form-item>
          <el-form-item label="课程名称">
            <el-input v-model="queryForm.courseName" placeholder="请输入课程名称" clearable></el-input>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryForm.status" placeholder="请选择" clearable>
              <el-option label="待审核" value="待审核"></el-option>
              <el-option label="已预约" value="已预约"></el-option>
              <el-option label="已取消" value="已取消"></el-option>
              <el-option label="已完成" value="已完成"></el-option>
              <el-option label="已签到" value="已签到"></el-option>
              <el-option label="已拒绝" value="已拒绝"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增预约</el-button>
      </div>

      <div class="table-wrapper">
        <el-table :data="tableData" border v-loading="loading">
          <el-table-column prop="bookingId" label="预约ID" width="80"></el-table-column>
          <el-table-column prop="memberId" label="会员ID" width="80"></el-table-column>
          <el-table-column prop="memberName" label="会员姓名" width="100"></el-table-column>
          <el-table-column prop="courseName" label="课程名称" width="150"></el-table-column>
          <el-table-column prop="categoryName" label="课程分类" width="100"></el-table-column>
          <el-table-column prop="courseType" label="课程类型" width="80"></el-table-column>
          <el-table-column prop="coachName" label="教练" width="100"></el-table-column>
          <el-table-column prop="bookingTime" label="预约时间" width="160"></el-table-column>
          <el-table-column prop="classTime" label="上课时间" width="160"></el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="signinTime" label="签到时间" width="160"></el-table-column>
          <el-table-column label="操作" fixed="right" width="250">
            <template slot-scope="scope">
              <el-button v-if="scope.row.status === '待审核'" size="mini" type="success" @click="handleApprove(scope.row)">通过</el-button>
              <el-button v-if="scope.row.status === '待审核'" size="mini" type="danger" @click="handleReject(scope.row)">拒绝</el-button>
              <el-button v-if="scope.row.status === '已预约'" size="mini" type="warning" @click="handleCancel(scope.row)">取消</el-button>
              <el-button v-if="scope.row.status === '已预约'" size="mini" type="primary" @click="handleSignIn(scope.row)">签到</el-button>
              <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

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

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form ref="dataForm" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="会员ID" prop="memberId">
          <el-input v-model.number="formData.memberId" placeholder="请输入会员ID"></el-input>
        </el-form-item>
        <el-form-item label="课程ID" prop="courseId">
          <el-input v-model.number="formData.courseId" placeholder="请输入课程ID"></el-input>
        </el-form-item>
        <el-form-item label="教练ID" prop="coachId">
          <el-input v-model.number="formData.coachId" placeholder="请输入教练ID"></el-input>
        </el-form-item>
        <el-form-item label="预约时间" prop="bookingTime">
          <el-date-picker v-model="formData.bookingTime" type="datetime" placeholder="选择时间" style="width: 100%" value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker>
        </el-form-item>
        <el-form-item label="上课时间" prop="classTime">
          <el-date-picker v-model="formData.classTime" type="datetime" placeholder="选择时间" style="width: 100%" value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择" style="width: 100%">
            <el-option label="待审核" value="待审核"></el-option>
            <el-option label="已预约" value="已预约"></el-option>
            <el-option label="已取消" value="已取消"></el-option>
            <el-option label="已完成" value="已完成"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getCourseBookingPage, addCourseBooking, updateCourseBooking, deleteCourseBooking, approveBooking, cancelBooking, signInBooking } from '@/api/courseBooking'

export default {
  name: 'CourseBooking',
  data() {
    return {
      queryForm: {
        memberId: null,
        courseName: '',
        status: ''
      },
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '',
      formData: {
        bookingId: null,
        memberId: null,
        courseId: null,
        courseName: '',
        coachId: null,
        bookingTime: '',
        classTime: '',
        status: '待审核',
        remark: ''
      },
      rules: {
        memberId: [{ required: true, message: '请输入会员ID', trigger: 'blur' }],
        courseId: [{ required: true, message: '请输入课程ID', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getCourseBookingPage({
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
        memberId: null,
        courseName: '',
        status: ''
      }
      this.handleQuery()
    },
    handleAdd() {
      this.dialogTitle = '新增预约'
      this.formData = {
        bookingId: null,
        memberId: null,
        courseId: null,
        courseName: '',
        coachId: null,
        bookingTime: '',
        classTime: '',
        status: '待审核',
        remark: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑预约'
      this.formData = { ...row }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定要删除该预约吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteCourseBooking(row.bookingId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    handleApprove(row) {
      this.$confirm('确定要通过该预约吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        approveBooking(row.bookingId).then(() => {
          this.$message.success('审核通过')
          this.getList()
        }).catch(err => {
          this.$message.error(err.message || '操作失败')
        })
      })
    },
    handleReject(row) {
      this.$prompt('请输入拒绝原因', '拒绝预约', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /.+/,
        inputErrorMessage: '请输入拒绝原因'
      }).then(({ value }) => {
        updateCourseBooking({ bookingId: row.bookingId, status: '已拒绝', remark: value }).then(() => {
          this.$message.success('已拒绝')
          this.getList()
        }).catch(err => {
          this.$message.error(err.message || '操作失败')
        })
      }).catch(() => {})
    },
    handleCancel(row) {
      this.$confirm('确定要取消该预约吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        cancelBooking(row.bookingId).then(() => {
          this.$message.success('取消成功')
          this.getList()
        }).catch(err => {
          this.$message.error(err.message || '操作失败')
        })
      })
    },
    handleSignIn(row) {
      this.$confirm('确定要签到吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        signInBooking(row.bookingId).then(() => {
          this.$message.success('签到成功')
          this.getList()
        }).catch(err => {
          this.$message.error(err.message || '签到失败')
        })
      })
    },
    handleSubmit() {
      this.$refs.dataForm.validate(valid => {
        if (valid) {
          const api = this.formData.bookingId ? updateCourseBooking : addCourseBooking
          api(this.formData).then(() => {
            this.$message.success(this.formData.bookingId ? '更新成功' : '添加成功')
            this.dialogVisible = false
            this.getList()
          })
        }
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
    getStatusType(status) {
      const map = {
        '待审核': 'warning',
        '已预约': 'primary',
        '已取消': 'info',
        '已完成': 'success',
        '已签到': 'success',
        '已拒绝': 'danger'
      }
      return map[status] || ''
    }
  }
}
</script>

<style lang="scss" scoped>
.booking-container {
  .toolbar, .table-toolbar { margin-bottom: 20px; }
  .table-wrapper { width: 100%; overflow-x: auto; }
  .pagination { margin-top: 20px; text-align: right; }
}
</style>
