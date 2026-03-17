<template>
  <div class="shift-container">
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
        <el-button type="primary" @click="handleAdd">新增调班</el-button>
      </div>

      <div class="table-wrapper">
        <el-table :data="tableData" border v-loading="loading">
          <el-table-column prop="shiftId" label="调班ID" width="80"></el-table-column>
          <el-table-column prop="coachId" label="教练ID" width="80"></el-table-column>
          <el-table-column prop="originalDate" label="原日期" width="120"></el-table-column>
          <el-table-column prop="targetDate" label="目标日期" width="120"></el-table-column>
          <el-table-column prop="reason" label="调班原因"></el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 0 ? 'warning' : scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 0 ? '待审核' : scope.row.status === 1 ? '已同意' : '已拒绝' }}
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
        <el-form-item label="原日期" prop="originalDate">
          <el-date-picker v-model="formData.originalDate" type="date" placeholder="选择日期" style="width: 100%" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="目标日期" prop="targetDate">
          <el-date-picker v-model="formData.targetDate" type="date" placeholder="选择日期" style="width: 100%" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="调班原因" prop="reason">
          <el-input v-model="formData.reason" type="textarea" rows="3" placeholder="请输入调班原因"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="0">待审核</el-radio>
            <el-radio :label="1">已同意</el-radio>
            <el-radio :label="2">已拒绝</el-radio>
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
import { getCoachShiftPage, addCoachShift, updateCoachShift, deleteCoachShift } from '@/api/coachShift'

export default {
  name: 'CoachShift',
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
        shiftId: null,
        coachId: null,
        originalDate: '',
        targetDate: '',
        reason: '',
        status: 0
      },
      rules: {
        coachId: [{ required: true, message: '请输入教练ID', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getCoachShiftPage({
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
      this.dialogTitle = '新增调班'
      this.formData = {
        shiftId: null,
        coachId: null,
        originalDate: '',
        targetDate: '',
        reason: '',
        status: 0
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑调班'
      this.formData = { ...row }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定要删除该调班吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteCoachShift(row.shiftId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    handleSubmit() {
      this.$refs.dataForm.validate(valid => {
        if (valid) {
          const api = this.formData.shiftId ? updateCoachShift : addCoachShift
          api(this.formData).then(() => {
            this.$message.success(this.formData.shiftId ? '更新成功' : '添加成功')
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
    }
  }
}
</script>

<style lang="scss" scoped>
.shift-container {
  .toolbar, .table-toolbar { margin-bottom: 20px; }
  .table-wrapper { width: 100%; overflow-x: auto; }
  .pagination { margin-top: 20px; text-align: right; }
}
</style>
