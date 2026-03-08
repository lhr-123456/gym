<template>
  <div class="member-list-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="会员姓名">
            <el-input v-model="queryForm.memberName" placeholder="请输入会员姓名" clearable></el-input>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="queryForm.phoneNum" placeholder="请输入手机号" clearable></el-input>
          </el-form-item>
          <el-form-item label="健身水平">
            <el-select v-model="queryForm.fitnessLevel" placeholder="请选择" clearable>
              <el-option label="初级" value="初级"></el-option>
              <el-option label="中级" value="中级"></el-option>
              <el-option label="高级" value="高级"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增会员</el-button>
      </div>

      <div class="table-wrapper">
        <el-table :data="tableData" border :style="{ width: '100%' }" v-loading="loading">
        <el-table-column prop="memberId" label="会员 ID" width="80"></el-table-column>
        <el-table-column prop="memberName" label="会员姓名" width="100"></el-table-column>
        <el-table-column prop="gender" label="性别" width="60">
          <template slot-scope="scope">
            <span>{{ scope.row.gender === 'M' ? '男' : '女' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="phoneNum" label="手机号" width="120"></el-table-column>
        <el-table-column prop="emailAddr" label="邮箱" width="180"></el-table-column>
        <el-table-column prop="fitnessLevel" label="健身水平" width="80"></el-table-column>
        <el-table-column prop="memberLevel" label="会员等级" width="80">
          <template slot-scope="scope">
            <span>{{ getMemberLevelText(scope.row.memberLevel) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="80"></el-table-column>
        <el-table-column prop="balance" label="余额" width="80"></el-table-column>
        <el-table-column prop="accountStatus" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.accountStatus === 0 ? 'success' : 'danger'">
              {{ scope.row.accountStatus === 0 ? '正常' : '冻结' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
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
        <el-form-item label="会员姓名" prop="memberName">
          <el-input v-model="formData.memberName" placeholder="请输入会员姓名"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender">
            <el-radio label="M">男</el-radio>
            <el-radio label="F">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker v-model="formData.birthDate" type="date" placeholder="选择日期" style="width: 100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="手机号" prop="phoneNum">
          <el-input v-model="formData.phoneNum" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="emailAddr">
          <el-input v-model="formData.emailAddr" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="健身水平" prop="fitnessLevel">
          <el-select v-model="formData.fitnessLevel" placeholder="请选择" style="width: 100%">
            <el-option label="初级" value="初级"></el-option>
            <el-option label="中级" value="中级"></el-option>
            <el-option label="高级" value="高级"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="会员等级" prop="memberLevel">
          <el-select v-model="formData.memberLevel" placeholder="请选择" style="width: 100%">
            <el-option label="普通会员" :value="1"></el-option>
            <el-option label="银卡会员" :value="2"></el-option>
            <el-option label="金卡会员" :value="3"></el-option>
            <el-option label="钻石会员" :value="4"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="账户状态" prop="accountStatus">
          <el-radio-group v-model="formData.accountStatus">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">冻结</el-radio>
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
import { getMemberPage, addMember, updateMember, deleteMember } from '@/api/member'

export default {
  name: 'MemberList',
  data() {
    return {
      queryForm: {
        memberName: '',
        phoneNum: '',
        fitnessLevel: ''
      },
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '',
      formData: {
        memberId: null,
        memberName: '',
        gender: 'M',
        birthDate: '',
        phoneNum: '',
        emailAddr: '',
        fitnessLevel: '初级',
        memberLevel: 1,
        accountStatus: 0
      },
      rules: {
        memberName: [{ required: true, message: '请输入会员姓名', trigger: 'blur' }],
        phoneNum: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getMemberPage({
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
        memberName: '',
        phoneNum: '',
        fitnessLevel: ''
      }
      this.handleQuery()
    },
    handleAdd() {
      this.dialogTitle = '新增会员'
      this.formData = {
        memberId: null,
        memberName: '',
        gender: 'M',
        birthDate: '',
        phoneNum: '',
        emailAddr: '',
        fitnessLevel: '初级',
        memberLevel: 1,
        accountStatus: 0
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑会员'
      this.formData = { ...row }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该会员吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMember(row.memberId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    handleSubmit() {
      this.$refs.dataForm.validate(valid => {
        if (valid) {
          const api = this.formData.memberId ? updateMember : addMember
          api(this.formData).then(() => {
            this.$message.success(this.formData.memberId ? '更新成功' : '添加成功')
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
    getMemberLevelText(level) {
      const map = { 1: '普通会员', 2: '银卡会员', 3: '金卡会员', 4: '钻石会员' }
      return map[level] || '普通会员'
    }
  }
}
</script>

<style lang="scss" scoped>
.member-list-container {
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
    min-width: 1000px;
  }

  .pagination {
    margin-top: 20px;
    text-align: right;
  }
}
</style>
