<template>
  <div class="points-mall-container">
    <el-card>
      <div class="header-row">
        <div class="points-balance">
          <div class="balance-label">我的积分</div>
          <div class="balance-value">
            <i class="el-icon-coin"></i>
            {{ pointsBalance }}
          </div>
        </div>
        <div class="header-actions">
          <el-button type="text" size="small" @click="handlePointsRule">
            <i class="el-icon-info"></i> 积分规则
          </el-button>
          <el-button type="text" size="small" @click="handleRecordDetail">
            <i class="el-icon-tickets"></i> 积分明细
          </el-button>
        </div>
      </div>

      <!-- 标签页 -->
      <el-tabs v-model="activeTab">
        <el-tab-pane label="商品兑换" name="goods">
          <div class="goods-grid" v-loading="goodsLoading">
            <template v-if="goodsList.length > 0">
              <div v-for="item in goodsList" :key="item.id || item.goodsId" class="goods-card">
                <div class="goods-img">
                  <img :src="item.image || defaultImage" :alt="item.name" @error="handleImageError" />
                </div>
                <div class="goods-info">
                  <div class="goods-name">{{ item.name }}</div>
                  <div class="goods-desc">{{ item.description || '精选商品' }}</div>
                  <div class="goods-price">
                    <span class="price-icon">积分</span>
                    <span class="price-value">{{ item.points }}</span>
                  </div>
                  <div class="goods-stock" v-if="item.stock">库存: {{ item.stock }}</div>
                  <el-button
                    type="primary"
                    size="small"
                    :disabled="item.stock <= 0 || pointsBalance < item.points"
                    @click="handleExchange(item)"
                  >立即兑换</el-button>
                </div>
              </div>
            </template>
            <div v-else class="empty-tip">
              <i class="el-icon-shopping-bag-2"></i>
              <p>暂无商品，敬请期待</p>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="课程兑换" name="courses">
          <div class="course-list" v-loading="coursesLoading">
            <template v-if="courseList.length > 0">
              <div v-for="item in courseList" :key="item.courseId" class="course-card">
                <div class="course-info">
                  <div class="course-name">{{ item.courseName }}</div>
                  <div class="course-meta">
                    <el-tag size="small" :type="item.courseType === '私教课' ? 'warning' : 'primary'">
                      {{ item.courseType || '团课' }}
                    </el-tag>
                    <span class="coach-name">教练: {{ item.coachName || '-' }}</span>
                  </div>
                  <div class="course-desc">{{ item.description || '专业课程' }}</div>
                  <div v-if="item.maxCapacity != null" class="course-capacity">
                    剩余名额 {{ courseRemaining(item) }} / {{ item.maxCapacity }}
                  </div>
                </div>
                <div class="course-action">
                  <div class="course-price">
                    <span class="price-label">所需积分</span>
                    <span class="price-value">{{ item.points || item.price * 10 }}</span>
                  </div>
                  <el-button
                    type="primary"
                    :disabled="courseExchangeDisabled(item)"
                    @click="handleExchangeCourse(item)"
                  >兑换</el-button>
                </div>
              </div>
            </template>
            <div v-else class="empty-tip">
              <i class="el-icon-reading"></i>
              <p>暂无可兑换课程</p>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="兑换记录" name="records">
          <el-table :data="recordList" border v-loading="recordsLoading">
            <el-table-column prop="goodsName" label="商品/课程" min-width="150"></el-table-column>
            <el-table-column prop="points" label="消耗积分" width="100">
              <template slot-scope="scope">
                <span class="points-spent">-{{ scope.row.points }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="exchangeTime" label="兑换时间" width="180"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === '已完成' ? 'success' : 'warning'">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination">
            <el-pagination
              :current-page="recordPage"
              :page-size="recordPageSize"
              :total="recordTotal"
              layout="total, prev, pager, next"
              @current-change="handleRecordPageChange"
            ></el-pagination>
          </div>
        </el-tab-pane>
        <el-tab-pane label="积分任务" name="tasks">
          <div class="task-grid" v-loading="tasksLoading">
            <div v-for="task in taskList" :key="task.taskType" class="task-card" :class="{ 'task-done': !task.canDo }">
              <div class="task-icon">
                <i :class="taskIcon(task.taskType)"></i>
              </div>
              <div class="task-info">
                <div class="task-name">{{ task.taskName }}</div>
                <div class="task-desc">
                  <span v-if="task.repeatable">每日可做</span>
                  <span v-else>一次性</span>
                  <span class="task-points">+{{ task.points }}积分</span>
                </div>
                <div class="task-status">
                  <el-tag v-if="task.canDo" type="success" size="mini">可完成</el-tag>
                  <el-tag v-else type="info" size="mini">
                    {{ task.repeatable ? '今日已完成' : '已完成' }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 积分规则对话框 -->
    <el-dialog title="积分规则" :visible.sync="ruleDialogVisible" width="500px">
      <div class="rule-content">
        <div class="rule-item">
          <div class="rule-title">获取积分</div>
          <ul>
            <li>每日签到: +10 积分/次（连续7天额外奖励）</li>
            <li>完成课程: +20 积分/次</li>
            <li>预约课程: +5 积分/次</li>
            <li>评价课程: +10 积分/次</li>
            <li>完善个人资料: +50 积分（一次性）</li>
            <li>邀请好友注册: +30 积分（一次性）</li>
          </ul>
        </div>
        <div class="rule-item">
          <div class="rule-title">消耗积分</div>
          <ul>
            <li>商品兑换: 详见商品页</li>
            <li>课程兑换: 课程价格 x 10 = 所需积分</li>
            <li>取消预约: -5 积分/次</li>
          </ul>
        </div>
      </div>
      <div slot="footer">
        <el-button @click="ruleDialogVisible = false">我知道了</el-button>
      </div>
    </el-dialog>

    <!-- 积分明细对话框 -->
    <el-dialog title="积分明细" :visible.sync="detailDialogVisible" width="700px">
      <el-table :data="detailList" border v-loading="detailLoading" size="small">
        <el-table-column prop="taskName" label="任务类型" width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.points > 0 ? 'success' : 'warning'" size="mini">
              {{ scope.row.typeLabel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分变动" width="100" align="center">
          <template slot-scope="scope">
            <span :class="scope.row.points > 0 ? 'points-gain' : 'points-spent'">
              {{ scope.row.points > 0 ? '+' : '' }}{{ scope.row.points }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120"></el-table-column>
        <el-table-column prop="createTime" label="时间" width="160"></el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          :current-page="detailPage"
          :page-size="detailPageSize"
          :total="detailTotal"
          layout="total, prev, pager, next"
          @current-change="handleDetailPageChange"
        ></el-pagination>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import {
  getGoodsList,
  getExchangePage,
  exchangeGoods,
  exchangeCourse,
  getMemberPoints,
  getPointsTaskList,
  getPointsRecordPage
} from '@/api/points'
import { getAvailableCourses } from '@/api/course'

export default {
  name: 'MemberPointsMall',
  data() {
    return {
      pointsBalance: 0,
      activeTab: 'goods',
      goodsLoading: false,
      coursesLoading: false,
      recordsLoading: false,
      goodsList: [],
      courseList: [],
      recordList: [],
      recordPage: 1,
      recordPageSize: 10,
      recordTotal: 0,
      tasksLoading: false,
      taskList: [],
      ruleDialogVisible: false,
      detailDialogVisible: false,
      detailLoading: false,
      detailList: [],
      detailPage: 1,
      detailPageSize: 20,
      detailTotal: 0,
      defaultImage: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgZmlsbD0iI2YzZjRmNiIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBkb21pbmFudC1iYXNlbGluZT0ibWlkZGxlIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmaWxsPSIjOTAzOTk5IiBmb250LXNpemU9IjE2Ij5JbWFnZSBOb3QgRm91bmQ8L3RleHQ+PC9zdmc+'
    }
  },
  computed: {
    ...mapGetters(['userId', 'username', 'memberId']),
    currentMemberId() {
      return this.memberId || this.userId
    }
  },
  created() {
    this.fetchMemberPoints()
    this.fetchGoodsList()
    this.fetchCourseList()
    this.fetchRecords()
    this.fetchTaskList()
  },
  methods: {
    fetchMemberPoints() {
      getMemberPoints(this.currentMemberId).then(res => {
        if (res.code === 200 || res.code === '200') {
          this.pointsBalance = res.data || 0
        }
      }).catch(() => {})
    },
    fetchGoodsList() {
      this.goodsLoading = true
      getGoodsList({ type: 'goods', status: 1 }).then(res => {
        if (res.code === 200 || res.code === '200') {
          this.goodsList = (res.data || []).map(item => ({
            ...item,
            image: item.image ? '/' + item.image.replace(/^\//, '') : this.defaultImage
          }))
        }
        this.goodsLoading = false
      }).catch(() => {
        this.goodsLoading = false
      })
    },
    fetchCourseList() {
      this.coursesLoading = true
      // 只展示仍可预约的课程，避免列表含已满员课程导致兑换时报「课程已满员」
      getAvailableCourses({ pageNum: 1, pageSize: 200 }).then(res => {
        if (res.code === 200 || res.code === '200') {
          const page = res.data || {}
          const records = page.records || []
          this.courseList = records.map(item => ({
            ...item,
            courseName: item.courseName || item.name,
            courseId: item.courseId || item.id,
            courseType: item.courseType || (item.privateCourse ? '私教课' : '团课'),
            points: item.points || (item.price ? Math.round(Number(item.price) * 10) : 0)
          }))
        }
        this.coursesLoading = false
      }).catch(() => {
        this.coursesLoading = false
      })
    },
    courseRemaining(item) {
      const max = item.maxCapacity != null ? Number(item.maxCapacity) : 0
      const cur = item.currentCapacity != null ? Number(item.currentCapacity) : 0
      return Math.max(0, max - cur)
    },
    courseExchangeDisabled(item) {
      const need = item.points || (item.price ? Math.round(Number(item.price) * 10) : 0)
      if (this.pointsBalance < need) return true
      const max = item.maxCapacity != null ? Number(item.maxCapacity) : null
      const cur = item.currentCapacity != null ? Number(item.currentCapacity) : null
      if (max != null && cur != null && cur >= max) return true
      return false
    },
    fetchRecords() {
      this.recordsLoading = true
      getExchangePage({
        memberId: this.currentMemberId,
        pageNum: this.recordPage,
        pageSize: this.recordPageSize
      }).then(res => {
        if (res.code === 200 || res.code === '200') {
          const page = res.data
          this.recordList = (page.records || []).map(item => ({
            ...item,
            status: item.status === 1 ? '已完成' : item.status === 0 ? '待处理' : '已取消'
          }))
          this.recordTotal = page.total || 0
        }
        this.recordsLoading = false
      }).catch(() => {
        this.recordsLoading = false
      })
    },
    handleExchange(item) {
      if (this.pointsBalance < item.points) {
        this.$message.warning('积分不足，无法兑换')
        return
      }
      this.$confirm(`确认花费 ${item.points} 积分兑换「${item.name}」吗？`, '积分兑换', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        exchangeGoods({
          memberId: this.currentMemberId,
          goodsId: item.id,
          refId: item.refId
        }).then(res => {
          if (res.code === 200 || res.code === '200') {
            this.$message.success(res.message || '兑换成功！请到前台领取商品')
            this.fetchMemberPoints()
            this.fetchGoodsList()
            this.fetchRecords()
          } else {
            this.$message.error(res.message || '兑换失败')
          }
        })
      }).catch(() => {})
    },
    handleExchangeCourse(item) {
      const points = item.points || 0
      if (this.pointsBalance < points) {
        this.$message.warning('积分不足，无法兑换')
        return
      }
      this.$confirm(`确认花费 ${points} 积分兑换「${item.courseName}」课程吗？`, '积分兑换', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        exchangeCourse({
          memberId: this.currentMemberId,
          refId: item.courseId || item.id,
          goodsName: item.courseName,
          points: points
        }).then(res => {
          if (res.code === 200 || res.code === '200') {
            this.$message.success(res.message || '兑换成功！课程已添加到您的预约中')
            this.fetchMemberPoints()
            this.fetchCourseList()
            this.fetchRecords()
          } else {
            this.$message.error(res.message || '兑换失败')
          }
        }).catch(() => {})
      }).catch(() => {})
    },
    handlePointsRule() {
      this.ruleDialogVisible = true
    },
    handleRecordPageChange(page) {
      this.recordPage = page
      this.fetchRecords()
    },
    fetchTaskList() {
      this.tasksLoading = true
      getPointsTaskList(this.currentMemberId).then(res => {
        if (res.code === 200 || res.code === '200') {
          this.taskList = res.data || []
        }
        this.tasksLoading = false
      }).catch(() => {
        this.tasksLoading = false
      })
    },
    taskIcon(taskType) {
      const iconMap = {
        'signin': 'el-icon-circle-check',
        'complete_course': 'el-icon-medal',
        'booking': 'el-icon-calendar',
        'review': 'el-icon-edit',
        'profile': 'el-icon-user',
        'invite': 'el-icon-user-plus'
      }
      return iconMap[taskType] || 'el-icon-star-on'
    },
    handleRecordDetail() {
      this.detailDialogVisible = true
      this.fetchDetailList()
    },
    fetchDetailList() {
      this.detailLoading = true
      getPointsRecordPage({
        memberId: this.currentMemberId,
        pageNum: this.detailPage,
        pageSize: this.detailPageSize
      }).then(res => {
        if (res.code === 200 || res.code === '200') {
          const page = res.data || {}
          this.detailList = (page.records || []).map(item => ({
            ...item,
            typeLabel: this.getTypeLabel(item.taskType, item.points)
          }))
          this.detailTotal = page.total || 0
        }
        this.detailLoading = false
      }).catch(() => {
        this.detailLoading = false
      })
    },
    getTypeLabel(taskType, points) {
      if (points < 0) return '积分兑换'
      const labelMap = {
        'signin': '每日签到',
        'complete_course': '完成课程',
        'booking': '预约课程',
        'review': '评价课程',
        'profile': '完善资料',
        'invite': '邀请好友'
      }
      return labelMap[taskType] || taskType || '其他'
    },
    handleDetailPageChange(page) {
      this.detailPage = page
      this.fetchDetailList()
    },
    handleImageError(e) {
      e.target.src = this.defaultImage
    }
  }
}
</script>

<style lang="scss" scoped>
.points-mall-container {
  .header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 8px;
    color: #fff;
  }
  .points-balance {
    .balance-label {
      font-size: 14px;
      opacity: 0.9;
      margin-bottom: 8px;
    }
    .balance-value {
      font-size: 32px;
      font-weight: bold;
      i { margin-right: 8px; }
    }
  }
  .header-actions {
    display: flex;
    gap: 12px;
    .el-button--text {
      color: rgba(255,255,255,0.85);
      &:hover { color: #fff; }
    }
  }
  .goods-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 20px;
    min-height: 200px;
  }
  .goods-card {
    background: rgba(255, 255, 255, 0.22) !important;
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    border: 1px solid rgba(255, 255, 255, 0.45) !important;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 8px 24px rgba(30, 58, 138, 0.1);
    transition: all 0.3s;
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 14px 36px rgba(30, 58, 138, 0.16);
    }
    .goods-img {
      height: 150px;
      background: rgba(255, 255, 255, 0.4);
      display: flex;
      align-items: center;
      justify-content: center;
      img {
        max-width: 100%;
        max-height: 100%;
        object-fit: contain;
      }
    }
    .goods-info {
      padding: 12px;
      .goods-name {
        font-size: 14px;
        font-weight: 600;
        color: #1e3a8a;
        margin-bottom: 4px;
      }
      .goods-desc {
        font-size: 12px;
        color: #3b82f6;
        margin-bottom: 8px;
      }
      .goods-price {
        display: flex;
        align-items: baseline;
        margin-bottom: 4px;
        .price-icon {
          font-size: 12px;
          color: #2563eb;
          margin-right: 4px;
          font-weight: 600;
        }
        .price-value {
          font-size: 18px;
          font-weight: bold;
          color: #2563eb;
        }
      }
      .goods-stock {
        font-size: 12px;
        color: #60a5fa;
        margin-bottom: 8px;
        font-weight: 500;
      }
    }
  }
  .course-list {
    min-height: 200px;
  }
  .course-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    background: rgba(255, 255, 255, 0.22) !important;
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.4) !important;
    border-radius: 12px;
    margin-bottom: 12px;
    box-shadow: 0 4px 16px rgba(30, 58, 138, 0.08);
    .course-info {
      flex: 1;
      .course-name {
        font-size: 16px;
        font-weight: 600;
        color: #1e3a8a;
        margin-bottom: 8px;
      }
      .course-meta {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 8px;
        .coach-name {
          font-size: 12px;
          color: #2563eb;
          font-weight: 500;
        }
      }
      .course-desc {
        font-size: 12px;
        color: #3b82f6;
        margin-bottom: 6px;
      }
      .course-capacity {
        font-size: 12px;
        color: #1e3a8a;
        font-weight: 600;
      }
    }
    .course-action {
      text-align: right;
      .course-price {
        margin-bottom: 12px;
        .price-label {
          font-size: 12px;
          color: #3b82f6;
          margin-right: 8px;
          font-weight: 500;
        }
        .price-value {
          font-size: 20px;
          font-weight: bold;
          color: #2563eb;
        }
      }
    }
  }
  .pagination {
    margin-top: 20px;
    text-align: right;
  }
  .points-spent {
    color: #2563eb;
    font-weight: bold;
  }
  .points-gain {
    color: #1e3a8a;
    font-weight: bold;
  }
  .task-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 16px;
    padding: 4px;
  }
  .task-card {
    display: flex;
    align-items: center;
    gap: 14px;
    padding: 16px;
    background: rgba(255, 255, 255, 0.22) !important;
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.4) !important;
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(30, 58, 138, 0.08);
    transition: all 0.3s;
    &.task-done {
      opacity: 0.6;
      background: rgba(255, 255, 255, 0.12) !important;
    }
    &:hover {
      background: rgba(255, 255, 255, 0.38) !important;
      border-color: rgba(37, 99, 235, 0.35) !important;
    }
    .task-icon {
      font-size: 32px;
      color: #2563eb;
      flex-shrink: 0;
    }
    .task-info {
      flex: 1;
      .task-name {
        font-size: 15px;
        font-weight: 700;
        color: #1e3a8a;
        margin-bottom: 4px;
      }
      .task-desc {
        font-size: 12px;
        color: #3b82f6;
        margin-bottom: 6px;
        display: flex;
        gap: 8px;
        align-items: center;
      }
      .task-points {
        color: #2563eb;
        font-weight: bold;
      }
    }
  }
  .empty-tip {
    text-align: center;
    padding: 40px;
    color: #2563eb;
    i { font-size: 48px; display: block; margin-bottom: 12px; color: #2563eb; }
    p { margin: 0; color: #3b82f6; }
  }
  .rule-content {
    .rule-item {
      margin-bottom: 20px;
      .rule-title {
        font-size: 16px;
        font-weight: bold;
        color: #1e3a8a;
        margin-bottom: 12px;
      }
      ul {
        margin: 0;
        padding-left: 20px;
        li {
          color: #3b82f6;
          margin-bottom: 8px;
        }
      }
    }
  }
}
</style>
