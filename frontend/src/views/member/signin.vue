<template>
  <div class="member-signin-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="会员ID">
            <el-input v-model="queryForm.memberId" placeholder="请输入会员ID" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
            <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="signinId" label="ID" width="80"></el-table-column>
        <el-table-column prop="memberId" label="会员ID" width="100"></el-table-column>
        <el-table-column prop="signinDate" label="签到日期" width="120"></el-table-column>
        <el-table-column prop="signinTime" label="签到时间" width="160"></el-table-column>
        <el-table-column prop="signinType" label="签到类型" width="100"></el-table-column>
        <el-table-column prop="pointsEarned" label="获得积分" width="100"></el-table-column>
        <el-table-column prop="consecutiveDays" label="连续签到" width="100">
          <template slot-scope="scope">
            {{ scope.row.consecutiveDays }}天
          </template>
        </el-table-column>
        <el-table-column prop="remarks" label="备注"></el-table-column>
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
import { getMemberSigninPage } from '@/api/memberSignin'

export default {
  name: 'MemberSignin',
  data() {
    return {
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      queryForm: {
        memberId: null
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
      getMemberSigninPage(params).then(res => {
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
      this.queryForm = { memberId: null }
      this.handleQuery()
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
.member-signin-container {
  .toolbar {
    margin-bottom: 20px;
  }
  .pagination {
    margin-top: 20px;
    text-align: right;
  }
}
</style>
