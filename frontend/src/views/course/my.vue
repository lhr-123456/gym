<template>
  <div class="my-course-container">
    <el-card>
      <div class="header">
        <h2>我的课程</h2>
        <el-button type="primary" @click="handleAddCourse">添加课程</el-button>
      </div>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="courseId" label="课程 ID" width="80"></el-table-column>
        <el-table-column prop="courseName" label="课程名称" width="150"></el-table-column>
        <el-table-column prop="courseType" label="课程类型" width="80"></el-table-column>
        <el-table-column prop="durationMin" label="时长 (分钟)" width="100"></el-table-column>
        <el-table-column prop="price" label="价格" width="80"></el-table-column>
        <el-table-column prop="maxCapacity" label="最大容量" width="80"></el-table-column>
        <el-table-column prop="currentCapacity" label="已预约" width="80"></el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160"></el-table-column>
        <el-table-column prop="room" label="教室" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="220">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleView(scope.row)">查看详情</el-button>
            <el-button size="mini" type="primary" @click="handleManageBooking(scope.row)">管理预约</el-button>
            <el-button size="mini" type="warning" @click="handleArrangeSchedule(scope.row)">安排排期</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
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

    <el-dialog title="课程详情" :visible.sync="detailDialogVisible" width="600px">
      <el-descriptions :column="2" border v-if="currentCourse">
        <el-descriptions-item label="课程名称">{{ currentCourse.courseName }}</el-descriptions-item>
        <el-descriptions-item label="课程类型">{{ currentCourse.courseType }}</el-descriptions-item>
        <el-descriptions-item label="课程时长">{{ currentCourse.durationMin }} 分钟</el-descriptions-item>
        <el-descriptions-item label="价格">{{ currentCourse.price }}</el-descriptions-item>
        <el-descriptions-item label="最大容量">{{ currentCourse.maxCapacity }}</el-descriptions-item>
        <el-descriptions-item label="已预约">{{ currentCourse.currentCapacity }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ currentCourse.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ currentCourse.endTime }}</el-descriptions-item>
        <el-descriptions-item label="教室">{{ currentCourse.room }}</el-descriptions-item>
        <el-descriptions-item label="课程描述">{{ currentCourse.description || '无' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 添加课程对话框 -->
    <el-dialog title="添加课程" :visible.sync="addDialogVisible" width="650px">
      <el-form ref="addForm" :model="addFormData" :rules="addFormRules" label-width="100px">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="addFormData.courseName" placeholder="请输入课程名称"></el-input>
        </el-form-item>
        <el-form-item label="课程分类" prop="categoryId">
          <el-select v-model="addFormData.categoryId" placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in categoryList"
              :key="item.categoryId"
              :label="item.categoryName"
              :value="item.categoryId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="课程类型" prop="courseType">
          <el-select v-model="addFormData.courseType" placeholder="请选择" style="width: 100%">
            <el-option label="团课" value="团课"></el-option>
            <el-option label="私教课" value="私教课"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="教练" prop="coachId">
          <el-select v-model="addFormData.coachId" placeholder="请选择教练" style="width: 100%" filterable>
            <el-option
              v-for="item in coachList"
              :key="item.coachId"
              :label="item.coachName"
              :value="item.coachId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="课程日期" prop="courseDate">
          <el-date-picker
            v-model="addFormData.courseDate"
            type="date"
            placeholder="选择课程日期"
            style="width: 100%"
            value-format="yyyy-MM-dd"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-time-picker
            v-model="addFormData.startTime"
            placeholder="选择开始时间"
            style="width: 100%"
            format="HH:mm"
            value-format="HH:mm:ss"
          ></el-time-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-time-picker
            v-model="addFormData.endTime"
            placeholder="选择结束时间"
            style="width: 100%"
            format="HH:mm"
            value-format="HH:mm:ss"
          ></el-time-picker>
        </el-form-item>
        <el-form-item label="教室" prop="room">
          <el-input v-model="addFormData.room" placeholder="请输入教室"></el-input>
        </el-form-item>
        <el-form-item label="最大容量" prop="maxCapacity">
          <el-input-number v-model="addFormData.maxCapacity" :min="1" :max="100" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="课程价格" prop="price">
          <el-input-number v-model="addFormData.price" :min="0" :precision="2" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input v-model="addFormData.description" type="textarea" :rows="3" placeholder="请输入课程描述"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitAdd">确定</el-button>
      </div>
    </el-dialog>

    <!-- 安排排期对话框 -->
    <el-dialog title="安排排期" :visible.sync="scheduleDialogVisible" width="500px">
      <el-form ref="scheduleForm" :model="scheduleFormData" :rules="scheduleFormRules" label-width="100px">
        <el-form-item label="课程">
          <span>{{ scheduleFormData.courseName }}</span>
        </el-form-item>
        <el-form-item label="教练">
          <span>{{ scheduleFormData.coachName }}</span>
        </el-form-item>
        <el-form-item label="排期日期" prop="scheduleDate">
          <el-date-picker
            v-model="scheduleFormData.scheduleDate"
            type="date"
            placeholder="选择排期日期"
            style="width: 100%"
            value-format="yyyy-MM-dd"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-time-picker
            v-model="scheduleFormData.startTime"
            placeholder="选择开始时间"
            style="width: 100%"
            format="HH:mm"
            value-format="HH:mm:ss"
          ></el-time-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-time-picker
            v-model="scheduleFormData.endTime"
            placeholder="选择结束时间"
            style="width: 100%"
            format="HH:mm"
            value-format="HH:mm:ss"
          ></el-time-picker>
        </el-form-item>
        <el-form-item label="地点" prop="location">
          <el-input v-model="scheduleFormData.location" placeholder="请输入上课地点"></el-input>
        </el-form-item>
        <el-form-item label="最大容量" prop="maxCapacity">
          <el-input-number v-model="scheduleFormData.maxCapacity" :min="1" :max="100" style="width: 100%"></el-input-number>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="scheduleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitSchedule">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="管理预约" :visible.sync="bookingDialogVisible" width="600px">
      <div v-if="currentCourse">
        <h4>课程: {{ currentCourse.courseName }}</h4>
        <p>已预约会员: {{ currentCourse.currentCapacity }} / {{ currentCourse.maxCapacity }}</p>
      </div>
      <el-table :data="bookingList" border style="width: 100%; margin-top: 20px;">
        <el-table-column prop="bookingId" label="预约 ID" width="80"></el-table-column>
        <el-table-column prop="memberId" label="会员 ID" width="100"></el-table-column>
        <el-table-column prop="memberName" label="会员姓名" width="120"></el-table-column>
        <el-table-column prop="bookingTime" label="预约时间" width="160"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '已确认' : '已取消' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer">
        <el-button @click="bookingDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getCoursePage, getCourseBookings, addCourse } from '@/api/course'
import { getAllCategories } from '@/api/courseCategory'
import { getCoachList } from '@/api/coach'
import { addCoachSchedule } from '@/api/coachSchedule'

export default {
  name: 'MyCourse',
  data() {
    return {
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      detailDialogVisible: false,
      bookingDialogVisible: false,
      currentCourse: null,
      bookingList: [],
      addDialogVisible: false,
      categoryList: [],
      coachList: [],
      addFormData: {
        courseName: '',
        categoryId: null,
        courseType: '',
        coachId: null,
        courseDate: '',
        startTime: '',
        endTime: '',
        room: '',
        maxCapacity: 20,
        price: 0,
        description: ''
      },
      addFormRules: {
        courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
        categoryId: [{ required: true, message: '请选择课程分类', trigger: 'change' }],
        courseType: [{ required: true, message: '请选择课程类型', trigger: 'change' }],
        coachId: [{ required: true, message: '请选择教练', trigger: 'change' }],
        courseDate: [{ required: true, message: '请选择课程日期', trigger: 'change' }],
        startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
        endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
        room: [{ required: true, message: '请输入教室', trigger: 'blur' }],
        maxCapacity: [{ required: true, message: '请输入最大容量', trigger: 'blur' }]
      },
      scheduleDialogVisible: false,
      scheduleFormData: {
        courseId: null,
        courseName: '',
        coachId: null,
        coachName: '',
        scheduleDate: '',
        startTime: '',
        endTime: '',
        location: '',
        maxCapacity: 20,
        scheduleType: '团课'
      },
      scheduleFormRules: {
        scheduleDate: [{ required: true, message: '请选择排期日期', trigger: 'change' }],
        startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
        endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
        location: [{ required: true, message: '请输入上课地点', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
    this.getCategoryList()
    this.getCoachList()
  },
  methods: {
    getList() {
      this.loading = true
      getCoursePage({
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }).then(response => {
        this.tableData = response.data.records
        this.total = response.data.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.getList()
    },
    handleView(row) {
      this.currentCourse = row
      this.detailDialogVisible = true
    },
    handleManageBooking(row) {
      this.currentCourse = row
      this.bookingList = []
      getCourseBookings(row.courseId).then(response => {
        this.bookingList = response.data || []
      }).catch(() => {
        this.bookingList = []
      })
      this.bookingDialogVisible = true
    },
    getStatusType(status) {
      const map = { 0: '', 1: 'danger', 2: 'warning' }
      return map[status] || ''
    },
    getStatusText(status) {
      const map = { 0: '正常', 1: '已取消', 2: '已满员' }
      return map[status] || '未知'
    },
    getCategoryList() {
      getAllCategories().then(response => {
        this.categoryList = response.data || []
      }).catch(() => {
        this.categoryList = []
      })
    },
    getCoachList() {
      getCoachList().then(response => {
        this.coachList = response.data || []
      }).catch(() => {
        this.coachList = []
      })
    },
    handleAddCourse() {
      this.addFormData = {
        courseName: '',
        categoryId: null,
        courseType: '',
        coachId: null,
        courseDate: '',
        startTime: '',
        endTime: '',
        room: '',
        maxCapacity: 20,
        price: 0,
        description: ''
      }
      this.addDialogVisible = true
      this.$nextTick(() => {
        this.$refs.addForm && this.$refs.addForm.clearValidate()
      })
    },
    handleSubmitAdd() {
      this.$refs.addForm.validate(valid => {
        if (valid) {
          const startDateTime = this.addFormData.courseDate + ' ' + this.addFormData.startTime
          const endDateTime = this.addFormData.courseDate + ' ' + this.addFormData.endTime
          const submitData = {
            ...this.addFormData,
            startTime: startDateTime,
            endTime: endDateTime,
            durationMin: this.calculateDuration(this.addFormData.startTime, this.addFormData.endTime)
          }
          addCourse(submitData).then(() => {
            this.$message.success('添加课程成功')
            this.addDialogVisible = false
            this.getList()
          }).catch(err => {
            this.$message.error(err.message || '添加课程失败')
          })
        }
      })
    },
    calculateDuration(startTime, endTime) {
      if (!startTime || !endTime) return 0
      const [startHour, startMin] = startTime.split(':').map(Number)
      const [endHour, endMin] = endTime.split(':').map(Number)
      return (endHour * 60 + endMin) - (startHour * 60 + startMin)
    },
    handleArrangeSchedule(row) {
      this.scheduleFormData = {
        courseId: row.courseId,
        courseName: row.courseName,
        coachId: row.coachId,
        coachName: row.coachName || '',
        scheduleDate: '',
        startTime: '',
        endTime: '',
        location: row.room || '',
        maxCapacity: row.maxCapacity || 20,
        scheduleType: row.courseType || '团课'
      }
      this.scheduleDialogVisible = true
      this.$nextTick(() => {
        this.$refs.scheduleForm && this.$refs.scheduleForm.clearValidate()
      })
    },
    handleSubmitSchedule() {
      this.$refs.scheduleForm.validate(valid => {
        if (valid) {
          addCoachSchedule(this.scheduleFormData).then(() => {
            this.$message.success('安排排期成功')
            this.scheduleDialogVisible = false
          }).catch(err => {
            this.$message.error(err.message || '安排排期失败')
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.my-course-container {
  .header {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    h2 {
      margin: 0;
    }
  }

  .pagination {
    margin-top: 20px;
    text-align: right;
  }
}
</style>
