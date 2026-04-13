<template>
  <div class="member-homework-container">
    <el-card>
      <div slot="header" class="card-header">
        <span><i class="el-icon-document-copy"></i> 我的作业</span>
        <el-tag :type="pendingCount > 0 ? 'warning' : 'success'" size="small">
          待完成 {{ pendingCount }} 项
        </el-tag>
      </div>

      <!-- 待完成作业 -->
      <div v-if="pendingList.length > 0" class="section-title">待完成</div>
      <div v-if="pendingList.length > 0" class="homework-list">
        <div v-for="item in pendingList" :key="item.id" class="homework-card pending">
          <div class="homework-header">
            <span class="homework-title">{{ item.title }}</span>
            <el-tag type="warning" size="mini">待完成</el-tag>
          </div>
          <div class="homework-content">{{ item.content }}</div>
          <div class="homework-meta">
            <span v-if="item.targetDate"><i class="el-icon-date"></i> 目标：{{ item.targetDate }}</span>
            <span v-if="item.coachRemark"><i class="el-icon-chat-dot-round"></i> 教练备注：{{ item.coachRemark }}</span>
          </div>
          <div class="homework-actions">
            <el-button type="primary" size="small" icon="el-icon-check" @click="handleComplete(item)">打卡完成</el-button>
          </div>
        </div>
      </div>

      <!-- 历史作业 -->
      <div class="section-title" style="margin-top: 24px;">历史记录</div>
      <el-table :data="historyList" border v-loading="loading">
        <el-table-column prop="title" label="作业标题" min-width="160"></el-table-column>
        <el-table-column prop="content" label="作业内容" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="targetDate" label="目标日期" width="110"></el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'" size="mini">
              {{ scope.row.status === 1 ? '已完成' : '未完成' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="completeTime" label="完成时间" width="160"></el-table-column>
        <el-table-column prop="coachRemark" label="教练备注" width="160" show-overflow-tooltip></el-table-column>
      </el-table>
      <div v-if="historyList.length === 0 && !loading" class="empty-tip">
        <i class="el-icon-document-copy"></i>
        <p>暂无历史作业记录</p>
      </div>
    </el-card>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getHomeworkList, completeHomework } from '@/api/memberHomework'

export default {
  name: 'MemberHomework',
  data() {
    return {
      allList: [],
      loading: false
    }
  },
  computed: {
    ...mapGetters(['userId', 'memberId']),
    currentMemberId() {
      return this.memberId || this.userId
    },
    pendingList() {
      return this.allList.filter(i => i.status === 0)
    },
    historyList() {
      return this.allList.filter(i => i.status === 1)
    },
    pendingCount() {
      return this.pendingList.length
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    fetchList() {
      if (!this.currentMemberId) return
      this.loading = true
      getHomeworkList(this.currentMemberId).then(res => {
        if (res && (res.code === 200 || res.code === '200')) {
          this.allList = res.data || []
        }
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleComplete(item) {
      this.$confirm(`确认完成作业「${item.title}」吗？`, '打卡确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        completeHomework(item.id, this.currentMemberId).then(res => {
          if (res && (res.code === 200 || res.code === '200')) {
            this.$message.success('打卡成功！继续加油！')
            this.fetchList()
          } else {
            this.$message.error(res.message || '打卡失败')
          }
        })
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.member-homework-container {
  ::v-deep .el-card {
    background: rgba(255, 255, 255, 0.18) !important;
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.4) !important;
    box-shadow: 0 8px 32px rgba(30, 58, 138, 0.1);
    ::v-deep .el-card__header {
      background: rgba(255, 255, 255, 0.1) !important;
      border-bottom: 1px solid rgba(255, 255, 255, 0.25) !important;
    }
    ::v-deep .el-card__body { background: transparent !important; }
  }
  .card-header {
    font-size: 16px; font-weight: bold;
    display: flex; justify-content: space-between; align-items: center;
    color: #1e3a8a;
    i { margin-right: 8px; color: #2563eb; }
  }
  .section-title {
    font-size: 15px; font-weight: 700; color: #1e3a8a; margin-bottom: 14px;
  }
  .homework-list {
    display: flex; flex-direction: column; gap: 12px;
  }
  .homework-card {
    padding: 16px;
    background: rgba(255, 255, 255, 0.22) !important;
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.4) !important;
    border-left: 4px solid #2563eb;
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(30, 58, 138, 0.08);
    transition: all 0.3s;
    &:hover { transform: translateY(-2px); box-shadow: 0 10px 28px rgba(30, 58, 138, 0.15); }
    &.pending { border-left-color: #2563eb; }
  }
  .homework-header {
    display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;
    .homework-title { font-size: 15px; font-weight: 700; color: #1e3a8a; }
  }
  .homework-content { font-size: 14px; color: #3b82f6; margin-bottom: 10px; line-height: 1.6; font-weight: 500; }
  .homework-meta {
    font-size: 12px; color: #60a5fa; margin-bottom: 12px; font-weight: 500;
    span { margin-right: 16px; i { margin-right: 4px; color: #2563eb; } }
  }
  .homework-actions { display: flex; gap: 8px; }
  .empty-tip {
    text-align: center; padding: 40px; color: #2563eb;
    i { font-size: 40px; display: block; margin-bottom: 10px; color: #2563eb; }
    p { margin: 0; color: #3b82f6; }
  }
}
</style>
