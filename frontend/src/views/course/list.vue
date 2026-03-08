<template>
  <div class="course-list-container">
    <el-card>
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

      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增课程</el-button>
      </div>

      <div class="table-wrapper">
        <el-table :data="tableData" border :style="{ width: '100%' }" v-loading="loading">
        <el-table-column prop="courseId" label="课程 ID" width="80"></el-table-column>
        <el-table-column prop="courseName" label="课程名称" width="150"></el-table-column>
        <el-table-column prop="courseType" label="课程类型" width="80"></el-table-column>
        <el-table-column prop="coachId" label="教练 ID" width="80"></el-table-column>
        <el-table-column prop="durationMin" label="时长 (分钟)" width="80"></el-table-column>
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
        <el-table-column label="操作" fixed="right" width="250">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
            <el-button size="mini" type="primary" @click="handleBook(scope.row)">预约</el-button>
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
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="formData.courseName" placeholder="请输入课程名称"></el-input>
        </el-form-item>
        <el-form-item label="课程类型" prop="courseType">
          <el-select v-model="formData.courseType" placeholder="请选择" style="width: 100%">
            <el-option label="团课" value="团课"></el-option>
            <el-option label="私教课" value="私教课"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="教练 ID" prop="coachId">
          <el-input-number v-model="formData.coachId" :min="1"></el-input-number>
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入课程描述"></el-input>
        </el-form-item>
        <el-form-item label="课程时长" prop="durationMin">
          <el-input-number v-model="formData.durationMin" :min="10" :max="180"></el-input-number>
        </el-form-item>
        <el-form-item label="课程价格" prop="price">
          <el-input-number v-model="formData.price" :min="0" :precision="2"></el-input-number>
        </el-form-item>
        <el-form-item label="最大容量" prop="maxCapacity">
          <el-input-number v-model="formData.maxCapacity" :min="1" :max="100"></el-input-number>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="formData.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="formData.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="教室" prop="room">
          <el-input v-model="formData.room" placeholder="请输入教室"></el-input>
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
import { getCoursePage, addCourse, updateCourse, deleteCourse, bookCourse } from '@/api/course'

export default {
  name: 'CourseList',
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
      dialogVisible: false,
      dialogTitle: '',
      formData: {
        courseId: null,
        courseName: '',
        courseType: '团课',
        coachId: null,
        description: '',
        durationMin: 60,
        price: 0,
        maxCapacity: 10,
        startTime: '',
        endTime: '',
        room: ''
      },
      rules: {
        courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
        courseType: [{ required: true, message: '请选择课程类型', trigger: 'change' }]
      }
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
    handleAdd() {
      this.dialogTitle = '新增课程'
      this.formData = {
        courseId: null,
        courseName: '',
        courseType: '团课',
        coachId: null,
        description: '',
        durationMin: 60,
        price: 0,
        maxCapacity: 10,
        startTime: '',
        endTime: '',
        room: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑课程'
      this.formData = { ...row }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该课程吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteCourse(row.courseId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    handleBook(row) {
      this.$prompt('请输入会员 ID', '预约课程', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^\d+$/,
        inputErrorMessage: '请输入有效的数字'
      }).then(({ value }) => {
        bookCourse(row.courseId, { memberId: value }).then(() => {
          this.$message.success('预约成功')
          this.getList()
        })
      }).catch(() => {})
    },
    handleSubmit() {
      this.$refs.dataForm.validate(valid => {
        if (valid) {
          const api = this.formData.courseId ? updateCourse : addCourse
          api(this.formData).then(() => {
            this.$message.success(this.formData.courseId ? '更新成功' : '添加成功')
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
.course-list-container {
  width: 100%;

  .toolbar {
    margin-bottom: 20px;
  }

  .table-toolbar {
    margin-bottom: 20px;
  }

  .table-wrapper {
    width: 100%;
    overflow-x: auto;
  }

  .el-table {
    min-width: 1200px;
  }

  .pagination {
    margin-top: 20px;
    text-align: right;
  }
}
</style>
