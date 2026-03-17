<template>
  <div class="salary-container">
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
        <el-button type="primary" @click="handleAdd">新增工资</el-button>
      </div>

      <div class="table-wrapper">
        <el-table :data="tableData" border v-loading="loading">
          <el-table-column prop="salaryId" label="工资ID" width="80"></el-table-column>
          <el-table-column prop="coachId" label="教练ID" width="80"></el-table-column>
          <el-table-column prop="salaryMonth" label="工资月份" width="120"></el-table-column>
          <el-table-column prop="baseSalary" label="基本工资" width="100"></el-table-column>
          <el-table-column prop="performanceBonus" label="绩效奖金" width="100"></el-table-column>
          <el-table-column prop="courseBonus" label="课时费" width="100"></el-table-column>
          <el-table-column prop="totalSalary" label="总工资" width="100"></el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 0 ? 'warning' : scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 0 ? '待发放' : scope.row.status === 1 ? '已发放' : '已扣款' }}
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
        <el-form-item label="工资月份" prop="salaryMonth">
          <el-date-picker v-model="formData.salaryMonth" type="month" placeholder="选择月份" style="width: 100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="基本工资" prop="baseSalary">
          <el-input v-model.number="formData.baseSalary" placeholder="请输入基本工资"></el-input>
        </el-form-item>
        <el-form-item label="绩效奖金" prop="performanceBonus">
          <el-input v-model.number="formData.performanceBonus" placeholder="请输入绩效奖金"></el-input>
        </el-form-item>
        <el-form-item label="课时费" prop="courseBonus">
          <el-input v-model.number="formData.courseBonus" placeholder="请输入课时费"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="0">待发放</el-radio>
            <el-radio :label="1">已发放</el-radio>
            <el-radio :label="2">已扣款</el-radio>
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
import { getCoachSalaryPage, addCoachSalary, updateCoachSalary, deleteCoachSalary } from '@/api/coachSalary'

export default {
  name: 'CoachSalary',
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
        salaryId: null,
        coachId: null,
        salaryMonth: '',
        baseSalary: 0,
        performanceBonus: 0,
        courseBonus: 0,
        totalSalary: 0,
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
      getCoachSalaryPage({
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
      this.dialogTitle = '新增工资'
      this.formData = {
        salaryId: null,
        coachId: null,
        salaryMonth: '',
        baseSalary: 0,
        performanceBonus: 0,
        courseBonus: 0,
        totalSalary: 0,
        status: 0
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑工资'
      this.formData = { ...row }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定要删除该工资记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteCoachSalary(row.salaryId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    handleSubmit() {
      this.$refs.dataForm.validate(valid => {
        if (valid) {
          this.formData.totalSalary = this.formData.baseSalary + this.formData.performanceBonus + this.formData.courseBonus
          const api = this.formData.salaryId ? updateCoachSalary : addCoachSalary
          api(this.formData).then(() => {
            this.$message.success(this.formData.salaryId ? '更新成功' : '添加成功')
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
.salary-container {
  .toolbar, .table-toolbar { margin-bottom: 20px; }
  .table-wrapper { width: 100%; overflow-x: auto; }
  .pagination { margin-top: 20px; text-align: right; }
}
</style>
