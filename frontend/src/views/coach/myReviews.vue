<template>
  <div class="coach-reviews-container">
    <el-card>
      <div class="header">
        <h2>课程评价</h2>
        <span class="sub-title">以下评价来自上过您课程的会员</span>
      </div>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="reviewId" label="评价ID" width="80"></el-table-column>
        <el-table-column prop="memberName" label="会员" width="120">
          <template slot-scope="scope">{{ scope.row.memberName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程" width="140">
          <template slot-scope="scope">{{ scope.row.courseName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="rating" label="评分" width="120" align="center">
          <template slot-scope="scope">
            <el-rate
              v-model="scope.row.rating"
              disabled
              show-score
              text-color="#ff9900"
            ></el-rate>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createTime" label="评价时间" width="170">
          <template slot-scope="scope">{{ formatTime(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="回复状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.reply ? 'success' : 'info'">
              {{ scope.row.reply ? '已回复' : '未回复' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="tableData.length === 0 && !loading" class="empty-tip">
        <i class="el-icon-chat-line-round"></i>
        <p>暂无课程评价</p>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getCoachReviews } from '@/api/coachDashboard'

export default {
  name: 'CoachMyReviews',
  data() {
    return {
      tableData: [],
      loading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      // 不传 coachId，后端 JWT 会自动关联到当前教练
      getCoachReviews(null).then(res => {
        this.loading = false
        if (res.code === 200 || res.code === 0) {
          this.tableData = res.data || []
        } else {
          this.$message.error(res.message || res.msg || '获取评价列表失败')
        }
      }).catch(err => {
        this.loading = false
        this.$message.error((err && err.message) ? err.message : '获取评价列表失败')
      })
    },
    formatTime(val) {
      if (!val) return '-'
      if (typeof val === 'string') return val.replace('T', ' ').substring(0, 19)
      return val
    }
  }
}
</script>

<style lang="scss" scoped>
.coach-reviews-container {
  .header {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 12px;
    h2 { margin: 0; }
    .sub-title { font-size: 13px; color: #909399; font-weight: normal; }
  }
  .empty-tip {
    text-align: center;
    padding: 40px 0;
    color: #909399;
    i { font-size: 48px; display: block; margin-bottom: 12px; }
    p { margin: 0; }
  }
}
</style>
