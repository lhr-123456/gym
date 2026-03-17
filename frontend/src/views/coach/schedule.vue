<template>
  <div class="schedule-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="教练ID">
            <el-input v-model.number="queryForm.coachId" placeholder="请输入教练ID" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增排班</el-button>
      </div>

      <div class="table-wrapper">
        <el-table :data="tableData" border v-loading="loading">
          <el-table-column prop="scheduleId" label="排班ID" width="80"></el-table-column>
          <el-table-column prop="coachId" label="教练ID" width="80"></el-table-column>
          <el-table-column prop="scheduleDate" label="排班日期" width="120"></el-table-column>
          <el-table-column prop="startTime" label="开始时间" width="100"></el-table-column>
          <el-table-column prop="endTime" label="结束时间" width="100"></el-table-column>
          <el-table-column prop="scheduleType" label="排班类型" width="100"></el-table-column>
          <el-table-column prop="location" label="地点" width="120"></el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 0 ? 'success' : 'info'">
                {{ scope.row.status === 0 ? '正常' : '取消' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right" width="150">
            <template slot-scope="scope">
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
        <el-form-item label="教练ID" prop="coachId">
          <el-input v-model.number="formData.coachId" placeholder="请输入教练ID"></el-input>
        </el-form-item>
        <el-form-item label="排班日期" prop="scheduleDate">
          <el-date-picker v-model="formData.scheduleDate" type="date" placeholder="选择日期" style="width: 100%" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-time-picker v-model="formData.startTime" placeholder="选择时间" style="width: 100%"></el-time-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-time-picker v-model="formData.endTime" placeholder="选择时间" style="width: 100%"></el-time-picker>
        </el-form-item>
        <el-form-item label="排班类型" prop="scheduleType">
          <el-select v-model="formData.scheduleType" placeholder="请选择" style="width: 100%">
            <el-option label="团课" value="团课"></el-option>
            <el-option label="私教" value="私教"></el-option>
            <el-option label="值班" value="值班"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="地点" prop="location">
          <el-input v-model="formData.location" placeholder="请输入地点"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">取消</el-radio>
          </el-radio-group>
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
import { getCoachSchedulePage, addCoachSchedule, updateCoachSchedule, deleteCoachSchedule } from '@/api/coachSchedule'

export default {
  name: 'CoachSchedule',
  data() {
    return {
      queryForm: {
        coachId: null
      },
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '',
      formData: {
        scheduleId: null,
        coachId: null,
        scheduleDate: '',
        startTime: '',
        endTime: '',
        scheduleType: '',
        location: '',
        status: 0
      },
      rules: {
        coachId: [{ required: true, message: '请输入教练ID', trigger: 'blur' }],
        scheduleDate: [{ required: true, message: '请选择排班日期', trigger: 'change' }]
      }
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
      this.queryForm = { coachId: null }
      this.handleQuery()
    },
    handleAdd() {
      this.dialogTitle = '新增排班'
      this.formData = {
        scheduleId: null,
        coachId: null,
        scheduleDate: '',
        startTime: '',
        endTime: '',
        scheduleType: '',
        location: '',
        status: 0
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑排班'
      this.formData = { ...row }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定要删除该排班吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteCoachSchedule(row.scheduleId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    handleSubmit() {
      this.$refs.dataForm.validate(valid => {
        if (valid) {
          // 格式化时间数据
          const submitData = { ...this.formData }
          if (submitData.startTime) {
            if (submitData.startTime instanceof Date) {
              submitData.startTime = this.formatTime(submitData.startTime)
            }
          }
          if (submitData.endTime) {
            if (submitData.endTime instanceof Date) {
              submitData.endTime = this.formatTime(submitData.endTime)
            }
          }

          const api = submitData.scheduleId ? updateCoachSchedule : addCoachSchedule
          api(submitData).then(() => {
            this.$message.success(submitData.scheduleId ? '更新成功' : '添加成功')
            this.dialogVisible = false
            this.getList()
          }).catch(err => {
            this.$message.error(err.message || '操作失败')
          })
        }
      })
    },
    formatTime(date) {
      if (!date) return ''
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      return `${hours}:${minutes}:${seconds}`
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.getList()
    }
  }
}
</script>

<style lang="scss" scoped>
.schedule-container {
  .toolbar, .table-toolbar { margin-bottom: 20px; }
  .table-wrapper { width: 100%; overflow-x: auto; }
  .pagination { margin-top: 20px; text-align: right; }
}
</style>
