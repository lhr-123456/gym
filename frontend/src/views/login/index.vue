<template>
  <div class="login-container">
    <!-- 背景动画 -->
    <div class="bg-animation">
      <div class="circle circle1"></div>
      <div class="circle circle2"></div>
      <div class="circle circle3"></div>
    </div>

    <div class="login-box">
      <div class="login-right">
        <div class="form-container">
          <!-- 切换标签 -->
          <div class="tabs">
            <div
              class="tab-item"
              :class="{ active: activeTab === 'login' }"
              @click="activeTab = 'login'"
            >
              登录
            </div>
            <div
              class="tab-item"
              :class="{ active: activeTab === 'register' }"
              @click="activeTab = 'register'"
            >
              注册
            </div>
          </div>

          <!-- 登录表单 -->
          <el-form
            v-show="activeTab === 'login'"
            ref="loginForm"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
            auto-complete="on"
            label-position="top"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                prefix-icon="el-icon-user"
                size="large"
              ></el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="el-icon-lock"
                size="large"
                show-password
                @keyup.enter.native="handleLogin"
              ></el-input>
            </el-form-item>

            <el-form-item prop="userType">
              <el-radio-group v-model="loginForm.userType" class="user-type-group">
                <el-radio-button :label="1">
                  <i class="el-icon-s-custom"></i> 管理员
                </el-radio-button>
                <el-radio-button :label="2">
                  <i class="el-icon-user"></i> 教练
                </el-radio-button>
                <el-radio-button :label="3">
                  <i class="el-icon-sunny"></i> 会员
                </el-radio-button>
              </el-radio-group>
            </el-form-item>

            <el-button
              :loading="loading"
              type="primary"
              size="large"
              class="login-btn"
              @click.native.prevent="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form>

          <!-- 注册表单 -->
          <el-form
            v-show="activeTab === 'register'"
            ref="registerForm"
            :model="registerForm"
            :rules="registerRules"
            class="login-form"
            auto-complete="on"
            label-position="top"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                prefix-icon="el-icon-user"
                size="large"
              ></el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="el-icon-lock"
                size="large"
                show-password
              ></el-input>
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                prefix-icon="el-icon-lock"
                size="large"
                show-password
              ></el-input>
            </el-form-item>

            <el-form-item prop="userType">
              <el-radio-group v-model="registerForm.userType" class="user-type-group">
                <el-radio-button :label="2">
                  <i class="el-icon-user"></i> 教练
                </el-radio-button>
                <el-radio-button :label="3">
                  <i class="el-icon-sunny"></i> 会员
                </el-radio-button>
              </el-radio-group>
            </el-form-item>

            <el-button
              :loading="registerLoading"
              type="primary"
              size="large"
              class="login-btn"
              @click.native.prevent="handleRegister"
            >
              {{ registerLoading ? '注册中...' : '注 册' }}
            </el-button>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/user'

export default {
  name: 'Login',
  data() {
    // 验证确认密码
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    return {
      activeTab: 'login',
      loginForm: {
        username: '',
        password: '',
        userType: 1
      },
      loginRules: {
        username: [{ required: true, trigger: 'blur', message: '请输入用户名' }],
        password: [{ required: true, trigger: 'blur', message: '请输入密码' }],
        userType: [{ required: true, trigger: 'change', message: '请选择用户类型' }]
      },
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        userType: 3
      },
      registerRules: {
        username: [
          { required: true, trigger: 'blur', message: '请输入用户名' },
          { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, trigger: 'blur', message: '请输入密码' },
          { min: 6, message: '密码长度至少6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validateConfirmPassword, trigger: 'blur' }
        ],
        userType: [{ required: true, trigger: 'change', message: '请选择用户类型' }]
      },
      loading: false,
      registerLoading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store
            .dispatch('user/login', this.loginForm)
            .then(() => {
              this.$router.push({ path: '/dashboard/index' })
              this.loading = false
            })
            .catch((err) => {
              this.$message.error(err.message || '登录失败')
              this.loading = false
            })
        }
      })
    },
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.registerLoading = true
          register({
            username: this.registerForm.username,
            password: this.registerForm.password,
            userType: this.registerForm.userType
          })
            .then(res => {
              if (res.code === 200) {
                this.$message.success('注册成功，请登录')
                this.activeTab = 'login'
                this.loginForm.username = this.registerForm.username
              } else {
                this.$message.error(res.message || '注册失败')
              }
            })
            .catch(err => {
              this.$message.error(err.message || '注册失败')
            })
            .finally(() => {
              this.registerLoading = false
            })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.6)),
              url('~@/assets/images/gym1.jpg') no-repeat center center;
  background-size: cover;
  background-position: center;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;

  // 背景动画圆圈
  .bg-animation {
    position: absolute;
    width: 100%;
    height: 100%;
    top: 0;
    left: 0;
    overflow: hidden;

    .circle {
      position: absolute;
      border-radius: 50%;
      opacity: 0.1;
    }

    .circle1 {
      width: 600px;
      height: 600px;
      background: linear-gradient(45deg, #667eea, #764ba2);
      top: -200px;
      left: -200px;
      animation: float 15s ease-in-out infinite;
    }

    .circle2 {
      width: 400px;
      height: 400px;
      background: linear-gradient(45deg, #f093fb, #f5576c);
      bottom: -100px;
      right: -100px;
      animation: float 12s ease-in-out infinite reverse;
    }

    .circle3 {
      width: 300px;
      height: 300px;
      background: linear-gradient(45deg, #4facfe, #00f2fe);
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      animation: pulse 10s ease-in-out infinite;
    }
  }

  @keyframes float {
    0%, 100% {
      transform: translate(0, 0) rotate(0deg);
    }
    50% {
      transform: translate(30px, 30px) rotate(180deg);
    }
  }

  @keyframes pulse {
    0%, 100% {
      transform: translate(-50%, -50%) scale(1);
    }
    50% {
      transform: translate(-50%, -50%) scale(1.2);
    }
  }

  .login-box {
    position: relative;
    z-index: 1;
    width: 420px;
    padding: 40px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 24px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
    display: flex;
    justify-content: center;

    .login-right {
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0;

      .form-container {
        width: 100%;

        .tabs {
          display: flex;
          margin-bottom: 40px;
          border-bottom: 2px solid #eee;

          .tab-item {
            flex: 1;
            text-align: center;
            padding-bottom: 15px;
            font-size: 18px;
            color: #999;
            cursor: pointer;
            transition: all 0.3s;
            position: relative;

            &::after {
              content: '';
              position: absolute;
              bottom: -2px;
              left: 0;
              width: 100%;
              height: 2px;
              background: linear-gradient(90deg, #667eea, #764ba2);
              transform: scaleX(0);
              transition: transform 0.3s;
            }

            &.active {
              color: #667eea;
              font-weight: bold;

              &::after {
                transform: scaleX(1);
              }
            }

            &:hover {
              color: #667eea;
            }
          }
        }

        .login-form {
          .user-type-group {
            width: 100%;
            display: flex;
            justify-content: space-between;

            ::v-deep .el-radio-button {
              flex: 1;

              .el-radio-button__inner {
                width: 100%;
                padding: 10px 5px;
                font-size: 12px;
              }
            }
          }

          .login-btn {
            width: 100%;
            margin-top: 20px;
            height: 48px;
            font-size: 16px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 8px;
            transition: all 0.3s;

            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
            }
          }
        }
      }
    }
  }
}

// 响应式
@media (max-width: 960px) {
  .login-container {
    .login-box {
      width: 90%;
      padding: 20px;

      .login-right {
        padding: 0;
      }
    }
  }
}
</style>
