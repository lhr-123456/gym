<template>
  <div class="member-card-container">
    <el-card>
      <!-- 工具栏 -->
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="会员ID">
            <el-input v-model="queryForm.memberId" placeholder="请输入会员ID" clearable></el-input>
          </el-form-item>
          <el-form-item label="卡类型">
            <el-select v-model="queryForm.cardType" placeholder="请选择" clearable>
              <el-option label="次卡" value="times"></el-option>
              <el-option label="月卡" value="monthly"></el-option>
              <el-option label="季卡" value="quarterly"></el-option>
              <el-option label="年卡" value="yearly"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryForm.status" placeholder="请选择" clearable>
              <el-option label="正常" :value="1"></el-option>
              <el-option label="挂失" :value="2"></el-option>
              <el-option label="已补办" :value="3"></el-option>
              <el-option label="已过期" :value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
            <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
            <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增会员卡</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="cardId" label="卡ID" width="80"></el-table-column>
        <el-table-column prop="cardNo" label="卡号" width="180"></el-table-column>
        <el-table-column prop="memberId" label="会员ID" width="100"></el-table-column>
        <el-table-column prop="cardTypeName" label="卡类型" width="100"></el-table-column>
        <el-table-column prop="totalTimes" label="总次数" width="80"></el-table-column>
        <el-table-column prop="usedTimes" label="已用次数" width="80"></el-table-column>
        <el-table-column prop="remainingTimes" label="剩余次数" width="80"></el-table-column>
        <el-table-column prop="balance" label="余额" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.balance || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120"></el-table-column>
        <el-table-column prop="endDate" label="到期日期" width="120"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="warning" @click="handleRenew(scope.row)" v-if="scope.row.status === 1">续费</el-button>
            <el-button size="mini" type="info" @click="handleReportLoss(scope.row)" v-if="scope.row.status === 1">挂失</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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

      <!-- 新增/编辑对话框 -->
      <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="会员ID" prop="memberId">
            <el-input-number v-model="form.memberId" :min="1"></el-input-number>
          </el-form-item>
          <el-form-item label="卡类型" prop="cardType">
            <el-select v-model="form.cardType" placeholder="请选择卡类型" style="width: 100%">
              <el-option label="次卡（10次）" value="times_10"></el-option>
              <el-option label="次卡（30次）" value="times_30"></el-option>
              <el-option label="次卡（50次）" value="times_50"></el-option>
              <el-option label="月卡" value="monthly"></el-option>
              <el-option label="季卡" value="quarterly"></el-option>
              <el-option label="年卡" value="yearly"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="充值金额" prop="balance">
            <el-input-number v-model="form.balance" :min="0" :precision="2"></el-input-number>
          </el-form-item>
          <el-form-item label="充值次数" prop="totalTimes">
            <el-input-number v-model="form.totalTimes" :min="0"></el-input-number>
          </el-form-item>
          <el-form-item label="开始日期" prop="startDate">
            <el-date-picker v-model="form.startDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" style="width: 100%"></el-date-picker>
          </el-form-item>
          <el-form-item label="到期日期" prop="endDate">
            <el-date-picker v-model="form.endDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" style="width: 100%"></el-date-picker>
          </el-form-item>
          <el-form-item label="备注" prop="remarks">
            <el-input v-model="form.remarks" type="textarea" :rows="2"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </el-dialog>

      <!-- 续费对话框 -->
      <el-dialog title="续费/充值" :visible.sync="renewDialogVisible" width="500px">
        <el-form :model="renewForm" label-width="100px">
          <el-form-item label="充值金额">
            <el-input-number v-model="renewForm.balance" :min="0" :precision="2"></el-input-number>
          </el-form-item>
          <el-form-item label="充值次数">
            <el-input-number v-model="renewForm.totalTimes" :min="0"></el-input-number>
          </el-form-item>
          <el-form-item label="延期至">
            <el-date-picker v-model="renewForm.endDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" style="width: 100%"></el-date-picker>
          </el-form-item>
        </el-form>
        <div slot="footer">
          <el-button @click="renewDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRenew">确定续费</el-button>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { getMemberCardPage, addMemberCard, updateMemberCard, deleteMemberCard, renewMemberCard, reportLossMemberCard } from '@/api/memberCard'

