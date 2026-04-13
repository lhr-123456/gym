<template>
  <div class="member-profile-container">
    <el-row :gutter="20">
      <!-- 左侧：个人资料卡片 -->
      <el-col :span="8">
        <el-card class="profile-card">
          <div class="avatar-section">
            <el-avatar :size="100" :src="avatarUrl" class="member-avatar">
              {{ username ? username.charAt(0).toUpperCase() : 'M' }}
            </el-avatar>
            <el-upload
              class="avatar-upload"
              action="#"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="handleAvatarUpload"
            >
              <el-button size="small" type="text">更换头像</el-button>
            </el-upload>
          </div>
          <div class="profile-info">
            <h3>{{ username }}</h3>
            <el-tag :type="memberLevelType">{{ memberLevel }}</el-tag>
            <div class="info-item">
              <i class="el-icon-user"></i>
              <span>会员ID: {{ userId }}</span>
            </div>
            <div class="info-item">
              <i class="el-icon-phone"></i>
              <span>{{ profileForm.phone || '未设置' }}</span>
            </div>
            <div class="info-item">
              <i class="el-icon-message"></i>
              <span>{{ profileForm.email || '未设置' }}</span>
            </div>
          </div>
        </el-card>

        <!-- 会员卡信息 -->
        <el-card class="card-info" v-loading="cardLoading">
          <div slot="header" class="card-header">
            <span><i class="el-icon-credit-card"></i> 会员卡信息</span>
          </div>
          <div class="card-content" v-if="memberCard">
            <div class="card-no">卡号: {{ memberCard.cardNo }}</div>
            <div class="card-type">{{ memberCard.cardTypeName || memberCard.cardType }}</div>
            <div class="card-stats">
              <div class="stat-item">
                <div class="stat-value">{{ memberCard.remainingTimes || 0 }}</div>
                <div class="stat-label">剩余次数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">¥{{ memberCard.balance || 0 }}</div>
                <div class="stat-label">卡内余额</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ formatDate(memberCard.endDate) }}</div>
                <div class="stat-label">到期日期</div>
              </div>
            </div>
          </div>
          <div class="card-empty" v-else>
            <i class="el-icon-credit-card"></i>
            <p>暂无可用会员卡</p>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：详细信息 -->
      <el-col :span="16">
        <el-card>
          <el-tabs v-model="activeTab">
            <!-- 基本资料 -->
            <el-tab-pane label="基本资料" name="info">
              <el-form :model="profileForm" :rules="profileRules" ref="profileFormRef" label-width="100px" class="profile-form">
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="profileForm.username" disabled></el-input>
                </el-form-item>
                <el-form-item label="姓名" prop="realName">
                  <el-input v-model="profileForm.realName" placeholder="请输入姓名"></el-input>
                </el-form-item>
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="profileForm.gender">
                    <el-radio label="男">男</el-radio>
                    <el-radio label="女">女</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="profileForm.phone" placeholder="请输入手机号"></el-input>
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="profileForm.email" placeholder="请输入邮箱"></el-input>
                </el-form-item>
                <el-form-item label="生日" prop="birthday">
                  <el-date-picker
                    v-model="profileForm.birthday"
                    type="date"
                    placeholder="选择生日"
                    value-format="yyyy-MM-dd"
                    style="width: 100%"
                  ></el-date-picker>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleSaveProfile" :loading="saveLoading">保存修改</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 消费记录 -->
            <el-tab-pane label="消费记录" name="consumption">
              <div class="consumption-header">
                <el-select v-model="consumptionType" placeholder="筛选类型" clearable style="width: 150px">
                  <el-option label="全部" value=""></el-option>
                  <el-option label="课程消费" value="课程消费"></el-option>
                  <el-option label="商品购买" value="商品购买"></el-option>
                  <el-option label="会员卡" value="会员卡"></el-option>
                </el-select>
              </div>
              <el-table :data="consumptionList" border v-loading="consumptionLoading">
                <el-table-column prop="consumeTime" label="时间" width="180"></el-table-column>
                <el-table-column prop="consumeType" label="类型" width="100">
                  <template slot-scope="scope">
                    <el-tag size="small" :type="getConsumeTypeTag(scope.row.consumeType)">
                      {{ scope.row.consumeType }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="amount" label="金额" width="100">
                  <template slot-scope="scope">
                    <span class="amount">¥{{ scope.row.amount }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" min-width="150"></el-table-column>
              </el-table>
              <div class="pagination">
                <el-pagination
                  :current-page="consumptionPage"
                  :page-size="consumptionPageSize"
                  :total="consumptionTotal"
                  layout="total, prev, pager, next"
                  @current-change="handleConsumptionPageChange"
                ></el-pagination>
              </div>
            </el-tab-pane>

            <!-- 修改密码 -->
            <el-tab-pane label="修改密码" name="password">
              <el-form
                :model="passwordForm"
                :rules="passwordRules"
                ref="passwordFormRef"
                label-width="100px"
                class="password-form"
              >
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    placeholder="请输入原密码"
                    show-password
                  ></el-input>
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    placeholder="请输入新密码"
                    show-password
                  ></el-input>
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    placeholder="请再次输入新密码"
                    show-password
                  ></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handlePasswordSubmit" :loading="passwordLoading">修改密码</el-button>
                  <el-button @click="handlePasswordReset">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 设置 -->
            <el-tab-pane label="设置" name="settings">
              <div class="settings-section">
                <div class="settings-title">通知设置</div>
                <el-form label-width="150px">
                  <el-form-item label="课程提醒">
                    <el-switch v-model="settings.courseNotify" active-text="开启" inactive-text="关闭"></el-switch>
                  </el-form-item>
                  <el-form-item label="系统通知">
                    <el-switch v-model="settings.systemNotify" active-text="开启" inactive-text="关闭"></el-switch>
                  </el-form-item>
                  <el-form-item label="营销推送">
                    <el-switch v-model="settings.marketingNotify" active-text="开启" inactive-text="关闭"></el-switch>
                  </el-form-item>
                </el-form>
              </div>
              <div class="settings-section">
                <div class="settings-title">隐私设置</div>
                <el-form label-width="150px">
                  <el-form-item label="查看运动数据">
                    <el-switch v-model="settings.showData" active-text="所有人可见" inactive-text="仅自己"></el-switch>
                  </el-form-item>
                </el-form>
              </div>
              <div class="settings-footer">
                <el-button type="primary" @click="handleSaveSettings">保存设置</el-button>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getMemberCardByMember } from '@/api/memberCard'
import { getMemberConsumptionByMember } from '@/api/memberConsumption'
import { updateMember } from '@/api/member'
import { updatePassword } from '@/api/user'
import { uploadAvatar } from '@/api/member'
import { getUserInfo, setUserInfo } from '@/utils/auth'

export default {
  name: 'MemberProfile',
  data() {
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else {
        if (this.passwordForm.confirmPassword !== '') {
          this.$refs.passwordFormRef.validateField('confirmPassword')
        }
        callback()
      }
    }
    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    return {
      avatarUrl: '',
      memberLevel: '黄金会员',
      memberLevelType: 'warning',
      memberCard: null,
      cardLoading: false,
      activeTab: 'info',
      profileForm: {
        username: '',
        realName: '',
        gender: '',
        phone: '',
        email: '',
        birthday: ''
      },
      profileRules: {},
      saveLoading: false,
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
        newPassword: [
          { required: true, validator: validatePass, trigger: 'blur' },
          { min: 6, message: '密码长度至少6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validatePass2, trigger: 'blur' }
        ]
      },
      passwordLoading: false,
      consumptionType: '',
      consumptionList: [],
      consumptionLoading: false,
      consumptionPage: 1,
      consumptionPageSize: 10,
      consumptionTotal: 0,
      settings: {
        courseNotify: true,
        systemNotify: true,
        marketingNotify: false,
        showData: true
      }
    }
  },
  computed: {
    ...mapGetters(['username', 'userId', 'userType', 'avatar'])
  },
  watch: {
    avatar(val) {
      if (val) this.avatarUrl = val
    }
  },
  created() {
    this.avatarUrl = this.avatar || ''
    this.initData()
  },
  methods: {
    initData() {
      this.profileForm.username = this.username || ''
      this.fetchMemberCard()
      this.fetchConsumptionList()
    },
    fetchMemberCard() {
      if (!this.userId) return
      this.cardLoading = true
      getMemberCardByMember(this.userId).then(res => {
        if (res && res.code === 200 && res.data) {
          this.memberCard = res.data
        }
      }).catch(() => {}).finally(() => {
        this.cardLoading = false
      })
    },
    fetchConsumptionList() {
      if (!this.userId) return
      this.consumptionLoading = true
      getMemberConsumptionByMember(this.userId).then(res => {
        if (res && res.code === 200) {
          let data = res.data || []
          if (this.consumptionType) {
            data = data.filter(c => c.consumeType === this.consumptionType)
          }
          this.consumptionList = data
          this.consumptionTotal = data.length
        }
      }).catch(() => {
        this.consumptionList = []
        this.consumptionTotal = 0
      }).finally(() => {
        this.consumptionLoading = false
      })
    },
    handleSaveProfile() {
      this.$refs.profileFormRef.validate(valid => {
        if (!valid) return
        this.saveLoading = true
        // 字段映射：表单字段 → MemberInfo 实体字段
        const data = {
          memberId: this.userId,
          memberName: this.profileForm.realName,
          gender: this.profileForm.gender,
          phoneNum: this.profileForm.phone,
          emailAddr: this.profileForm.email,
          birthDate: this.profileForm.birthday || null
        }
        updateMember(data).then(res => {
          if (res.code === 200 || res.code === 0) {
            this.$message.success('保存成功')
          } else {
            this.$message.error(res.message || res.msg || '保存失败')
          }
        }).catch(err => {
          this.$message.error((err && err.message) ? err.message : '保存失败，请重试')
        }).finally(() => {
          this.saveLoading = false
        })
      })
    },
    handlePasswordSubmit() {
      this.$refs.passwordFormRef.validate(valid => {
        if (valid) {
          this.passwordLoading = true
          updatePassword({
            oldPassword: this.passwordForm.oldPassword,
            newPassword: this.passwordForm.newPassword
          }).then(() => {
            this.$message.success('密码修改成功')
            this.handlePasswordReset()
          }).catch(err => {
            this.$message.error(err.message || '密码修改失败')
          }).finally(() => {
            this.passwordLoading = false
          })
        }
      })
    },
    handlePasswordReset() {
      this.passwordForm = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      this.$refs.passwordFormRef && this.$refs.passwordFormRef.resetFields()
    },
    handleConsumptionPageChange(page) {
      this.consumptionPage = page
      this.fetchConsumptionList()
    },
    handleSaveSettings() {
      this.$message.success('设置保存成功')
    },
    getConsumeTypeTag(type) {
      const map = {
        '课程消费': 'primary',
        '商品购买': 'success',
        '会员卡': 'warning'
      }
      return map[type] || 'info'
    },
    formatDate(date) {
      if (!date) return '-'
      return date.toString().slice(0, 10)
    },
    handleAvatarUpload(option) {
      uploadAvatar(option.file).then(res => {
        if (res.code === 200 || res.code === '200') {
          const newAvatar = res.data
          this.avatarUrl = newAvatar
          this.$store.commit('user/SET_AVATAR', newAvatar)
          const stored = getUserInfo()
          if (stored) {
            setUserInfo({ ...stored, avatar: newAvatar })
          }
          this.$message.success('头像上传成功')
        } else {
          this.$message.error(res.message || '头像上传失败')
        }
      }).catch(err => {
        this.$message.error('头像上传失败：' + (err.message || '网络错误'))
      })
    },
    beforeAvatarUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isImage) {
        this.$message.error('只能上传图片文件')
        return false
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB')
        return false
      }
      return true
    }
  }
}
</script>

