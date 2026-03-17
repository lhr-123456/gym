<template>
  <div class="coach-dashboard">
    <!-- 欢迎横幅 -->
    <el-card class="welcome-card">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>欢迎回来，{{ coachName }}教练！</h2>
          <p>{{ welcomeMessage }} · 今日 {{ todayCourseCount }} 节课 · {{ todayMemberCount }} 名学员</p>
        </div>
        <div class="welcome-icon">
          <i class="el-icon-user-solid"></i>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <i class="el-icon-time"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.monthHours || 0 }}h</div>
              <div class="stat-label">本月课时</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <i class="el-icon-star-on"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.rating || '0.0' }}</div>
              <div class="stat-label">学员评分</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <i class="el-icon-circle-check"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.attendance || '0%' }}</div>
              <div class="stat-label">出勤率</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
              <i class="el-icon-user"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalMembers || 0 }}</div>
              <div class="stat-label">累计学员</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 今日课程和快捷操作 -->
    <el-row :gutter="20">
      <!-- 今日课程安排 -->
      <el-col :span="14">
        <el-card class="course-card">
          <div slot="header" class="card-header">
            <span>📅 今日课程安排</span>
          </div>
          <div class="course-list" v-if="todayCourses.length > 0">
            <div
              v-for="course in todayCourses"
              :key="course.bookingId"
              class="course-item"
              :class="{ 'current': course.isCurrent }"
            >
              <div class="course-status">
                <el-tag v-if="course.isCurrent" type="success" size="small">正在进行</el-tag>
                <el-tag v-else-if="course.isNext" type="warning" size="small">下一节</el-tag>
                <el-tag v-else type="info" size="small">已结束</el-tag>
              </div>
              <div class="course-time">
                {{ course.startTime }} - {{ course.endTime }}
              </div>
              <div class="course-name">{{ course.courseName }}</div>
              <div class="course-info">
                学员 {{ course.bookedCount }}/{{ course.maxCapacity }}人 ·
                <span v-if="course.signedCount > 0">{{ course.signedCount }}人已签到</span>
                <span v-else>暂无签到</span>
              </div>
              <div class="course-actions">
                <el-button
                  v-if="course.status === '已预约'"
                  type="primary"
                  size="small"
                  @click="handleStartCourse(course)"
                >开始上课</el-button>
                <el-button
                  v-if="course.status === '已签到'"
                  type="success"
                  size="small"
                  @click="handleSignin(course)"
                >学员签到</el-button>
              </div>
            </div>
          </div>
          <div class="empty-tip" v-else>
            <i class="el-icon-calendar"></i>
            <p>今日暂无课程安排</p>
          </div>
        </el-card>
      </el-col>

      <!-- 快捷操作 -->
      <el-col :span="10">
        <el-card class="action-card">
          <div slot="header" class="card-header">
            <span>📊 快捷操作</span>
          </div>
          <div class="quick-actions">
            <div class="action-item" @click="handleQuickAction('start')">
              <div class="action-icon" style="background: #409EFF">
                <i class="el-icon-video-play"></i>
              </div>
              <span>开始上课</span>
            </div>
            <div class="action-item" @click="handleQuickAction('bodyTest')">
              <div class="action-icon" style="background: #67C23A">
                <i class="el-icon-data-analysis"></i>
              </div>
              <span>录入体测</span>
            </div>
            <div class="action-item" @click="handleQuickAction('contact')">
              <div class="action-icon" style="background: #E6A23C">
                <i class="el-icon-chat-line-round"></i>
              </div>
              <span>联系学员</span>
            </div>
            <div class="action-item" @click="handleQuickAction('review')">
              <div class="action-icon" style="background: #F56C6C">
                <i class="el-icon-star-on"></i>
              </div>
              <span>课后评价</span>
            </div>
          </div>

          <!-- 学员签到进度 -->
          <div class="signin-progress" v-if="todayCourses.length > 0">
            <div class="progress-title">学员签到进度</div>
            <el-progress
              :percentage="signinProgress"
              :status="signinProgress === 100 ? 'success' : ''"
              :stroke-width="10"
            ></el-progress>
            <div class="progress-text">已签到 {{ signedCount }}/{{ totalCount }}人</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 待办提醒和今日学员 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 待办提醒 -->
      <el-col :span="12">
        <el-card class="todo-card">
          <div slot="header" class="card-header">
            <span>🔔 待办提醒 ({{ todos.length }})</span>
          </div>
          <div class="todo-list" v-if="todos.length > 0">
            <div v-for="todo in todos" :key="todo.id" class="todo-item">
              <div class="todo-content">
                <i :class="todo.icon" :style="{ color: todo.color }"></i>
                <span>{{ todo.content }}</span>
              </div>
              <el-button
                v-if="todo.actionText"
                type="text"
                size="small"
                @click="handleTodoAction(todo)"
              >{{ todo.actionText }}</el-button>
            </div>
          </div>
          <div class="empty-tip" v-else>
            <i class="el-icon-bell"></i>
            <p>暂无待办事项</p>
          </div>
        </el-card>
      </el-col>

      <!-- 今日上课学员 -->
      <el-col :span="12">
        <el-card class="member-card">
          <div slot="header" class="card-header">
            <span>👥 今日上课学员</span>
          </div>
          <div class="member-list" v-if="todayMembers.length > 0">
            <div class="member-avatars">
              <el-avatar
                v-for="member in todayMembers.slice(0, 8)"
                :key="member.memberId"
                :size="50"
                :src="member.avatar || defaultAvatar"
                class="member-avatar"
              >
                {{ member.memberName ? member.memberName.charAt(0) : '?' }}
              </el-avatar>
              <el-avatar v-if="todayMembers.length > 8" :size="50" class="more-avatar">
                +{{ todayMembers.length - 8 }}
              </el-avatar>
            </div>
            <div class="member-names">
              <span v-for="member in todayMembers.slice(0, 6)" :key="member.memberId">
                {{ member.memberName }}
              </span>
              <span v-if="todayMembers.length > 6">...</span>
            </div>
          </div>
          <div class="empty-tip" v-else>
            <i class="el-icon-user"></i>
            <p>今日暂无学员上课</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import {
  getCoachDashboard,
  getCoachTodayCourses,
  getCoachTodayMembers,
  getCoachTodos,
  startCourse,
  signinMember
} from '@/api/coachDashboard'

