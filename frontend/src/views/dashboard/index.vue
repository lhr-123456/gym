<template>
  <div class="dashboard-container">
    <!-- 欢迎横幅 -->
    <el-card class="welcome-card">
      <div class="welcome-bg-images">
        <div class="welcome-bg-half welcome-bg-half-left">
          <img src="@/assets/images/welcome-bg1.jpg" alt="" class="welcome-bg-img" />
        </div>
        <div class="welcome-bg-half welcome-bg-half-right">
          <img src="@/assets/images/welcome-bg2.jpg" alt="" class="welcome-bg-img" />
        </div>
      </div>
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>欢迎回来，{{ username }}！</h2>
          <p>{{ welcomeMessage }}</p>
        </div>
        <div class="welcome-icon">
          <i class="el-icon-gym"></i>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" @click.native="goTo('/member/list')">
          <div class="stat-item">
            <div class="stat-icon">
              <img src="@/assets/images/member-icon.jpeg" alt="会员" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.memberCount }}</div>
              <div class="stat-label">会员总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" @click.native="goTo('/coach/list')">
          <div class="stat-item">
            <div class="stat-icon">
              <img src="@/assets/images/coach-icon.jpeg" alt="教练" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.coachCount }}</div>
              <div class="stat-label">教练总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" @click.native="goTo('/course/list')">
          <div class="stat-item">
            <div class="stat-icon">
              <img src="@/assets/images/course-icon.png" alt="课程" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.courseCount }}</div>
              <div class="stat-label">课程总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" @click.native="goTo('/equipment/list')">
          <div class="stat-item">
            <div class="stat-icon">
              <img src="@/assets/images/equipment-icon.jpeg" alt="器材" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.equipmentCount }}</div>
              <div class="stat-label">器材总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作和最近动态 -->
    <el-row :gutter="20">
      <!-- 快捷操作 -->
      <el-col :span="8">
        <el-card class="quick-actions-card">
          <div slot="header" class="card-header">
            <span>快捷操作</span>
          </div>
          <div class="quick-actions">
            <div class="action-item" @click="goTo('/member/list')">
              <div class="action-icon" style="background: #409EFF">
                <i class="el-icon-plus"></i>
              </div>
              <span>新增会员</span>
            </div>
            <div class="action-item" @click="goTo('/coach/list')">
              <div class="action-icon" style="background: #67C23A">
                <i class="el-icon-plus"></i>
              </div>
              <span>新增教练</span>
            </div>
            <div class="action-item" @click="goTo('/course/list')">
              <div class="action-icon" style="background: #E6A23C">
                <i class="el-icon-plus"></i>
              </div>
              <span>新增课程</span>
            </div>
            <div class="action-item" @click="goTo('/equipment/list')">
              <div class="action-icon" style="background: #F56C6C">
                <i class="el-icon-plus"></i>
              </div>
              <span>新增器材</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 系统信息 -->
      <el-col :span="8">
        <el-card class="system-info-card">
          <div slot="header" class="card-header">
            <span>系统信息</span>
          </div>
          <div class="system-info">
            <div class="info-item">
              <span class="info-label">当前时间：</span>
              <span class="info-value">{{ currentTime }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">用户角色：</span>
              <span class="info-value">{{ role }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">系统版本：</span>
              <span class="info-value">v1.0.0</span>
            </div>
            <div class="info-item">
              <span class="info-label">服务器状态：</span>
              <span class="info-value" style="color: #67C23A">
                <i class="el-icon-circle-check"></i> 正常运行
              </span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 今日概览 -->
      <el-col :span="8">
        <el-card class="today-overview-card">
          <div slot="header" class="card-header">
            <span>今日概览</span>
          </div>
          <div class="today-overview">
            <div class="overview-item">
              <div class="overview-icon overview-icon-img">
                <img src="@/assets/images/date-icon.png" alt="今日日期" />
              </div>
              <div class="overview-info">
                <div class="overview-value">{{ todayDate }}</div>
                <div class="overview-label">今日日期</div>
              </div>
            </div>
            <div class="overview-item">
              <div class="overview-icon overview-icon-img">
                <img src="@/assets/images/greeting-icon.jpg" alt="时段问候" />
              </div>
              <div class="overview-info">
                <div class="overview-value">{{ greeting }}</div>
                <div class="overview-label">时段问候</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getDashboardStatistics } from '@/api/statistics'

export default {
  name: 'Dashboard',
  data() {
    return {
      statistics: {
        memberCount: 0,
        courseCount: 0,
        equipmentCount: 0,
        coachCount: 0
      },
      currentTime: '',
      timer: null
    }
  },
  computed: {
    ...mapGetters(['username', 'role']),
    todayDate() {
      const date = new Date()
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    greeting() {
      const hour = new Date().getHours()
      if (hour < 6) return '凌晨好'
      if (hour < 9) return '早上好'
      if (hour < 12) return '上午好'
      if (hour < 14) return '中午好'
      if (hour < 18) return '下午好'
      if (hour < 22) return '晚上好'
      return '夜深了'
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
    this.fetchStatistics()
    this.updateTime()
    this.timer = setInterval(this.updateTime, 1000)
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    fetchStatistics() {
      getDashboardStatistics().then(res => {
        if (res.code === 200 && res.data) {
          this.statistics = {
            memberCount: res.data.memberCount || 0,
            courseCount: res.data.courseCount || 0,
            equipmentCount: res.data.equipmentCount || 0,
            coachCount: res.data.coachCount || 0
          }
        }
      }).catch(err => {
        console.error('获取统计数据失败：', err)
      })
    },
    updateTime() {
      const now = new Date()
      const hours = String(now.getHours()).padStart(2, '0')
      const minutes = String(now.getMinutes()).padStart(2, '0')
      const seconds = String(now.getSeconds()).padStart(2, '0')
      this.currentTime = `${hours}:${minutes}:${seconds}`
    },
    goTo(path) {
      this.$router.push(path)
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  .welcome-card {
    margin-bottom: 20px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.5) 0%, rgba(118, 75, 162, 0.5) 100%);
    border: none;
    position: relative;
    overflow: hidden;

    ::v-deep .el-card__body {
      padding: 30px;
      position: relative;
      z-index: 1;
    }

    .welcome-bg-images {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      z-index: 0;
      pointer-events: none;
      display: flex;
    }

    .welcome-bg-half {
      width: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      overflow: hidden;
    }

    .welcome-bg-half-left {
      justify-content: center;
    }

    .welcome-bg-half-right {
      justify-content: center;
    }

    .welcome-bg-img {
      max-height: 100%;
      max-width: 100%;
      width: auto;
      height: auto;
      object-fit: contain;
      opacity: 0.85;
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
      cursor: pointer;
      transition: transform 0.3s;

      &:hover {
        transform: translateY(-5px);
      }

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

          img {
            width: 100%;
            height: 100%;
            object-fit: contain;
            border-radius: 12px;
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

  .quick-actions-card {
    .quick-actions {
      display: flex;
      flex-wrap: wrap;
      gap: 15px;

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
          width: 36px;
          height: 36px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 12px;

          i {
            color: #fff;
            font-size: 18px;
          }
        }

        span {
          font-size: 14px;
          color: #666;
        }
      }
    }
  }

  .system-info-card {
    .system-info {
      .info-item {
        display: flex;
        justify-content: space-between;
        padding: 10px 0;
        border-bottom: 1px solid #eee;

        &:last-child {
          border-bottom: none;
        }

        .info-label {
          color: #999;
        }

        .info-value {
          color: #333;
          font-weight: 500;
        }
      }
    }
  }

  .today-overview-card {
    .today-overview {
      .overview-item {
        display: flex;
        align-items: center;
        padding: 15px 0;
        border-bottom: 1px solid #eee;

        &:last-child {
          border-bottom: none;
        }

        .overview-icon {
          width: 48px;
          height: 48px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 15px;
          overflow: hidden;

          i {
            font-size: 24px;
            color: #fff;
          }

          &.overview-icon-img img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            border-radius: 50%;
          }
        }

        .overview-info {
          .overview-value {
            font-size: 18px;
            font-weight: bold;
            color: #333;
          }

          .overview-label {
            font-size: 12px;
            color: #999;
            margin-top: 3px;
          }
        }
      }
    }
  }
}
</style>