export default {
  name: 'MemberCard',
  data() {
    return {
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      queryForm: {
        memberId: null,
        cardType: '',
        status: null
      },
      dialogVisible: false,
      dialogTitle: '',
      form: {
        cardId: null,
        memberId: null,
        cardType: '',
        cardTypeName: '',
        balance: 0,
        totalTimes: 0,
        startDate: '',
        endDate: '',
        remarks: ''
      },
      rules: {
        memberId: [{ required: true, message: '请输入会员ID', trigger: 'blur' }],
        cardType: [{ required: true, message: '请选择卡类型', trigger: 'change' }]
      },
      renewDialogVisible: false,
      renewForm: {
        balance: 0,
        totalTimes: 0,
        endDate: ''
      },
      currentCardId: null
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      this.loading = true
      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        ...this.queryForm
      }
      getMemberCardPage(params).then(res => {
        if (res.code === 200) {
          this.tableData = res.data.records || []
          this.total = res.data.total || 0
        }
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.pageNum = 1
      this.fetchData()
    },
    handleReset() {
      this.queryForm = {
        memberId: null,
        cardType: '',
        status: null
      }
      this.handleQuery()
    },
    handleAdd() {
      this.form = {
        cardId: null,
        memberId: null,
        cardType: '',
        cardTypeName: '',
        balance: 0,
        totalTimes: 0,
        startDate: '',
        endDate: '',
        remarks: ''
      }
      this.dialogTitle = '新增会员卡'
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.form = { ...row }
      this.dialogTitle = '编辑会员卡'
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定要删除该会员卡吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMemberCard(row.cardId).then(res => {
          if (res.code === 200) {
            this.$message.success('删除成功')
            this.fetchData()
          }
        })
      })
    },
    handleRenew(row) {
      this.currentCardId = row.cardId
      this.renewForm = {
        balance: 0,
        totalTimes: 0,
        endDate: ''
      }
      this.renewDialogVisible = true
    },
    submitRenew() {
      renewMemberCard(this.currentCardId, this.renewForm).then(res => {
        if (res.code === 200) {
          this.$message.success('续费成功')
          this.renewDialogVisible = false
          this.fetchData()
        }
      })
    },
    handleReportLoss(row) {
      this.$confirm('确定要挂失该会员卡吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        reportLossMemberCard(row.cardId).then(res => {
          if (res.code === 200) {
            this.$message.success('挂失成功')
            this.fetchData()
          }
        })
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          // 设置卡类型名称
          const typeMap = {
            'times_10': '次卡（10次）',
            'times_30': '次卡（30次）',
            'times_50': '次卡（50次）',
            'monthly': '月卡',
            'quarterly': '季卡',
            'yearly': '年卡'
          }
          this.form.cardTypeName = typeMap[this.form.cardType] || this.form.cardType

          const action = this.form.cardId ? updateMemberCard : addMemberCard
          action(this.form).then(res => {
            if (res.code === 200) {
              this.$message.success('操作成功')
              this.dialogVisible = false
              this.fetchData()
            }
          })
        }
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchData()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.fetchData()
    },
    getStatusType(status) {
      const map = {
        0: 'info',
        1: 'success',
        2: 'warning',
        3: 'danger'
      }
      return map[status] || 'info'
    },
    getStatusText(status) {
      const map = {
        0: '已过期',
        1: '正常',
        2: '挂失',
        3: '已补办'
      }
      return map[status] || '未知'
    }
  }
}
</script>

<style lang="scss" scoped>
.member-card-container {
  .toolbar {
    margin-bottom: 20px;
  }

  .pagination {
    margin-top: 20px;
    text-align: right;
  }
}
</style>
