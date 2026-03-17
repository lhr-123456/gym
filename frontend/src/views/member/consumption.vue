<template>
  <div class="member-consumption-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="会员ID">
            <el-input v-model="queryForm.memberId" placeholder="请输入会员ID" clearable></el-input>
          </el-form-item>
          <el-form-item label="消费类型">
            <el-select v-model="queryForm.consumptionType" placeholder="请选择" clearable>
              <el-option label="购物" value="shopping"></el-option>
              <el-option label="课程" value="course"></el-option>
              <el-option label="私教" value="personal"></el-option>
              <el-option label="其他" value="other"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
            <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
            <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增消费</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="consumptionId" label="ID" width="80"></el-table-column>
        <el-table-column prop="orderNo" label="订单号" width="180"></el-table-column>
        <el-table-column prop="memberId" label="会员ID" width="100"></el-table-column>
        <el-table-column prop="consumptionTypeName" label="消费类型" width="100"></el-table-column>
        <el-table-column prop="amount" label="消费金额" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="actualAmount" label="实收金额" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.actualAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="discountAmount" label="优惠金额" width="100">
          <template slot-scope="scope">
            -¥{{ scope.row.discountAmount || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="pointsEarned" label="获得积分" width="80"></el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="100"></el-table-column>
        <el-table-column prop="consumptionTime" label="消费时间" width="160"></el-table-column>
        <el-table-column label="操作" fixed="right" width="100">
          <template slot-scope="scope">
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

      <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="会员ID" prop="memberId">
            <el-input-number v-model="form.memberId" :min="1" style="width: 100%"></el-input-number>
          </el-form-item>
          <el-form-item label="消费类型" prop="consumptionType">
            <el-select v-model="form.consumptionType" placeholder="请选择" style="width: 100%">
              <el-option label="购物" value="shopping"></el-option>
              <el-option label="课程" value="course"></el-option>
              <el-option label="私教" value="personal"></el-option>
              <el-option label="其他" value="other"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="消费金额" prop="amount">
            <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%"></el-input-number>
          </el-form-item>
          <el-form-item label="实收金额" prop="actualAmount">
            <el-input-number v-model="form.actualAmount" :min="0" :precision="2" style="width: 100%"></el-input-number>
          </el-form-item>
          <el-form-item label="优惠金额" prop="discountAmount">
            <el-input-number v-model="form.discountAmount" :min="0" :precision="2" style="width: 100%"></el-input-number>
          </el-form-item>
          <el-form-item label="获得积分" prop="pointsEarned">
            <el-input-number v-model="form.pointsEarned" :min="0"></el-input-number>
          </el-form-item>
          <el-form-item label="支付方式" prop="paymentMethod">
            <el-select v-model="form.paymentMethod" placeholder="请选择" style="width: 100%">
              <el-option label="现金" value="现金"></el-option>
              <el-option label="微信" value="微信支付"></el-option>
              <el-option label="支付宝" value="支付宝"></el-option>
              <el-option label="银行卡" value="银行卡"></el-option>
            </el-select>
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
    </el-card>
  </div>
</template>

<script>
import { getMemberConsumptionPage, addMemberConsumption, deleteMemberConsumption } from '@/api/memberConsumption'

export default {
  name: 'MemberConsumption',
  data() {
    return {
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      queryForm: {
        memberId: null,
        consumptionType: ''
      },
      dialogVisible: false,
      dialogTitle: '',
      form: {
        consumptionId: null,
        memberId: null,
        consumptionType: '',
        consumptionTypeName: '',
        amount: 0,
        actualAmount: 0,
        discountAmount: 0,
        pointsEarned: 0,
        paymentMethod: '',
        remarks: ''
      },
      rules: {
        memberId: [{ required: true, message: '请输入会员ID', trigger: 'blur' }],
        consumptionType: [{ required: true, message: '请选择消费类型', trigger: 'change' }],
        amount: [{ required: true, message: '请输入消费金额', trigger: 'blur' }]
      }
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
      getMemberConsumptionPage(params).then(res => {
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
        consumptionType: ''
      }
      this.handleQuery()
    },
    handleAdd() {
      this.form = {
        consumptionId: null,
        memberId: null,
        consumptionType: '',
        consumptionTypeName: '',
        amount: 0,
        actualAmount: 0,
        discountAmount: 0,
        pointsEarned: 0,
        paymentMethod: '',
        remarks: ''
      }
      this.dialogTitle = '新增消费记录'
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定删除该消费记录吗？', '提示', {
        type: 'warning'
      }).then(() => {
        deleteMemberConsumption(row.consumptionId).then(res => {
          if (res.code === 200) {
            this.$message.success('删除成功')
            this.fetchData()
          }
        })
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          const typeMap = {
            'shopping': '购物',
            'course': '课程',
            'personal': '私教',
            'other': '其他'
          }
          this.form.consumptionTypeName = typeMap[this.form.consumptionType] || this.form.consumptionType

          addMemberConsumption(this.form).then(res => {
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
    }
  }
}
</script>

<style lang="scss" scoped>
.member-consumption-container {
  .toolbar {
    margin-bottom: 20px;
  }
  .pagination {
    margin-top: 20px;
    text-align: right;
  }
}
</style>
