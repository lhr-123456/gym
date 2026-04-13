<template>
  <div class="member-bodytest-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true">
          <el-form-item>
            <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增体测</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="testId" label="ID" width="80"></el-table-column>
        <el-table-column prop="memberId" label="会员ID" width="100"></el-table-column>
        <el-table-column prop="testDate" label="测试日期" width="120"></el-table-column>
        <el-table-column prop="height" label="身高(cm)" width="100"></el-table-column>
        <el-table-column prop="weight" label="体重(kg)" width="100"></el-table-column>
        <el-table-column prop="bmi" label="BMI" width="80"></el-table-column>
        <el-table-column prop="bodyFatRate" label="体脂率%" width="90"></el-table-column>
        <el-table-column prop="muscleMass" label="肌肉量kg" width="100"></el-table-column>
        <el-table-column prop="healthScore" label="健康评分" width="100">
          <template slot-scope="scope">
            <el-tag :type="getScoreType(scope.row.healthScore)">
              {{ scope.row.healthScore || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
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

      <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="700px">
        <el-form ref="form" :model="form" :rules="rules" label-width="120px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="会员ID">
                <span>{{ memberId }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="测试日期" prop="testDate">
                <el-date-picker v-model="form.testDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" style="width: 100%"></el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="身高(cm)" prop="height">
                <el-input-number v-model="form.height" :min="50" :max="250" :precision="1"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="体重(kg)" prop="weight">
                <el-input-number v-model="form.weight" :min="20" :max="300" :precision="1"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="体脂率%" prop="bodyFatRate">
                <el-input-number v-model="form.bodyFatRate" :min="0" :max="100" :precision="1"></el-input-number>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="肌肉量kg" prop="muscleMass">
                <el-input-number v-model="form.muscleMass" :min="0" :max="200" :precision="1"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="水分%" prop="waterContent">
                <el-input-number v-model="form.waterContent" :min="0" :max="100" :precision="1"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="内脏脂肪" prop="visceralFat">
                <el-input-number v-model="form.visceralFat" :min="0" :max="50" :precision="1"></el-input-number>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="腰围cm" prop="waist">
                <el-input-number v-model="form.waist" :min="0" :max="200" :precision="1"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="臀围cm" prop="hip">
                <el-input-number v-model="form.hip" :min="0" :max="200" :precision="1"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="心率" prop="heartRate">
                <el-input-number v-model="form.heartRate" :min="30" :max="200"></el-input-number>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="备注" prop="remarks">
            <el-input v-model="form.remarks" type="textarea" :rows="2"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { getMemberBodyTestPage, addMemberBodyTest, updateMemberBodyTest, deleteMemberBodyTest } from '@/api/memberBodyTest'
import { mapGetters } from 'vuex'

export default {
  name: 'MemberBodyTest',
  data() {
    return {
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '',
      form: {
        testId: null,
        memberId: null,
        testDate: '',
        height: null,
        weight: null,
        bmi: null,
        bodyFatRate: null,
        muscleMass: null,
        waterContent: null,
        visceralFat: null,
        waist: null,
        hip: null,
        heartRate: null,
        remarks: ''
      },
      rules: {
        testDate: [{ required: true, message: '请选择测试日期', trigger: 'change' }]
      }
    }
  },
  computed: {
    ...mapGetters(['memberId'])
  },
  watch: {
    memberId: {
      handler(newVal) {
        if (newVal) {
          this.fetchData()
        }
      },
      immediate: true
    }
  },
  created() {
    // 如果 memberId 已加载，直接获取数据
    if (this.memberId) {
      this.fetchData()
    }
  },
  methods: {
    fetchData() {
      if (!this.memberId) return
      this.loading = true
      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        memberId: this.memberId
      }
      getMemberBodyTestPage(params).then(res => {
        if (res.code === 200) {
          this.tableData = res.data.records || []
          this.total = res.data.total || 0
        }
      }).finally(() => {
        this.loading = false
      })
    },
    handleCancel() {
      this.dialogVisible = false
      this.$refs.form && this.$refs.form.resetFields()
    },
    handleAdd() {
      this.form = {
        testId: null,
        memberId: this.memberId,
        testDate: '',
        height: null,
        weight: null,
        bmi: null,
        bodyFatRate: null,
        muscleMass: null,
        waterContent: null,
        visceralFat: null,
        waist: null,
        hip: null,
        heartRate: null,
        remarks: ''
      }
      this.dialogTitle = '新增体测记录'
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.form = { ...row }
      this.dialogTitle = '编辑体测记录'
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定删除该体测记录吗？', '提示', {
        type: 'warning'
      }).then(() => {
        deleteMemberBodyTest(row.testId).then(res => {
          if (res.code === 200) {
            this.$message.success('删除成功')
            this.fetchData()
          }
        })
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          const action = this.form.testId ? updateMemberBodyTest : addMemberBodyTest
          action(this.form).then(res => {
            if (res.code === 200) {
              this.$message.success('操作成功')
              this.dialogVisible = false
              this.fetchData()
            }
          })
        }
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchData()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.fetchData()
    },
    getScoreType(score) {
      if (!score) return 'info'
      if (score >= 90) return 'success'
      if (score >= 70) return 'warning'
      return 'danger'
    }
  }
}
</script>

<style lang="scss" scoped>
.member-bodytest-container {
  .toolbar {
    margin-bottom: 20px;
  }
  .pagination {
    margin-top: 20px;
    text-align: right;
  }
}
</style>
