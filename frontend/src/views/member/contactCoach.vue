<template>
  <div class="contact-coach-container">
    <el-card>
      <div slot="header" class="card-header">
        <span><i class="el-icon-chat-line-round"></i> 联系教练</span>
        <span class="sub-title">选择教练并发送消息</span>
      </div>

      <el-input
        v-model="searchKeyword"
        placeholder="搜索教练姓名或专长"
        prefix-icon="el-icon-search"
        clearable
        class="search-input"
        @input="filterCoachList"
      ></el-input>

      <div class="coach-list" v-loading="loading">
        <template v-if="filteredCoachList.length > 0">
          <div
            v-for="coach in filteredCoachList"
            :key="coach.coachId"
            class="coach-card"
          >
            <div class="coach-avatar">
              <el-avatar :size="56" :src="coach.avatar">
                {{ (coach.coachName || '教').charAt(0) }}
              </el-avatar>
            </div>
            <div class="coach-info">
              <div class="coach-name">{{ coach.coachName || '教练' }}</div>
              <div class="coach-meta" v-if="coach.specialty || coach.phoneNum">
                <span v-if="coach.specialty"><i class="el-icon-star-on"></i> {{ coach.specialty }}</span>
                <span v-if="coach.phoneNum" class="phone"><i class="el-icon-phone"></i> {{ coach.phoneNum }}</span>
              </div>
              <el-button
                type="primary"
                size="small"
                class="btn-contact"
                @click="openContactDialog(coach)"
              >
                <i class="el-icon-chat-dot-round"></i> 联系
              </el-button>
            </div>
          </div>
        </template>
        <div v-else class="empty-tip">
          <i class="el-icon-user"></i>
          <p>{{ searchKeyword ? '未找到匹配的教练' : '暂无教练信息' }}</p>
        </div>
      </div>
    </el-card>

    <!-- 联系教练对话框 -->
    <el-dialog
      title="发送消息"
      :visible.sync="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
      @close="resetContactForm"
    >
      <div v-if="currentCoach" class="dialog-coach">
        <span>发送给：</span>
        <strong>{{ currentCoach.coachName }}</strong>
      </div>
      <el-form ref="contactForm" :model="contactForm" :rules="contactRules" label-width="80px">
        <el-form-item label="消息内容" prop="content">
          <el-input
            v-model="contactForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入您要咨询或反馈的内容..."
            maxlength="500"
            show-word-limit
          ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="sending" @click="handleSendMessage">发送</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getCoachList } from '@/api/coach'
import { sendMessageToCoach } from '@/api/coachMessage'

export default {
  name: 'MemberContactCoach',
  data() {
    return {
      loading: false,
      searchKeyword: '',
      coachList: [],
      filteredCoachList: [],
      dialogVisible: false,
      currentCoach: null,
      sending: false,
      contactForm: {
        content: ''
      },
      contactRules: {
        content: [
          { required: true, message: '请输入消息内容', trigger: 'blur' },
          { min: 5, message: '消息内容至少5个字符', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters(['userId'])
  },
  created() {
    this.fetchCoachList()
  },
  methods: {
    fetchCoachList() {
      this.loading = true
      getCoachList().then(res => {
        if (res && (res.code === 200 || res.code === 0)) {
          const list = Array.isArray(res.data) ? res.data
            : (res.data && res.data.list) || (res.data && res.data.records) || []
          this.coachList = list
          this.filteredCoachList = list
        } else {
          this.coachList = []
          this.filteredCoachList = []
        }
      }).catch(() => {
        this.coachList = []
        this.filteredCoachList = []
      }).finally(() => {
        this.loading = false
      })
    },

    filterCoachList() {
      const kw = (this.searchKeyword || '').trim().toLowerCase()
      if (!kw) {
        this.filteredCoachList = this.coachList
        return
      }
      this.filteredCoachList = this.coachList.filter(c => {
        const name = (c.coachName || '').toLowerCase()
        const specialty = (c.specialty || '').toLowerCase()
        const phone = (c.phoneNum || '').toLowerCase()
        return name.includes(kw) || specialty.includes(kw) || phone.includes(kw)
      })
    },

    openContactDialog(coach) {
      this.currentCoach = coach
      this.contactForm.content = ''
      this.dialogVisible = true
    },

    resetContactForm() {
      this.currentCoach = null
      this.contactForm.content = ''
      if (this.$refs.contactForm) this.$refs.contactForm.resetFields()
    },

    handleSendMessage() {
      this.$refs.contactForm.validate(valid => {
        if (!valid) return
        const content = (this.contactForm.content || '').trim()
        if (!content) {
          this.$message.warning('请输入消息内容')
          return
        }
        this.sending = true
        sendMessageToCoach({
          coachId: this.currentCoach.coachId,
          content
        }).then(res => {
          if (res.code === 200 || res.code === 0) {
            this.$message.success(res.message || '消息已发送，教练会尽快回复您')
            this.dialogVisible = false
            this.resetContactForm()
          } else {
            this.$message.error(res.message || res.msg || '发送失败')
          }
        }).catch(err => {
          this.$message.error((err && err.message) ? err.message : '发送失败，请重试')
        }).finally(() => {
          this.sending = false
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.contact-coach-container {
  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    .sub-title {
      font-size: 13px;
      color: #909399;
      font-weight: normal;
    }
  }
  .search-input {
    margin-bottom: 20px;
    max-width: 320px;
  }
  .coach-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 16px;
    min-height: 200px;
  }
  .coach-card {
    display: flex;
    align-items: center;
    padding: 16px;
    background: rgba(255, 255, 255, 0.22) !important;
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.4) !important;
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(30, 58, 138, 0.08);
    transition: all 0.3s;
    &:hover {
      background: rgba(255, 255, 255, 0.38) !important;
      border-color: rgba(37, 99, 235, 0.35) !important;
      transform: translateY(-2px);
      box-shadow: 0 10px 28px rgba(30, 58, 138, 0.15);
    }
    .coach-avatar { margin-right: 16px; }
    .coach-info {
      flex: 1;
      min-width: 0;
      .coach-name {
        font-size: 16px;
        font-weight: 700;
        color: #1e3a8a;
        margin-bottom: 6px;
      }
      .coach-meta {
        font-size: 12px;
        color: #3b82f6;
        margin-bottom: 10px;
        font-weight: 500;
        i { color: #2563eb; margin-right: 3px; }
        span + span.phone { margin-left: 12px; }
      }
      .btn-contact { margin-top: 4px; }
    }
  }
  .empty-tip {
    grid-column: 1 / -1;
    text-align: center;
    padding: 40px;
    color: #2563eb;
    i { font-size: 48px; display: block; margin-bottom: 12px; color: #2563eb; }
    p { margin: 0; color: #3b82f6; }
  }
  .dialog-coach {
    margin-bottom: 16px;
    font-size: 14px;
    color: #1e3a8a;
    font-weight: 600;
    strong { margin-left: 8px; color: #1e3a8a; }
  }
}
</style>
