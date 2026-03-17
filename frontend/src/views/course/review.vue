<template>
  <div class="review-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="课程名称">
            <el-input v-model="queryForm.courseName" placeholder="请输入课程名称" clearable></el-input>
          </el-form-item>
          <el-form-item label="评分">
            <el-select v-model="queryForm.rating" placeholder="请选择" clearable>
              <el-option label="5星" :value="5"></el-option>
              <el-option label="4星" :value="4"></el-option>
              <el-option label="3星" :value="3"></el-option>
              <el-option label="2星" :value="2"></el-option>
              <el-option label="1星" :value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryForm.status" placeholder="请选择" clearable>
              <el-option label="隐藏" :value="0"></el-option>
              <el-option label="显示" :value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增评价</el-button>
      </div>

      <div class="table-wrapper">
        <el-table :data="tableData" border v-loading="loading">
          <el-table-column prop="reviewId" label="评价ID" width="80"></el-table-column>
          <el-table-column prop="courseName" label="课程名称" width="150"></el-table-column>
          <el-table-column prop="memberName" label="会员姓名" width="100"></el-table-column>
          <el-table-column prop="coachName" label="教练" width="100"></el-table-column>
          <el-table-column prop="rating" label="评分" width="100">
            <template slot-scope="scope">
              <el-rate v-model="scope.row.rating" disabled :max="5"></el-rate>
            </template>
          </el-table-column>
          <el-table-column prop="content" label="评价内容" min-width="200"></el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                {{ scope.row.status === 1 ? '显示' : '隐藏' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reply" label="回复" min-width="150">
            <template slot-scope="scope">
              <span v-if="scope.row.reply">{{ scope.row.reply }}</span>
              <span v-else class="no-reply">未回复</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="评价时间" width="160"></el-table-column>
          <el-table-column label="操作" fixed="right" width="200">
            <template slot-scope="scope">
              <el-button size="mini" @click="handleReply(scope.row)">回复</el-button>
              <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

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

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form ref="dataForm" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="课程ID" prop="courseId">
          <el-input v-model.number="formData.courseId" placeholder="请输入课程ID"></el-input>
        </el-form-item>
        <el-form-item label="会员ID" prop="memberId">
          <el-input v-model.number="formData.memberId" placeholder="请输入会员ID"></el-input>
        </el-form-item>
        <el-form-item label="教练ID" prop="coachId">
          <el-input v-model.number="formData.coachId" placeholder="请输入教练ID"></el-input>
        </el-form-item>
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="formData.rating" :max="5" show-text :texts="['1星', '2星', '3星', '4星', '5星']"></el-rate>
        </el-form-item>
        <el-form-item label="评价内容" prop="content">
          <el-input v-model="formData.content" type="textarea" :rows="4" placeholder="请输入评价内容"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">显示</el-radio>
            <el-radio :label="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="回复评价" :visible.sync="replyDialogVisible" width="500px">
      <el-form ref="replyForm" :model="replyForm" label-width="80px">
        <el-form-item label="回复内容">
          <el-input v-model="replyForm.reply" type="textarea" :rows="4" placeholder="请输入回复内容"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReply">确定回复</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getReviewPage, addReview, updateReview, deleteReview, replyReview } from '@/api/courseReview'

export default {
  name: 'CourseReview',
  data() {
    return {
      queryForm: {
        courseName: '',
        rating: null,
        status: null
      },
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      replyDialogVisible: false,
      dialogTitle: '',
      formData: {
        reviewId: null,
        courseId: null,
        memberId: null,
        coachId: null,
        rating: 5,
        content: '',
        status: 1
      },
      replyForm: {
        reviewId: null,
        reply: ''
      },
      rules: {
        courseId: [{ required: true, message: '请输入课程ID', trigger: 'blur' }],
        memberId: [{ required: true, message: '请输入会员ID', trigger: 'blur' }],
        rating: [{ required: true, message: '请选择评分', trigger: 'change' }],
        content: [{ required: true, message: '请输入评价内容', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getReviewPage({
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        ...this.queryForm
      }).then(response => {
        this.tableData = response.data.records
        this.total = response.data.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.pageNum = 1
      this.getList()
    },
    handleReset() {
      this.queryForm = {
        courseName: '',
        rating: null,
        status: null
      }
      this.handleQuery()
    },
    handleAdd() {
      this.dialogTitle = '新增评价'
      this.formData = {
        reviewId: null,
        courseId: null,
        memberId: null,
        coachId: null,
        rating: 5,
        content: '',
        status: 1
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑评价'
      this.formData = { ...row }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定要删除该评价吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteReview(row.reviewId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    handleReply(row) {
      this.replyForm = {
        reviewId: row.reviewId,
        reply: row.reply || ''
      }
      this.replyDialogVisible = true
    },
    handleSubmitReply() {
      if (!this.replyForm.reply || !this.replyForm.reply.trim()) {
        this.$message.warning('请输入回复内容')
        return
      }
      replyReview(this.replyForm.reviewId, this.replyForm.reply).then(() => {
        this.$message.success('回复成功')
        this.replyDialogVisible = false
        this.getList()
      })
    },
    handleSubmit() {
      this.$refs.dataForm.validate(valid => {
        if (valid) {
          const api = this.formData.reviewId ? updateReview : addReview
          api(this.formData).then(() => {
            this.$message.success(this.formData.reviewId ? '更新成功' : '添加成功')
            this.dialogVisible = false
            this.getList()
          })
        }
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.getList()
    }
  }
}
</script>

<style lang="scss" scoped>
.review-container {
  .toolbar, .table-toolbar { margin-bottom: 20px; }
  .table-wrapper { width: 100%; overflow-x: auto; }
  .pagination { margin-top: 20px; text-align: right; }
  .no-reply { color: #909399; font-style: italic; }
}
</style>
