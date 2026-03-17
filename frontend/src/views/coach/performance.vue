<template>
  <div class="performance-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="教练">
            <el-select v-model="queryForm.coachId" placeholder="请选择教练" clearable>
              <el-option v-for="coach in coachList" :key="coach.coachId" :label="coach.coachName" :value="coach.coachId"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="评估月份">
            <el-date-picker v-model="queryForm.evalMonth" type="month" placeholder="选择月份" clearable></el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增绩效</el-button>
      </div>

      <div class="table-wrapper">
        <el-table :data="tableData" border v-loading="loading">
          <el-table-column prop="perfId" label="ID" width="80"></el-table-column>
          <el-table-column prop="coachId" label="教练ID" width="80"></el-table-column>
          <el-table-column prop="evalMonth" label="评估月份" width="120"></el-table-column>
          <el-table-column prop="attendanceScore" label="出勤评分" width="100"></el-table-column>
          <el-table-column prop="teachingScore" label="教学评分" width="100"></el-table-column>
          <el-table-column prop="serviceScore" label="服务评分" width="100"></el-table-column>
          <el-table-column prop="salesScore" label="销售评分" width="100"></el-table-column>
          <el-table-column prop="totalScore" label="总分" width="80"></el-table-column>
          <el-table-column prop="evalLevel" label="评级" width="80">
            <template slot-scope="scope">
              <el-tag :type="scope.row.evalLevel === 'A' ? 'success' : scope.row.evalLevel === 'B' ? 'warning' : 'danger'">
                {{ scope.row.evalLevel }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="evalRemarks" label="备注"></el-table-column>
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

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="700px">
      <el-form ref="dataForm" :model="formData" :rules="rules" label-width="110px">
        <el-form-item label="教练" prop="coachId">
          <el-select v-model="formData.coachId" placeholder="请选择教练" style="width: 100%">
            <el-option v-for="coach in coachList" :key="coach.coachId" :label="coach.coachName" :value="coach.coachId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="评估月份" prop="evalMonth">
          <el-date-picker v-model="formData.evalMonth" type="month" placeholder="选择月份" style="width: 100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="出勤评分" prop="attendanceScore">
          <el-input-number v-model="formData.attendanceScore" :min="0" :max="100" :step="0.5" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="教学评分" prop="teachingScore">
          <el-input-number v-model="formData.teachingScore" :min="0" :max="100" :step="0.5" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="服务评分" prop="serviceScore">
          <el-input-number v-model="formData.serviceScore" :min="0" :max="100" :step="0.5" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="销售评分" prop="salesScore">
          <el-input-number v-model="formData.salesScore" :min="0" :max="100" :step="0.5" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="学员反馈评分" prop="studentFeedback">
          <el-input-number v-model="formData.studentFeedback" :min="0" :max="100" :step="0.5" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="评级" prop="evalLevel">
          <el-select v-model="formData.evalLevel" placeholder="请选择评级" style="width: 100%">
            <el-option label="A级" value="A"></el-option>
            <el-option label="B级" value="B"></el-option>
            <el-option label="C级" value="C"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="evalRemarks">
          <el-input v-model="formData.evalRemarks" type="textarea" rows="3" placeholder="请输入备注"></el-input>
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
import { getCoachPerformancePage, addCoachPerformance, updateCoachPerformance, deleteCoachPerformance } from '@/api/coachPerformance'
import { getCoachList } from '@/api/coach'

export default {
  name: 'CoachPerformance',
  data() {
    return {
      queryForm: {
        coachId: null,
        evalMonth: ''
      },
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      coachList: [],
      dialogVisible: false,
      dialogTitle: '',
      formData: {
        perfId: null,
        coachId: null,
        evalMonth: '',
        attendanceScore: 0,
        teachingScore: 0,
        serviceScore: 0,
        salesScore: 0,
        studentFeedback: 0,
        totalScore: 0,
        evalLevel: '',
        evalRemarks: ''
      },
      rules: {
        coachId: [{ required: true, message: '请选择教练', trigger: 'change' }],
        evalMonth: [{ required: true, message: '请选择评估月份', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getCoachList()
    this.getList()
  },
  methods: {
    getCoachList() {
      getCoachList().then(response => {
        this.coachList = response.data || []
      })
    },
    getList() {
      this.loading = true
      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }
      if (this.queryForm.coachId) {
        params.coachId = this.queryForm.coachId
      }
      if (this.queryForm.evalMonth) {
        const d = new Date(this.queryForm.evalMonth)
        params.evalMonth = d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0')
      }
      getCoachPerformancePage(params).then(response => {
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
      this.queryForm = { coachId: null, evalMonth: '' }
      this.handleQuery()
    },
    handleAdd() {
      this.dialogTitle = '新增绩效'
      this.formData = {
        perfId: null,
        coachId: null,
        evalMonth: '',
        attendanceScore: 0,
        teachingScore: 0,
        serviceScore: 0,
        salesScore: 0,
        studentFeedback: 0,
        totalScore: 0,
        evalLevel: '',
        evalRemarks: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑绩效'
      this.formData = { ...row }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定要删除该绩效记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteCoachPerformance(row.perfId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    handleSubmit() {
      this.$refs.dataForm.validate(valid => {
        if (valid) {
          // 计算总分
          const total = (Number(this.formData.attendanceScore) + Number(this.formData.teachingScore) + 
                        Number(this.formData.serviceScore) + Number(this.formData.salesScore) + 
                        Number(this.formData.studentFeedback)) / 5
          this.formData.totalScore = Math.round(total * 100) / 100
          
          const data = { ...this.formData }
          if (data.evalMonth) {
            const d = new Date(data.evalMonth)
            data.evalMonth = d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0')
          }
          
          const api = this.formData.perfId ? updateCoachPerformance : addCoachPerformance
          api(data).then(() => {
            this.$message.success(this.formData.perfId ? '更新成功' : '添加成功')
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
.performance-container {
  .toolbar, .table-toolbar { margin-bottom: 20px; }
  .table-wrapper { width: 100%; overflow-x: auto; }
  .pagination { margin-top: 20px; text-align: right; }
}
</style>
