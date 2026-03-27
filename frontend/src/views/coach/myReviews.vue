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
        <el-table-column prop="reply" label="教练回复" min-width="160">
          <template slot-scope="scope">
            <span v-if="scope.row.reply" class="reply-text">{{ scope.row.reply }}</span>
            <span v-else class="reply-empty">暂无回复</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="评价时间" width="170">
          <template slot-scope="scope">{{ formatTime(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="回复状态" width="120" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.reply ? 'success' : 'info'">
              {{ scope.row.reply ? '已回复' : '未回复' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="scope">
            <el-button
              size="mini"
              :type="scope.row.reply ? 'default' : 'primary'"
              @click="handleReply(scope.row)"
            >{{ scope.row.reply ? '查看回复' : '回复' }}</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="tableData.length === 0 && !loading" class="empty-tip">
        <i class="el-icon-chat-line-round"></i>
        <p>暂无课程评价</p>
      </div>
    </el-card>

    <!-- 回复对话框 -->
    <el-dialog :title="replyDialogTitle" :visible.sync="replyDialogVisible" width="520px">
      <div v-if="replyTarget && replyTarget.reply" class="reply-history">
        <div class="reply-label">我的回复：</div>
        <div class="reply-content">{{ replyTarget.reply }}</div>
      </div>
      <el-form ref="replyForm" :model="replyForm" label-width="80px" style="margin-top: 12px;">
        <el-form-item label="回复内容">
          <el-input
            v-model="replyForm.reply"
            type="textarea"
            :rows="3"
            :placeholder="replyTarget && replyTarget.reply ? '再次回复...' : '请输入回复内容'"
            maxlength="200"
            show-word-limit
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReply" :loading="submitting">提交回复</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getCoachReviews } from '@/api/coachDashboard'
import { replyReview } from '@/api/courseReview'

export default {
  name: 'CoachMyReviews',
  data() {
    return {
      tableData: [],
      loading: false,
      replyDialogVisible: false,
      replyDialogTitle: '回复评价',
      replyTarget: null,
      submitting: false,
      replyForm: { reply: '' }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
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
    handleReply(row) {
      this.replyTarget = row
      this.replyForm.reply = ''
      this.replyDialogTitle = row.reply ? '查看/补充回复' : '回复评价'
      this.replyDialogVisible = true
    },
    handleSubmitReply() {
      if (!this.replyForm.reply || !this.replyForm.reply.trim()) {
        this.$message.warning('请输入回复内容')
        return
      }
      this.submitting = true
      replyReview(this.replyTarget.reviewId, this.replyForm.reply.trim()).then(res => {
        if (res && (res.code === 200 || res.code === '200')) {
          this.$message.success('回复成功')
          this.replyDialogVisible = false
          this.getList()
        } else {
          this.$message.error(res.message || '回复失败')
        }
        this.submitting = false
      }).catch(() => {
        this.submitting = false
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
  .reply-history {
    background: #f5f7fa;
    border-radius: 6px;
    padding: 12px 16px;
    .reply-label { font-size: 12px; color: #909399; margin-bottom: 6px; }
    .reply-content { font-size: 14px; color: #606266; line-height: 1.6; }
  }
  .reply-text { font-size: 13px; color: #67C23A; }
  .reply-empty { font-size: 13px; color: #c0c4cc; font-style: italic; }
}
</style>