<style lang="scss" scoped>
.member-profile-container {
  .profile-card {
    margin-bottom: 20px;
    text-align: center;
    .avatar-section {
      padding: 20px 0;
      .member-avatar {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        font-size: 36px;
        overflow: hidden;
        flex-shrink: 0;

        /* 让任意比例的图片铺满圆形，避免竖图只贴一侧、露出紫色底 */
        ::v-deep img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          object-position: center;
          display: block;
        }
      }
      .avatar-upload {
        margin-top: 12px;
      }
    }
    .profile-info {
      padding: 0 20px 20px;
      h3 {
        margin: 0 0 12px 0;
        font-size: 20px;
      }
      .el-tag {
        margin-bottom: 16px;
      }
      .info-item {
        text-align: left;
        padding: 8px 0;
        color: #606266;
        font-size: 14px;
        i {
          margin-right: 8px;
          color: #909399;
        }
      }
    }
  }
  .card-info {
    background: rgba(255, 255, 255, 0.2) !important;
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    border: 1px solid rgba(255, 255, 255, 0.4) !important;
    box-shadow: 0 8px 32px rgba(30, 58, 138, 0.1);

    ::v-deep .el-card__header {
      background: rgba(255, 255, 255, 0.12) !important;
      border-bottom: 1px solid rgba(255, 255, 255, 0.25) !important;
    }

    ::v-deep .el-card__body { background: transparent !important; }

    .card-header {
      font-size: 15px;
      font-weight: bold;
      color: #1e3a8a;
      i { margin-right: 6px; color: #2563eb; }
    }
    .card-content {
      .card-no {
        font-size: 12px;
        color: #3b82f6;
        margin-bottom: 4px;
      }
      .card-type {
        font-size: 18px;
        font-weight: bold;
        color: #1e3a8a;
        margin-bottom: 16px;
      }
      .card-stats {
        display: flex;
        justify-content: space-between;
        .stat-item {
          text-align: center;
          .stat-value {
            font-size: 16px;
            font-weight: bold;
            color: #1e3a8a;
          }
          .stat-label {
            font-size: 12px;
            color: #2563eb;
            font-weight: 500;
          }
        }
      }
    }
    .card-empty {
      text-align: center;
      padding: 30px 0;
      color: #2563eb;
      i { font-size: 40px; margin-bottom: 10px; display: block; color: #2563eb; }
      p { margin: 0; color: #3b82f6; }
    }
  }
  .profile-form, .password-form {
    max-width: 500px;
    padding: 20px 0;
    ::v-deep .el-form-item__label { color: #1e3a8a; font-weight: 600; }
  }
  .consumption-header {
    margin-bottom: 16px;
  }
  .pagination {
    margin-top: 16px;
    text-align: right;
  }
  .amount {
    color: #2563eb;
    font-weight: bold;
  }
  .settings-section {
    margin-bottom: 30px;
    .settings-title {
      font-size: 16px;
      font-weight: bold;
      color: #1e3a8a;
      margin-bottom: 16px;
      padding-bottom: 8px;
      border-bottom: 1px solid rgba(37, 99, 235, 0.15);
    }
    ::v-deep .el-form-item__label { color: #2563eb; font-weight: 600; }
  }
  .settings-footer {
    padding-top: 20px;
    border-top: 1px solid rgba(37, 99, 235, 0.15);
  }
}
</style>
