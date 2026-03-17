<template>
  <div class="schedule-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true">
          <el-form-item label="选择周">
            <el-date-picker
              v-model="weekDate"
              type="week"
              format="yyyy年第WW周"
              placeholder="选择周"
              @change="handleWeekChange">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="教练">
            <el-select v-model="coachId" placeholder="全部" clearable filterable @change="handleQuery">
              <el-option
                v-for="item in coachList"
                :key="item.coachId"
                :label="item.coachName"
                :value="item.coachId">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>

      <div class="schedule-table">
        <table class="course-schedule-table">
          <thead>
            <tr>
              <th width="80">时间</th>
              <th v-for="day in weekDays" :key="day.date" :class="{ 'is-today': isToday(day.date) }">
                <div class="day-header">
                  <span class="day-name">{{ day.name }}</span>
                  <span class="day-date">{{ day.dateStr }}</span>
                </div>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="timeSlot in timeSlots" :key="timeSlot">
              <td class="time-cell">{{ timeSlot }}</td>
              <td v-for="day in weekDays" :key="day.date + timeSlot" :class="{ 'is-today': isToday(day.date) }">
                <div
                  v-for="course in getCoursesForSlot(day.date, timeSlot)"
                  :key="course.scheduleId"
                  class="course-card"
                  :style="{ backgroundColor: getCourseColor(course.coachId) }"
                  @click="handleCourseClick(course)"
                >
                  <div class="course-name">{{ course.courseName }}</div>
                  <div class="course-info">
                    <span>{{ course.remarks || course.coachName }}</span>
                    <span>{{ course.location }}</span>
                  </div>
                  <div class="course-capacity">
                    {{ course.currentCapacity || 0 }}/{{ course.maxCapacity }}
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="legend">
        <div class="legend-title">图例：</div>
        <div v-for="coach in coachList" :key="coach.coachId" class="legend-item">
          <span class="legend-color" :style="{ backgroundColor: getCourseColor(coach.coachId) }"></span>
          <span>{{ coach.coachName }}</span>
        </div>
      </div>
    </el-card>

    <!-- 课程详情对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form v-if="currentCourse" label-width="100px">
        <el-form-item label="课程名称">
          <span>{{ currentCourse.courseName }}</span>
        </el-form-item>
        <el-form-item label="课程分类">
          <span>{{ currentCourse.categoryName }}</span>
        </el-form-item>
        <el-form-item label="课程类型">
          <span>{{ currentCourse.courseType }}</span>
        </el-form-item>
        <el-form-item label="教练">
          <span>{{ currentCourse.coachName }}</span>
        </el-form-item>
        <el-form-item label="教室">
          <span>{{ currentCourse.room }}</span>
        </el-form-item>
        <el-form-item label="上课时间">
          <span>{{ formatTime(currentCourse.startTime) }} - {{ formatTime(currentCourse.endTime) }}</span>
        </el-form-item>
        <el-form-item label="容量">
          <span>{{ currentCourse.currentCapacity }} / {{ currentCourse.maxCapacity }}</span>
        </el-form-item>
        <el-form-item label="课程价格">
          <span>¥{{ currentCourse.price }}</span>
        </el-form-item>
        <el-form-item v-if="currentCourse.description" label="课程描述">
          <span>{{ currentCourse.description }}</span>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleBookCourse">预约课程</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { bookCourse } from '@/api/course'
import { getCoachList } from '@/api/coach'
import { getCoachScheduleList } from '@/api/coachSchedule'

