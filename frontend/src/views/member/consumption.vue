<template>
  <div class="member-consumption-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="消费类型">
            <el-select v-model="queryForm.type" placeholder="请选择" clearable style="width: 150px">
              <el-option label="全部" value=""></el-option>
              <el-option label="购卡" value="card"></el-option>
              <el-option label="购物" value="shopping"></el-option>
              <el-option label="课程" value="course"></el-option>
              <el-option label="私教" value="personal"></el-option>
              <el-option label="其他" value="other"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
            <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="200"></el-table-column>
        <el-table-column prop="typeName" label="类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === 'card' ? 'success' : 'primary'" size="small">
              {{ scope.row.typeName || scope.row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="itemName" label="项目名称" width="180"></el-table-column>
        <el-table-column prop="amount" label="金额" width="100">
          <template slot-scope="scope">
            <span class="amount">¥{{ scope.row.actualAmount || scope.row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="discountAmount" label="优惠" width="80">
          <template slot-scope="scope">
            <span v-if="scope.row.discountAmount" class="discount">-¥{{ scope.row.discountAmount }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="pointsEarned" label="获得积分" width="80">
          <template slot-scope="scope">
            <span v-if="scope.row.pointsEarned" class="points">+{{ scope.row.pointsEarned }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="100"></el-table-column>
        <el-table-column prop="time" label="时间" width="160">
          <template slot-scope="scope">
            {{ formatTime(scope.row.time) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusName(scope.row.status) }}
            </el-tag>
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
    </el-card>
  </div>
</template>

<script>
import { getMemberPurchases } from '@/api/memberConsumption'

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
        type: ''
      }
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      this.loading = true
      // 从本地存储获取会员ID
      const memberInfo = JSON.parse(localStorage.getItem('memberInfo') || '{}')
      const memberId = memberInfo.memberId

      if (!memberId) {
        this.loading = false
        return
      }

      getMemberPurchases(memberId).then(res => {
        if (res.code === 200) {
          let data = res.data || []
          // 过滤类型
          if (this.queryForm.type) {
            data = data.filter(item => item.type === this.queryForm.type)
          }
          this.total = data.length
          // 分页
          const start = (this.pageNum - 1) * this.pageSize
          const end = start + this.pageSize
          this.tableData = data.slice(start, end)
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
      this.queryForm = { type: '' }
      this.handleQuery()
    },
    formatTime(time) {
      if (!time) return '-'
      if (typeof time === 'string' && time.length <= 10) {
        return time
      }
      const date = new Date(time)
      return date.toLocaleString('zh-CN', { hour12: false })
    },
    getStatusType(status) {
      if (status === 1) return 'success'
      if (status === 0) return 'info'
      if (status === 2) return 'warning'
      return 'info'
    },
    getStatusName(status) {
      if (status === 1) return '正常'
      if (status === 0) return '已取消'
      if (status === 2) return '进行中'
      return '未知'
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
  .amount {
    color: #F56C6C;
    font-weight: bold;
  }
  .discount {
    color: #67C23A;
  }
  .points {
    color: #E6A23C;
  }
}
</style>
