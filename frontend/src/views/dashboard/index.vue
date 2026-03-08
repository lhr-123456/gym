<template>
  <div class="dashboard-container">
    <el-card>
      <div class="dashboard-content">
        <h2>欢迎使用健身房管理系统</h2>
        <p>当前用户：{{ username }}（{{ role }}）</p>

        <el-row :gutter="20" style="margin-top: 30px">
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-item">
                <div class="stat-icon" style="background-color: #409EFF">
                  <i class="el-icon-user"></i>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ statistics.memberCount }}</div>
                  <div class="stat-label">会员总数</div>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-item">
                <div class="stat-icon" style="background-color: #67C23A">
                  <i class="el-icon-video-play"></i>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ statistics.courseCount }}</div>
                  <div class="stat-label">课程总数</div>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-item">
                <div class="stat-icon" style="background-color: #E6A23C">
                  <i class="el-icon-sports"></i>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ statistics.equipmentCount }}</div>
                  <div class="stat-label">器材总数</div>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-item">
                <div class="stat-icon" style="background-color: #F56C6C">
                  <i class="el-icon-user-solid"></i>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ statistics.coachCount }}</div>
                  <div class="stat-label">教练总数</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
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
      }
    }
  },
  computed: {
    ...mapGetters(['username', 'role'])
  },
  created() {
    this.fetchStatistics()
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
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  .dashboard-content {
    h2 {
      margin-bottom: 10px;
      color: #333;
    }

    p {
      color: #666;
    }
  }

  .stat-card {
    margin-bottom: 20px;

    .stat-item {
      display: flex;
      align-items: center;

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 15px;

        i {
          font-size: 30px;
          color: #fff;
        }
      }

      .stat-info {
        flex: 1;

        .stat-value {
          font-size: 24px;
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
</style>
