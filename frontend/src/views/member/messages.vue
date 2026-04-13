<template>
  <div class="messages-container">
    <el-card>
      <div class="header">
        <h2>消息中心</h2>
        <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="unread-badge">
          <el-button type="text" @click="handleMarkAllRead" :disabled="unreadCount === 0">
            <i class="el-icon-check"></i> 全部已读
          </el-button>
        </el-badge>
      </div>

      <!-- 消息分类 -->
      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane label="全部" name="all">
          <span slot="label"><i class="el-icon-document"></i> 全部</span>
        </el-tab-pane>
        <el-tab-pane label="系统通知" name="system">
          <span slot="label"><i class="el-icon-bell"></i> 系统通知</span>
        </el-tab-pane>
        <el-tab-pane label="课程提醒" name="course">
          <span slot="label"><i class="el-icon-date"></i> 课程提醒</span>
        </el-tab-pane>
        <el-tab-pane label="教练回复" name="coach">
          <span slot="label"><i class="el-icon-chat-dot-round"></i> 教练回复</span>
        </el-tab-pane>
      </el-tabs>

      <!-- 消息列表 -->
      <div class="message-list" v-loading="loading">
        <template v-if="messageList.length > 0">
          <div
            v-for="msg in messageList"
            :key="msg.messageId"
            class="message-item"
            :class="{ unread: msg.isRead === 0 }"
            @click="handleClickMessage(msg)"
          >
            <div class="message-icon" :style="{ background: iconColor(msg.type) }">
              <i :class="getMessageIcon(msg.type)"></i>
            </div>
            <div class="message-content">
              <div class="message-header">
                <span class="message-title">{{ msg.title }}</span>
                <span class="message-time">{{ msg.createTime | timeFormat }}</span>
              </div>
              <div class="message-body">{{ msg.content }}</div>
              <div class="message-footer" v-if="msg.refId">
                <el-tag size="mini" type="info">{{ refTypeText(msg.refType) }}</el-tag>
                <el-button type="text" size="small" @click.stop="handleMessageAction(msg)">
                  查看详情
                </el-button>
              </div>
            </div>
            <div class="message-status" v-if="msg.isRead === 0">
              <span class="unread-dot"></span>
            </div>
          </div>
        </template>
        <div v-else class="empty-tip">
          <i class="el-icon-bell"></i>
          <p>暂无消息</p>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="total > 0">
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
import {
  getMemberMessagePage,
  getMemberMessageUnreadCount,
  markMessageRead,
  markAllMessagesRead
} from '@/api/memberMessage'

export default {
  name: 'MemberMessages',
  filters: {
    timeFormat(val) {
      if (!val) return '-'
      if (typeof val === 'string') {
        return val.replace('T', ' ').substring(0, 19)
      }
      return val
    }
  },
  data() {
    return {
      activeTab: 'all',
      messageList: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
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
      getMemberMessagePage({
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        type: this.activeTab === 'all' ? null : this.activeTab
      }).then(res => {
        const page = (res && res.data) ? res.data : {}
        this.messageList = page.records || []
        this.total = page.total || 0
      }).catch(() => {
        this.messageList = []
        this.total = 0
        this.$message.error('获取消息列表失败')
      }).finally(() => {
        this.loading = false
      })
    },

    fetchUnreadCount() {
      getMemberMessageUnreadCount().then(res => {
        if ((res.code === 200 || res.code === 0) && res.data != null) {
          this.unreadCount = res.data
        }
      }).catch(() => {})
    },

    handleTabChange() {
      this.pageNum = 1
      this.fetchMessages()
    },

    handleSizeChange(val) {
      this.pageSize = val
      this.pageNum = 1
      this.fetchMessages()
    },

    handleCurrentChange(val) {
      this.pageNum = val
      this.fetchMessages()
    },

    handleClickMessage(msg) {
      if (msg.isRead === 0) {
        markMessageRead(msg.messageId).then(() => {
          msg.isRead = 1
          this.unreadCount = Math.max(0, this.unreadCount - 1)
        }).catch(() => {})
      }
    },

    handleMessageAction(msg) {
      if (msg.refType === 'course_booking') {
        this.$router.push('/course/booking')
      } else if (msg.refType === 'course_review') {
        this.$router.push('/course/review')
      } else {
        this.$router.push('/member/home')
      }
    },

    handleMarkAllRead() {
      this.$confirm('确定将所有消息标为已读吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        markAllMessagesRead().then(() => {
          this.unreadCount = 0
          this.fetchMessages()
          this.$message.success('已全部标为已读')
        }).catch(() => {
          this.$message.error('操作失败')
        })
      }).catch(() => {})
    },

    getMessageIcon(type) {
      const iconMap = {
        'system': 'el-icon-bell',
        'course': 'el-icon-date',
        'coach': 'el-icon-chat-dot-round'
      }
      return iconMap[type] || 'el-icon-message'
    },

    iconColor(type) {
      const colorMap = {
        'system': '#409EFF',
        'course': '#67C23A',
        'coach': '#E6A23C'
      }
      return colorMap[type] || '#909399'
    },

    refTypeText(refType) {
      const map = {
        'course_booking': '课程预约',
        'course_review': '课程评价',
        'member_card': '会员卡',
        'points_exchange': '积分兑换'
      }
      return map[refType] || refType
    }
  }
}
</script>

<style lang="scss" scoped>
.messages-container {
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
  .header {
    margin-bottom: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    h2 { margin: 0; color: #1e3a8a; font-weight: 700; }
    .unread-badge { display: inline-block; }
  }

  .message-list { min-height: 300px; }
  .message-item {
    display: flex;
    padding: 16px;
    border-bottom: 1px solid rgba(30, 58, 138, 0.1);
    cursor: pointer;
    transition: background 0.2s;
    &:hover { background: rgba(37, 99, 235, 0.04); }
    &.unread {
      background: rgba(37, 99, 235, 0.06);
      &:hover { background: rgba(37, 99, 235, 0.1); }
    }
    .message-icon {
      width: 44px;
      height: 44px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 14px;
      flex-shrink: 0;
      i { font-size: 20px; color: #fff; }
    }
    .message-content {
      flex: 1;
      min-width: 0;
      .message-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 6px;
        .message-title { font-size: 15px; font-weight: 700; color: #1e3a8a; }
        .message-time { font-size: 12px; color: #3b82f6; font-weight: 500; }
      }
      .message-body {
        font-size: 13px; color: #3b82f6; line-height: 1.6;
        overflow: hidden; text-overflow: ellipsis;
        display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical;
        font-weight: 500;
      }
      .message-footer {
        margin-top: 8px;
        display: flex; align-items: center; gap: 8px;
      }
    }
    .message-status {
      margin-left: 12px;
      display: flex; align-items: center;
      .unread-dot {
        width: 8px; height: 8px; border-radius: 50%; background: #2563eb;
      }
    }
  }

  .pagination { margin-top: 20px; text-align: right; }

  .empty-tip {
    text-align: center; padding: 60px 0; color: #2563eb;
    i { font-size: 48px; display: block; margin-bottom: 12px; color: #2563eb; }
    p { margin: 0; color: #3b82f6; }
  }
}
</style></style>






