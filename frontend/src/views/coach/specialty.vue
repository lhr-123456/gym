<template>
  <div class="specialty-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="专长名称">
            <el-input v-model="queryForm.specialtyName" placeholder="请输入专长名称" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增专长</el-button>
      </div>

      <div class="table-wrapper">
        <el-table :data="tableData" border v-loading="loading">
          <el-table-column prop="specialtyId" label="专长ID" width="80"></el-table-column>
          <el-table-column prop="coachId" label="教练ID" width="80"></el-table-column>
          <el-table-column prop="specialtyName" label="专长名称" width="150"></el-table-column>
          <el-table-column prop="specialtyDesc" label="专长描述"></el-table-column>
          <el-table-column prop="level" label="等级" width="100">
            <template slot-scope="scope">
              <el-tag>{{ scope.row.level === 1 ? '初级' : scope.row.level === 2 ? '中级' : '高级' }}</el-tag>
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
        <el-form-item label="专长名称" prop="specialtyName">
          <el-input v-model="formData.specialtyName" placeholder="请输入专长名称"></el-input>
        </el-form-item>
        <el-form-item label="专长描述" prop="specialtyDesc">
          <el-input v-model="formData.specialtyDesc" type="textarea" rows="3" placeholder="请输入专长描述"></el-input>
        </el-form-item>
        <el-form-item label="等级" prop="level">
          <el-select v-model="formData.level" placeholder="请选择" style="width: 100%">
            <el-option label="初级" :value="1"></el-option>
            <el-option label="中级" :value="2"></el-option>
            <el-option label="高级" :value="3"></el-option>
          </el-select>
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
import { getCoachSpecialtyPage, addCoachSpecialty, updateCoachSpecialty, deleteCoachSpecialty } from '@/api/coachSpecialty'

export default {
  name: 'CoachSpecialty',
  data() {
    return {
      queryForm: {
        specialtyName: ''
      },
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '',
      formData: {
        specialtyId: null,
        coachId: null,
        specialtyName: '',
        specialtyDesc: '',
        level: 1
      },
      rules: {
        coachId: [{ required: true, message: '请输入教练ID', trigger: 'blur' }],
        specialtyName: [{ required: true, message: '请输入专长名称', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getCoachSpecialtyPage({
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
      this.queryForm = { specialtyName: '' }
      this.handleQuery()
    },
    handleAdd() {
      this.dialogTitle = '新增专长'
      this.formData = { specialtyId: null, coachId: null, specialtyName: '', specialtyDesc: '', level: 1 }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑专长'
      this.formData = { ...row }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定要删除该专长吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteCoachSpecialty(row.specialtyId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    handleSubmit() {
      this.$refs.dataForm.validate(valid => {
        if (valid) {
          const api = this.formData.specialtyId ? updateCoachSpecialty : addCoachSpecialty
          api(this.formData).then(() => {
            this.$message.success(this.formData.specialtyId ? '更新成功' : '添加成功')
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
.specialty-container {
  .toolbar, .table-toolbar { margin-bottom: 20px; }
  .table-wrapper { width: 100%; overflow-x: auto; }
  .pagination { margin-top: 20px; text-align: right; }
}
</style>