export default {
  name: 'CourseSchedule',
  data() {
    return {
      weekDate: new Date(),
      weekDays: [],
      coachId: null,
      coachList: [],
      courseList: [],
      timeSlots: [
        '06:00', '07:00', '08:00', '09:00', '10:00', '11:00',
        '12:00', '13:00', '14:00', '15:00', '16:00', '17:00',
        '18:00', '19:00', '20:00', '21:00', '22:00'
      ],
      dialogVisible: false,
      dialogTitle: '',
      currentCourse: null,
      colorMap: {}
    }
  },
  created() {
    this.initWeekDays()
    this.getCoachList()
    this.getCourseList()
  },
  methods: {
    initWeekDays() {
      const date = new Date(this.weekDate)
      const day = date.getDay()
      const diff = date.getDate() - day + (day === 0 ? -6 : 1)

      this.weekDays = []
      const weekStart = new Date(date.setDate(diff))

      for (let i = 0; i < 7; i++) {
        const current = new Date(weekStart)
        current.setDate(weekStart.getDate() + i)

        const dayNames = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        this.weekDays.push({
          date: this.formatDate(current),
          name: dayNames[i],
          dateStr: `${current.getMonth() + 1}-${current.getDate()}`
        })
      }
    },
    handleWeekChange() {
      this.initWeekDays()
      this.getCourseList()
    },
    handleQuery() {
      this.getCourseList()
    },
    getCoachList() {
      getCoachList().then(response => {
        this.coachList = response.data || []
        this.coachList.forEach((coach, index) => {
          this.colorMap[coach.coachId] = this.getRandomColor(index)
        })
      })
    },
    getCourseList() {
      const startDate = this.weekDays[0]?.date
      const endDate = this.weekDays[6]?.date

      if (!startDate || !endDate) return

      getCoachScheduleList({
        startDate: startDate,
        endDate: endDate,
        coachId: this.coachId
      }).then(response => {
        this.courseList = response.data || []
      })
    },
    getCoursesForSlot(date, timeSlot) {
      const slotHour = timeSlot ? timeSlot.split(':')[0] : ''
      return this.courseList.filter(course => {
        if (!course.scheduleDate || !course.startTime) return false
        const courseDate = course.scheduleDate
        // startTime可能是LocalTime格式，如 "08:00:00" 或 Date对象
        let courseHour = ''
        if (typeof course.startTime === 'string') {
          courseHour = course.startTime.split(':')[0]
        } else if (course.startTime instanceof Date) {
          courseHour = String(course.startTime.getHours()).padStart(2, '0')
        }
        return courseDate === date && courseHour === slotHour
      })
    },
    handleCourseClick(course) {
      this.currentCourse = course
      this.dialogTitle = course.courseName
      this.dialogVisible = true
    },
    handleBookCourse() {
      if (!this.currentCourse) return

      this.$prompt('请输入会员 ID', '预约课程', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^\d+$/,
        inputErrorMessage: '请输入有效的数字'
      }).then(({ value }) => {
        bookCourse(this.currentCourse.courseId, { memberId: value }).then(() => {
          this.$message.success('预约成功')
          this.dialogVisible = false
          this.getCourseList()
        }).catch(err => {
          this.$message.error(err.message || '预约失败')
        })
      }).catch(() => {})
    },
    formatDate(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    formatTime(dateTime) {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${hours}:${minutes}`
    },
    isToday(dateStr) {
      return dateStr === this.formatDate(new Date())
    },
    getCourseColor(categoryId) {
      return this.colorMap[categoryId] || '#909399'
    },
    getRandomColor(index) {
      const colors = [
        '#67C23A', '#409EFF', '#E6A23C', '#F56C6C', '#909399',
        '#C71585', '#FF8C00', '#00CED1', '#9370DB', '#20B2AA'
      ]
      return colors[index % colors.length]
    }
  }
}
</script>

<style lang="scss" scoped>
.schedule-container {
  .toolbar {
    margin-bottom: 20px;
  }

  .schedule-table {
    overflow-x: auto;
  }

  .course-schedule-table {
    width: 100%;
    border-collapse: collapse;
    table-layout: fixed;

    th, td {
      border: 1px solid #dcdfe6;
      padding: 8px;
      text-align: center;
      min-height: 80px;
      vertical-align: top;
    }

    th {
      background-color: #f5f7fa;
      font-weight: bold;
    }

    .time-cell {
      font-weight: bold;
      color: #606266;
      width: 80px;
    }

    .is-today {
      background-color: #ecf5ff;
    }

    .day-header {
      display: flex;
      flex-direction: column;

      .day-name {
        font-size: 14px;
        font-weight: bold;
      }

      .day-date {
        font-size: 12px;
        color: #909399;
      }
    }
  }

  .course-card {
    padding: 6px;
    margin-bottom: 4px;
    border-radius: 4px;
    color: #fff;
    font-size: 12px;
    cursor: pointer;
    transition: transform 0.2s;

    &:hover {
      transform: scale(1.02);
    }

    .course-name {
      font-weight: bold;
      margin-bottom: 4px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .course-info {
      display: flex;
      justify-content: space-between;
      font-size: 11px;
      opacity: 0.9;
    }

    .course-capacity {
      margin-top: 4px;
      text-align: right;
      font-size: 10px;
    }
  }

  .legend {
    margin-top: 20px;
    display: flex;
    flex-wrap: wrap;
    align-items: center;

    .legend-title {
      font-weight: bold;
      margin-right: 10px;
    }

    .legend-item {
      display: flex;
      align-items: center;
      margin-right: 15px;
      margin-bottom: 5px;

      .legend-color {
        width: 16px;
        height: 16px;
        margin-right: 6px;
        border-radius: 3px;
      }
    }
  }
}
</style>