export default {
  name: 'CoachDashboard',
  data() {
    return {
      coachName: '',
      statistics: {
        monthHours: 0,
        rating: '0.0',
        attendance: '0%',
        totalMembers: 0
      },
      todayCourses: [],
      todayMembers: [],
      todos: [],
      defaultAvatar: '',
      timer: null
    }
  },
  computed: {
    ...mapGetters(['username', 'userType', 'userId']),
    todayCourseCount() {
      return this.todayCourses.length
    },
    todayMemberCount() {
      const uniqueMembers = new Set(this.todayMembers.map(m => m.memberId))
      return uniqueMembers.size
    },
    signedCount() {
      return this.todayCourses.reduce((sum, c) => sum + (c.signedCount || 0), 0)
    },
    totalCount() {
      return this.todayCourses.reduce((sum, c) => sum + (c.bookedCount || 0), 0)
    },
    signinProgress() {
      if (this.totalCount === 0) return 0
      return Math.round((this.signedCount / this.totalCount) * 100)
    },
    welcomeMessage() {
      const hour = new Date().getHours()
      if (hour < 6) return '夜深了，注意休息'
      if (hour < 9) return '新的一天，从运动开始'
      if (hour < 12) return '上午好，工作顺利'
      if (hour < 14) return '午休时间，注意休息'
      if (hour < 18) return '下午好，继续加油'
      if (hour < 22) return '晚上好，来运动一下吧'
      return '夜深了，注意休息'
    }
  },
  created() {
    this.initData()
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    initData() {
      // 获取教练信息
      this.fetchCoachInfo()
      // 获取首页数据
      this.fetchDashboardData()
      // 获取今日课程
      this.fetchTodayCourses()
      // 获取今日学员
      this.fetchTodayMembers()
      // 获取待办事项
      this.fetchTodos()
    },
    fetchCoachInfo() {
      // 从 store 获取教练名称
      this.coachName = this.username || '教练'
    },
    fetchDashboardData() {
      getCoachDashboard(this.userId).then(res => {
        if (res.code === 200 && res.data) {
          this.statistics = {
            monthHours: res.data.monthHours || 0,
            rating: res.data.rating || '0.0',
            attendance: res.data.attendance || '0%',
            totalMembers: res.data.totalMembers || 0
          }
          if (res.data.coachName) {
            this.coachName = res.data.coachName
          }
        }
      }).catch(err => {
        console.error('获取教练数据失败：', err)
      })
    },
    fetchTodayCourses() {
      getCoachTodayCourses(this.userId).then(res => {
        if (res.code === 200 && res.data) {
          this.todayCourses = this.processCourses(res.data)
        }
      }).catch(err => {
        console.error('获取今日课程失败：', err)
      })
    },
    processCourses(courses) {
      const now = new Date()
      const currentHour = now.getHours()
      const currentMinute = now.getMinutes()
      const currentTime = currentHour * 60 + currentMinute

      return courses.map(course => {
        const startParts = (course.startTime || '').split(':')
        const endParts = (course.endTime || '').split(':')
        const startTimeMinutes = parseInt(startParts[0]) * 60 + parseInt(startParts[1] || 0)
        const endTimeMinutes = parseInt(endParts[0]) * 60 + parseInt(endParts[1] || 0)

        let isCurrent = false
        let isNext = false

        if (currentTime >= startTimeMinutes && currentTime < endTimeMinutes) {
          isCurrent = true
        } else if (currentTime < startTimeMinutes && (startTimeMinutes - currentTime) <= 60) {
          isNext = true
        }

        return {
          ...course,
          isCurrent,
          isNext
        }
      })
    },
    fetchTodayMembers() {
      getCoachTodayMembers(this.userId).then(res => {
        if (res.code === 200 && res.data) {
          this.todayMembers = res.data
        }
      }).catch(err => {
        console.error('获取今日学员失败：', err)
      })
    },
    fetchTodos() {
      getCoachTodos(this.userId).then(res => {
        if (res.code === 200 && res.data) {
          this.todos = res.data
        }
      }).catch(err => {
        console.error('获取待办事项失败：', err)
      })
    },
    handleStartCourse(course) {
      this.$confirm('确认开始上课吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        startCourse(course.bookingId).then(() => {
          this.$message.success('课程已开始')
          this.fetchTodayCourses()
        })
      })
    },
    handleSignin(course) {
      this.$prompt('请输入学员姓名或会员卡号进行签到', '学员签到', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        // 实际应该根据输入查找对应预约进行签到
        this.$message.success(`学员 ${value} 签到成功`)
        this.fetchTodayCourses()
      })
    },
    handleQuickAction(type) {
      switch (type) {
        case 'start':
          // 找到当前进行的课程
          const currentCourse = this.todayCourses.find(c => c.isCurrent)
          if (currentCourse) {
            this.handleStartCourse(currentCourse)
          } else {
            this.$message.info('当前没有进行中的课程')
          }
          break
        case 'bodyTest':
          this.$router.push('/coach-bodytest/add')
          break
        case 'contact':
          this.$message.info('联系学员功能开发中')
          break
        case 'review':
          this.$router.push('/coach-reviews/list')
          break
      }
    },
    handleTodoAction(todo) {
      if (todo.action === 'approveLeave') {
        this.$confirm('批准该请假申请？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '拒绝',
          type: 'info'
        }).then(() => {
          this.$message.success('已批准请假')
          this.fetchTodos()
        })
      } else if (todo.action === 'fillReview') {
        this.$router.push('/coach-reviews/list')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.coach-dashboard {
  .welcome-card {
    margin-bottom: 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;

    ::v-deep .el-card__body {
      padding: 30px;
    }

    .welcome-content {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .welcome-text {
        color: #fff;

        h2 {
          margin: 0 0 10px 0;
          font-size: 24px;
        }

        p {
          margin: 0;
          opacity: 0.9;
        }
      }

      .welcome-icon {
        i {
          font-size: 80px;
          color: rgba(255, 255, 255, 0.3);
        }
      }
    }
  }

  .stat-row {
    margin-bottom: 20px;

    .stat-card {
      .stat-item {
        display: flex;
        align-items: center;

        .stat-icon {
          width: 60px;
          height: 60px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 15px;

          i {
            font-size: 28px;
            color: #fff;
          }
        }

        .stat-info {
          flex: 1;

          .stat-value {
            font-size: 28px;
            font-weight: bold;
            color: #333;
          }

          .stat-label {
            font-size: 14px;
            color: #999;
            margin-top: 5px;
          }
        }
      }
    }
  }

  .card-header {
    font-size: 16px;
    font-weight: bold;
  }

  .course-card {
    .course-list {
      .course-item {
        padding: 15px;
        border-radius: 8px;
        background: #f5f7fa;
        margin-bottom: 15px;
        position: relative;

        &.current {
          background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
          border-left: 4px solid #67C23A;
        }

        &:last-child {
          margin-bottom: 0;
        }

        .course-status {
          position: absolute;
          top: 15px;
          right: 15px;
        }

        .course-time {
          font-size: 16px;
          font-weight: bold;
          color: #333;
          margin-bottom: 5px;
        }

        .course-name {
          font-size: 18px;
          color: #409EFF;
          margin-bottom: 5px;
        }

        .course-info {
          font-size: 14px;
          color: #666;
          margin-bottom: 10px;
        }
      }
    }

    .empty-tip {
      text-align: center;
      padding: 40px 0;
      color: #999;

      i {
        font-size: 48px;
        margin-bottom: 10px;
      }

      p {
        margin: 0;
      }
    }
  }

  .action-card {
    .quick-actions {
      display: flex;
      flex-wrap: wrap;
      gap: 15px;
      margin-bottom: 20px;

      .action-item {
        width: calc(50% - 7.5px);
        display: flex;
        align-items: center;
        padding: 15px;
        background: #f5f7fa;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background: #ecf5ff;
          transform: translateX(5px);
        }

        .action-icon {
          width: 40px;
          height: 40px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 12px;

          i {
            color: #fff;
            font-size: 20px;
          }
        }

        span {
          font-size: 14px;
          color: #666;
        }
      }
    }

    .signin-progress {
      .progress-title {
        font-size: 14px;
        color: #666;
        margin-bottom: 10px;
      }

      .progress-text {
        text-align: center;
        font-size: 14px;
        color: #999;
        margin-top: 10px;
      }
    }
  }

  .todo-card {
    .todo-list {
      .todo-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid #eee;

        &:last-child {
          border-bottom: none;
        }

        .todo-content {
          display: flex;
          align-items: center;

          i {
            font-size: 18px;
            margin-right: 10px;
          }
        }
      }
    }

    .empty-tip {
      text-align: center;
      padding: 40px 0;
      color: #999;

      i {
        font-size: 48px;
        margin-bottom: 10px;
      }

      p {
        margin: 0;
      }
    }
  }

  .member-card {
    .member-list {
      .member-avatars {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;
        margin-bottom: 15px;

        .member-avatar {
          cursor: pointer;
          transition: transform 0.3s;

          &:hover {
            transform: scale(1.1);
          }
        }

        .more-avatar {
          background: #909399;
          color: #fff;
        }
      }

      .member-names {
        span {
          display: inline-block;
          padding: 4px 8px;
          margin-right: 8px;
          margin-bottom: 8px;
          background: #f5f7fa;
          border-radius: 4px;
          font-size: 14px;
          color: #666;
        }
      }
    }

    .empty-tip {
      text-align: center;
      padding: 40px 0;
      color: #999;

      i {
        font-size: 48px;
        margin-bottom: 10px;
      }

      p {
        margin: 0;
      }
    }
  }
}
</style>
