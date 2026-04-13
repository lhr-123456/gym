<template>
  <div class="coach-messages-container">
    <el-card>
      <div slot="header" class="card-header">
        <span><i class="el-icon-chat-dot-round"></i> 学员消息</span>
        <el-button
          v-if="unreadCount > 0"
          type="text"
          @click="handleMarkAllRead"
          style="padding: 0;"
        >
          <el-badge :value="unreadCount" class="unread-badge">
            <el-button size="small" icon="el-icon-check" type="primary">全部已读</el-button>
          </el-badge>
        </el-button>
      </div>

      <div class="message-list" v-loading="loading">
        <template v-if="messageList.length > 0">
          <div
            v-for="msg in messageList"
            :key="msg.messageId"
            class="message-item"
            :class="{ unread: msg.isRead === 0 }"
            @click="handleClickMessage(msg)"
          >
            <div class="message-avatar">
              <el-avatar :size="44" icon="el-icon-user-solid"
                :style="{ background: msg.isRead === 0 ? '#409EFF' : '#909399' }">
              </el-avatar>
            </div>
            <div class="message-content">
              <div class="message-header">
                <span class="message-title">
                  {{ msg.memberName || '学员' }}
                  <el-tag size="mini" :type="msg.isRead === 0 ? 'primary' : 'info'" style="margin-left: 6px;">
                    {{ msg.isRead === 0 ? '新消息' : '已读' }}
                  </el-tag>
                </span>
                <span class="message-time">{{ msg.createTime | timeFormat }}</span>
              </div>
              <div class="message-body">{{ msg.content }}</div>
            </div>
          </div>
        </template>
        <div v-else class="empty-tip">
          <i class="el-icon-chat-line-round"></i>
          <p>暂无学员消息</p>
        </div>
      </div>

      <div class="pagination" v-if="total > 0">
        <el-pagination
          :current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import {
  getCoachMessagePage,
  getCoachMessageUnreadCount,
  markCoachMessageRead,
  markAllCoachMessagesRead
} from '@/api/coachMessage'

export default {
  name: 'CoachMessages',
  filters: {
    timeFormat(val) {
      if (!val) return '-'
      if (typeof val === 'string') return val.replace('T', ' ').substring(0, 19)
      return val
    }
  },
  data() {
    return {
      loading: false,
      messageList: [],
      pageNum: 1,
      pageSize: 20,
      total: 0,
      unreadCount: 0
    }
  },
  computed: {
    ...mapGetters(['userId'])
  },
  created() {
    this.fetchUnreadCount()
    this.fetchMessages()
  },
  methods: {
    fetchMessages() {
      this.loading = true
      getCoachMessagePage({
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }).then(res => {
        const page = (res && res.data) ? res.data : {}
        this.messageList = page.records || []
        this.total = page.total || 0
      }).catch(() => {
        this.messageList = []
        this.total = 0
      }).finally(() => {
        this.loading = false
      })
    },
    fetchUnreadCount() {
      getCoachMessageUnreadCount().then(res => {
        if ((res.code === 200 || res.code === 0) && res.data != null) {
          this.unreadCount = res.data
        }
      }).catch(() => {})
    },
    handleClickMessage(msg) {
      if (msg.isRead === 0) {
        markCoachMessageRead(msg.messageId).then(() => {
          msg.isRead = 1
          this.unreadCount = Math.max(0, this.unreadCount - 1)
        }).catch(() => {})
      }
    },
    handlePageChange(val) {
      this.pageNum = val
      this.fetchMessages()
    },
    handleMarkAllRead() {
      markAllCoachMessagesRead().then(() => {
        this.unreadCount = 0
        this.fetchMessages()
        this.$message.success('已全部标记为已读')
      }).catch(() => {
        this.$message.error('操作失败')
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.coach-messages-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: bold;
    i { margin-right: 8px; }
    .unread-badge { vertical-align: middle; }
  }
  .message-list { min-height: 300px; }
  .message-item {
    display: flex;
    align-items: flex-start;
    padding: 16px;
    border-bottom: 1px solid #ebeef5;
    cursor: pointer;
    transition: background 0.2s;
    &:hover { background: #f5f7fa; }
    &.unread { background: #f0f9ff; }
    .message-avatar { margin-right: 14px; flex-shrink: 0; }
    .message-content {
      flex: 1;
      min-width: 0;
      .message-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 6px;
        .message-title {
          font-size: 15px;
          font-weight: 500;
          color: #303133;
        }
        .message-time { font-size: 12px; color: #909399; }
      }
      .message-body {
        font-size: 13px;
        color: #606266;
        line-height: 1.6;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
  .pagination { margin-top: 16px; text-align: right; }
  .empty-tip {
    text-align: center; padding: 60px 0; color: #909399;
    i { font-size: 48px; display: block; margin-bottom: 12px; }
    p { margin: 0; }
  }
}
</style>
