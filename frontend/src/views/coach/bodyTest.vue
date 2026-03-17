<template>
  <div class="coach-bodytest-container">
    <el-card>
      <div class="header">
        <h2>录入体测数据</h2>
      </div>

      <el-form ref="dataForm" :model="formData" :rules="rules" label-width="120px">
        <el-form-item label="会员" prop="memberId">
          <el-select
            v-model="formData.memberId"
            placeholder="请选择会员"
            filterable
            @change="handleMemberChange"
          >
            <el-option
              v-for="member in memberList"
              :key="member.memberId"
              :label="member.memberName + ' - ' + member.phoneNum"
              :value="member.memberId"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="身高(cm)" prop="height">
              <el-input-number v-model="formData.height" :min="100" :max="250" :precision="1"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="体重(kg)" prop="weight">
              <el-input-number v-model="formData.weight" :min="30" :max="200" :precision="1"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="体脂率(%)" prop="bodyFat">
              <el-input-number v-model="formData.bodyFat" :min="5" :max="60" :precision="1"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="肌肉量(kg)" prop="muscleMass">
              <el-input-number v-model="formData.muscleMass" :min="10" :max="150" :precision="1"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="基础代谢(kcal)" prop="basalMetabolism">
              <el-input-number v-model="formData.basalMetabolism" :min="500" :max="3000"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="水分(%)" prop="waterContent">
              <el-input-number v-model="formData.waterContent" :min="30" :max="80" :precision="1"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="胸围(cm)" prop="chestCircumference">
              <el-input-number v-model="formData.chestCircumference" :min="50" :max="150" :precision="1"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="腰围(cm)" prop="waistCircumference">
              <el-input-number v-model="formData.waistCircumference" :min="40" :max="150" :precision="1"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="臀围(cm)" prop="hipCircumference">
              <el-input-number v-model="formData.hipCircumference" :min="50" :max="150" :precision="1"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="测试日期" prop="testDate">
          <el-date-picker
            v-model="formData.testDate"
            type="date"
            placeholder="选择测试日期"
            value-format="yyyy-MM-dd"
          ></el-date-picker>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit">保存</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { addBodyTest, getCoachMembersList } from '@/api/coachDashboard'

export default {
  name: 'CoachBodyTest',
  data() {
    return {
      memberList: [],
      formData: {
        memberId: null,
        height: 170,
        weight: 70,
        bodyFat: 20,
        muscleMass: 30,
        basalMetabolism: 1500,
        waterContent: 55,
        chestCircumference: 90,
        waistCircumference: 75,
        hipCircumference: 95,
        testDate: '',
        remark: ''
      },
      rules: {
        memberId: [{ required: true, message: '请选择会员', trigger: 'change' }],
        height: [{ required: true, message: '请输入身高', trigger: 'blur' }],
        weight: [{ required: true, message: '请输入体重', trigger: 'blur' }],
        testDate: [{ required: true, message: '请选择测试日期', trigger: 'change' }]
      }
    }
  },
  created() {
    const memberId = this.$route.query.memberId
    const memberName = this.$route.query.memberName
    if (memberId) {
      this.formData.memberId = memberId
    }
    // 设置默认日期为今天
    this.formData.testDate = new Date().toISOString().split('T')[0]
    this.fetchMembers(memberId, memberName)
  },
  methods: {
    fetchMembers(memberId, memberName) {
      getCoachMembersList().then(res => {
        this.memberList = (res && res.data) ? res.data : []
        // 如果从“我的学员”跳转过来，且下拉数据里没有该学员，也允许继续录入（不阻断、不白屏）
        if (memberId && memberName) {
          const exist = this.memberList.some(m => String(m.memberId) === String(memberId))
          if (!exist) {
            this.memberList.unshift({ memberId: Number(memberId), memberName, phoneNum: '' })
          }
        }
      }).catch(() => {
        this.memberList = []
      })
    },
    handleMemberChange(memberId) {
      // 会员选择变化时的处理
    },
    handleSubmit() {
      this.$refs.dataForm.validate(valid => {
        if (valid) {
          addBodyTest(this.formData).then(() => {
            this.$message.success('体测数据保存成功')
            this.handleReset()
          }).catch(err => {
            this.$message.error('保存失败：' + err.message)
          })
        }
      })
    },
    handleReset() {
      this.formData = {
        memberId: null,
        height: 170,
        weight: 70,
        bodyFat: 20,
        muscleMass: 30,
        basalMetabolism: 1500,
        waterContent: 55,
        chestCircumference: 90,
        waistCircumference: 75,
        hipCircumference: 95,
        testDate: new Date().toISOString().split('T')[0],
        remark: ''
      }
      this.$refs.dataForm.clearValidate()
    }
  }
}
</script>

<style lang="scss" scoped>
.coach-bodytest-container {
  .header {
    margin-bottom: 20px;
    h2 {
      margin: 0;
    }
  }
}
</style>
