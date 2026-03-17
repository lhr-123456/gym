<template>
  <div class="certificate-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="证书名称">
            <el-input v-model="queryForm.certName" placeholder="请输入证书名称" clearable></el-input>
          </el-form-item>
          <el-form-item label="证书类型">
            <el-select v-model="queryForm.certType" placeholder="请选择" clearable>
              <el-option label="资质证书" value="资质证书"></el-option>
              <el-option label="技能证书" value="技能证书"></el-option>
              <el-option label="专业证书" value="专业证书"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增证书</el-button>
      </div>

      <div class="table-wrapper">
        <el-table :data="tableData" border v-loading="loading">
          <el-table-column prop="certId" label="证书ID" width="80"></el-table-column>
          <el-table-column prop="coachId" label="教练ID" width="80"></el-table-column>
          <el-table-column prop="certName" label="证书名称" width="150"></el-table-column>
          <el-table-column prop="certType" label="证书类型" width="100"></el-table-column>
          <el-table-column prop="certNo" label="证书编号" width="150"></el-table-column>
          <el-table-column prop="issueOrg" label="发证机构" width="150"></el-table-column>
          <el-table-column prop="issueDate" label="发证日期" width="120"></el-table-column>
          <el-table-column prop="expireDate" label="过期日期" width="120"></el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 0 ? 'success' : scope.row.status === 1 ? 'warning' : 'danger'">
                {{ scope.row.status === 0 ? '有效' : scope.row.status === 1 ? '待审核' : '无效' }}
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
        <el-form-item label="证书名称" prop="certName">
          <el-input v-model="formData.certName" placeholder="请输入证书名称"></el-input>
        </el-form-item>
        <el-form-item label="证书类型" prop="certType">
          <el-select v-model="formData.certType" placeholder="请选择" style="width: 100%">
            <el-option label="资质证书" value="资质证书"></el-option>
            <el-option label="技能证书" value="技能证书"></el-option>
            <el-option label="专业证书" value="专业证书"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="证书编号" prop="certNo">
          <el-input v-model="formData.certNo" placeholder="请输入证书编号"></el-input>
        </el-form-item>
        <el-form-item label="发证机构" prop="issueOrg">
          <el-input v-model="formData.issueOrg" placeholder="请输入发证机构"></el-input>
        </el-form-item>
        <el-form-item label="发证日期" prop="issueDate">
          <el-date-picker v-model="formData.issueDate" type="date" placeholder="选择日期" style="width: 100%" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="过期日期" prop="expireDate">
          <el-date-picker v-model="formData.expireDate" type="date" placeholder="选择日期" style="width: 100%" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="0">有效</el-radio>
            <el-radio :label="1">待审核</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="formData.remarks" type="textarea" rows="3" placeholder="请输入备注"></el-input>
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
import { getCoachCertificatePage, addCoachCertificate, updateCoachCertificate, deleteCoachCertificate } from '@/api/coachCertificate'

export default {
  name: 'CoachCertificate',
  data() {
    return {
      queryForm: {
        certName: '',
        certType: ''
      },
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '',
      formData: {
        certId: null,
        coachId: null,
        certName: '',
        certType: '',
        certNo: '',
        issueOrg: '',
        issueDate: '',
        expireDate: '',
        status: 0,
        remarks: ''
      },
      rules: {
        coachId: [{ required: true, message: '请输入教练ID', trigger: 'blur' }],
        certName: [{ required: true, message: '请输入证书名称', trigger: 'blur' }],
        certType: [{ required: true, message: '请选择证书类型', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getCoachCertificatePage({
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
      this.queryForm = { certName: '', certType: '' }
      this.handleQuery()
    },
    handleAdd() {
      this.dialogTitle = '新增证书'
      this.formData = {
        certId: null,
        coachId: null,
        certName: '',
        certType: '',
        certNo: '',
        issueOrg: '',
        issueDate: '',
        expireDate: '',
        status: 0,
        remarks: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑证书'
      this.formData = { ...row }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定要删除该证书吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteCoachCertificate(row.certId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    handleSubmit() {
      this.$refs.dataForm.validate(valid => {
        if (valid) {
          const api = this.formData.certId ? updateCoachCertificate : addCoachCertificate
          api(this.formData).then(() => {
            this.$message.success(this.formData.certId ? '更新成功' : '添加成功')
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
.certificate-container {
  .toolbar, .table-toolbar {
    margin-bottom: 20px;
  }
  .table-wrapper {
    width: 100%;
    overflow-x: auto;
  }
  .pagination {
    margin-top: 20px;
    text-align: right;
  }
}
</style>
