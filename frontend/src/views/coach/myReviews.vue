<template>
  <div class="coach-reviews-container">
    <el-card>
      <div class="header">
        <h2>课程评价</h2>
      </div>

      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :inline="true">
          <el-form-item label="课程">
            <el-select v-model="queryForm.courseId" placeholder="全部课程" clearable>
              <el-option
                v-for="course in courseList"
                :key="course.courseId"
                :label="course.courseName"
                :value="course.courseId"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="bookingId" label="预约ID" width="80"></el-table-column>
        <el-table-column prop="courseName" label="课程" width="150"></el-table-column>
        <el-table-column prop="memberName" label="学员" width="100"></el-table-column>
        <el-table-column prop="classTime" label="上课时间" width="160"></el-table-column>
        <el-table-column prop="rating" label="评分" width="100">
          <template slot-scope="scope">
            <el-rate
              v-model="scope.row.rating"
              disabled
              show-score
              text-color="#ff9900"
            ></el-rate>
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="评价内容"></el-table-column>
        <el-table-column prop="createTime" label="评价时间" width="160"></el-table-column>
        <el-table-column label="操作" fixed="right" width="120">
          <template slot-scope="scope">
            <el-button
              v-if="!scope.row.rating"
              size="mini"
              type="primary"
              @click="handleAddReview(scope.row)"
            >评价</el-button>
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

    <!-- 评价对话框 -->
    <el-dialog title="课程评价" :visible.sync="dialogVisible" width="500px">
      <el-form ref="reviewForm" :model="reviewData" :rules="reviewRules" label-width="80px">
        <el-form-item label="学员" prop="memberName">
          <span>{{ reviewData.memberName }}</span>
        </el-form-item>
        <el-form-item label="课程" prop="courseName">
          <span>{{ reviewData.courseName }}</span>
        </el-form-item>
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="reviewData.rating" :colors="['#99A9BF', '#F7BA2A', '#FF9900']"></el-rate>
        </el-form-item>
        <el-form-item label="评价" prop="comment">
          <el-input
            v-model="reviewData.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入评价内容"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReview">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'CoachMyReviews',
  data() {
    return {
      queryForm: {
        courseId: null
      },
      courseList: [],
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      reviewData: {
        bookingId: null,
        memberName: '',
        courseName: '',
        rating: 5,
        comment: ''
      },
      reviewRules: {
        rating: [{ required: true, message: '请选择评分', trigger: 'change' }],
        comment: [{ required: true, message: '请输入评价内容', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      // 模拟数据
      this.loading = false
      this.tableData = [
        {
          bookingId: 1,
          courseName: '减脂训练',
          memberName: '张三',
          classTime: '2026-03-10 14:00',
          rating: 5,
          comment: '教练很专业，课程安排合理',
          createTime: '2026-03-10 16:00'
        },
        {
          bookingId: 2,
          courseName: '力量训练',
          memberName: '李四',
          classTime: '2026-03-11 10:00',
          rating: null,
          comment: '',
          createTime: ''
        }
      ]
      this.total = this.tableData.length
    },
    handleQuery() {
      this.pageNum = 1
      this.getList()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.getList()
    },
    handleAddReview(row) {
      this.reviewData = {
        bookingId: row.bookingId,
        memberName: row.memberName,
        courseName: row.courseName,
        rating: 5,
        comment: ''
      }
      this.dialogVisible = true
    },
    handleSubmitReview() {
      this.$refs.reviewForm.validate(valid => {
        if (valid) {
          // 调用 API 保存评价
          this.$message.success('评价提交成功')
          this.dialogVisible = false
          this.getList()
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.coach-reviews-container {
  .header {
    margin-bottom: 20px;
    h2 {
      margin: 0;
    }
  }

  .search-area {
    margin-bottom: 20px;
  }

  .pagination {
    margin-top: 20px;
    text-align: right;
  }
}
</style>
