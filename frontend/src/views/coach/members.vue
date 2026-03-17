<template>
  <div class="coach-members-container">
    <el-card>
      <div class="header">
        <h2>我的学员</h2>
      </div>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="memberId" label="会员ID" width="80"></el-table-column>
        <el-table-column prop="memberName" label="姓名" width="120"></el-table-column>
        <el-table-column prop="gender" label="性别" width="60">
          <template slot-scope="scope">
            {{ scope.row.gender === 'M' ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="phoneNum" label="手机号" width="130"></el-table-column>
        <el-table-column prop="totalCourses" label="累计课程" width="100"></el-table-column>
        <el-table-column prop="completedCourses" label="已完成" width="100"></el-table-column>
        <el-table-column prop="remainingCourses" label="剩余" width="100"></el-table-column>
        <el-table-column prop="lastCourseDate" label="最近上课" width="120"></el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleView(scope.row)">查看详情</el-button>
            <el-button size="mini" type="success" @click="handleBodyTest(scope.row)">体测</el-button>
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
import { mapGetters } from 'vuex'
import { getCoachMembersPage } from '@/api/coachDashboard'

export default {
  name: 'CoachMembers',
  data() {
    return {
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0
    }
  },
  computed: {
    ...mapGetters(['userId'])
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getCoachMembersPage({
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }).then(res => {
        const page = res && res.data ? res.data : {}
        this.tableData = page.records || []
        this.total = page.total || 0
      }).catch(err => {
        this.tableData = []
        this.total = 0
        this.$message.error((err && err.message) ? err.message : '获取学员列表失败')
      }).finally(() => {
        this.loading = false
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
    handleView(row) {
      this.$message.info('查看学员详情功能开发中')
    },
    handleBodyTest(row) {
      this.$router.push({
        path: '/coach-bodytest/add',
        query: { memberId: row.memberId, memberName: row.memberName }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.coach-members-container {
  .header {
    margin-bottom: 20px;
    h2 {
      margin: 0;
    }
  }

  .pagination {
    margin-top: 20px;
    text-align: right;
  }
}
</style>
