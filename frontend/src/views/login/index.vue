<template>
  <div class="login-container">
    <!-- 背景动画 -->
    <div class="bg-animation">
      <div class="spot spot1"></div>
      <div class="spot spot2"></div>
      <div class="spot spot3"></div>
    </div>

    <div class="login-box">
      <div class="form-container">
          <!-- 品牌标题 -->
          <div class="brand">
            <div class="brand-title">GYMSPACE</div>
            <div class="brand-sub">Fitness Management</div>
          </div>

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
              const ut = Number(this.loginForm.userType)
              let path = '/dashboard/index'
              if (ut === 2) path = '/coach-dashboard/index'
              else if (ut === 3) path = '/member/home'
              const redir = this.$route.query.redirect
              if (redir && typeof redir === 'string') {
                const r = decodeURIComponent(redir).replace(/^#\/?/, '/')
                // 教练/会员不允许通过 redirect 落到管理端首页
                if (ut === 1) {
                  path = r
                } else if (!/^\/dashboard(\/index)?$/.test(r) && !/^\/dashboard$/.test(r)) {
                  path = r
                }
              }
              this.$router.push({ path })
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
/* ============================================================
 * 海绵宝宝动漫风格 - 登录页面
 * ============================================================ */

/* 海绵宝宝主题色 */
$spongebob-yellow: #FFE873;
$spongebob-bright-yellow: #FFF176;
$spongebob-dark-yellow: #E6C84D;
$spongebob-brown: #8B4513;
$spongebob-dark-brown: #5D2E0C;
$spongebob-red: #FF3B30;
$spongebob-orange: #FF9500;
$spongebob-white: #FFFFFF;

$cartoon-border: 3px;
$cartoon-radius-sm: 12px;
$cartoon-radius-md: 16px;
$cartoon-radius-lg: 24px;
$cartoon-radius-xl: 32px;
$cartoon-transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);

.login-container {
  min-height: 100vh;
  width: 100%;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  background-color: #2C1810;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background-color: #2C1810;
    background-image: url('D:\\gympicture\\hmbb.png');
    background-repeat: no-repeat;
    background-position: center center;
    background-size: cover;
    z-index: 0;
  }

  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(
      160deg,
      rgba(44, 24, 16, 0.6) 0%,
      rgba(60, 40, 30, 0.5) 50%,
      rgba(30, 20, 15, 0.65) 100%
    );
    z-index: 0;
  }

  /* 悬浮光斑装饰 - 海绵宝宝风格 */
  .bg-animation {
    position: absolute;
    inset: 0;
    z-index: 0;
    overflow: hidden;
    pointer-events: none;

    .spot {
      position: absolute;
      border-radius: 50%;
      filter: blur(80px);

      &.spot1 {
        width: 500px;
        height: 500px;
        background: radial-gradient(circle, rgba(255, 232, 115, 0.4) 0%, transparent 70%);
        top: -150px;
        left: -100px;
        animation: drift 18s ease-in-out infinite;
      }

      &.spot2 {
        width: 400px;
        height: 400px;
        background: radial-gradient(circle, rgba(255, 107, 157, 0.35) 0%, transparent 70%);
        bottom: -120px;
        right: -80px;
        animation: drift 14s ease-in-out infinite reverse;
      }

      &.spot3 {
        width: 300px;
        height: 300px;
        background: radial-gradient(circle, rgba(255, 232, 115, 0.3) 0%, transparent 70%);
        top: 50%;
        left: 55%;
        transform: translate(-50%, -50%);
        animation: breathe 12s ease-in-out infinite;
      }
    }
  }

  @keyframes drift {
    0%, 100% { transform: translate(0, 0); }
    50%       { transform: translate(40px, 30px); }
  }

  @keyframes breathe {
    0%, 100% { transform: translate(-50%, -50%) scale(1);   opacity: 0.8; }
    50%       { transform: translate(-50%, -50%) scale(1.3); opacity: 1; }
  }

  /* 登录卡片 - 海绵宝宝风格 */
  .login-box {
    position: relative;
    z-index: 1;
    width: 440px;
    padding: 48px 44px;
    background: linear-gradient(145deg, $spongebob-yellow, $spongebob-dark-yellow);
    border: 4px solid $spongebob-brown;
    border-radius: $cartoon-radius-xl;
    box-shadow: 
      0 10px 0 $spongebob-dark-brown,
      0 15px 40px rgba(139, 69, 19, 0.5),
      0 0 40px rgba(255, 232, 115, 0.3),
      inset 0 4px 0 rgba(255, 255, 255, 0.4);
    animation: cardIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;

    /* 卡通高光效果 */
    &::before {
      content: '';
      position: absolute;
      top: 20px;
      left: 20px;
      right: 50%;
      height: 80px;
      background: linear-gradient(180deg, rgba(255, 255, 255, 0.4), transparent);
      border-radius: $cartoon-radius-md $cartoon-radius-md 50% 50%;
      pointer-events: none;
    }

    @keyframes cardIn {
      from { opacity: 0; transform: translateY(24px) scale(0.97); }
      to   { opacity: 1; transform: translateY(0) scale(1); }
    }

    .form-container {
      width: 100%;

      /* 标题区 */
      .brand {
        text-align: center;
        margin-bottom: 36px;

        .brand-title {
          font-size: 32px;
          font-weight: bold;
          color: $spongebob-brown;
          letter-spacing: 4px;
          margin: 0 0 8px;
          font-family: 'Comic Sans MS', 'Microsoft YaHei', cursive, sans-serif;
          text-shadow: 
            3px 3px 0 $spongebob-white,
            -1px -1px 0 rgba(139, 69, 19, 0.2);
        }

        .brand-sub {
          font-size: 14px;
          color: $spongebob-dark-brown;
          letter-spacing: 3px;
          font-family: 'Comic Sans MS', 'Microsoft YaHei', cursive, sans-serif;
          font-weight: bold;
        }
      }

      /* 登录/注册切换标签 - 海绵宝宝风格 */
      .tabs {
        display: flex;
        margin-bottom: 32px;
        border-radius: $cartoon-radius-md;
        background: $spongebob-brown;
        padding: 4px;
        gap: 4px;
        box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.2);

        .tab-item {
          flex: 1;
          text-align: center;
          padding: 12px 0;
          font-size: 15px;
          font-weight: bold;
          color: rgba(255, 255, 255, 0.7);
          cursor: pointer;
          border-radius: $cartoon-radius-sm;
          transition: $cartoon-transition;
          font-family: 'Comic Sans MS', 'Microsoft YaHei', cursive, sans-serif;
          text-shadow: 1px 1px 0 rgba(0, 0, 0, 0.2);

          &.active {
            background: linear-gradient(180deg, $spongebob-yellow, $spongebob-dark-yellow);
            color: $spongebob-brown;
            box-shadow: 0 3px 0 $spongebob-dark-brown;
            text-shadow: 1px 1px 0 rgba(255, 255, 255, 0.5);
          }

          &:hover:not(.active) {
            color: rgba(255, 255, 255, 0.95);
            background: rgba(255, 255, 255, 0.1);
          }
        }
      }

      /* 表单样式 */
      .login-form {
        .user-type-group {
          width: 100%;
          display: flex;
          gap: 8px;

          ::v-deep .el-radio-button {
            flex: 1;

            .el-radio-button__inner {
              width: 100%;
              padding: 10px 4px;
              font-size: 13px;
              font-family: 'Comic Sans MS', 'Microsoft YaHei', cursive, sans-serif;
              font-weight: bold;
              background: $spongebob-white;
              border: 3px solid $spongebob-brown;
              border-radius: $cartoon-radius-sm;
              color: $spongebob-brown;
              box-shadow: 0 3px 0 $spongebob-dark-brown !important;
              transition: $cartoon-transition;

              &:hover {
                background: $spongebob-bright-yellow;
              }
            }

            &.is-active .el-radio-button__inner {
              background: linear-gradient(180deg, $spongebob-red, #CC2F26);
              border-color: #CC2F26;
              color: $spongebob-white;
              box-shadow: 0 3px 0 #CC2F26 !important;
              text-shadow: 1px 1px 0 rgba(0, 0, 0, 0.2);
            }
          }
        }

        /* 表单项 */
        ::v-deep .el-form-item {
          margin-bottom: 20px;
        }

        /* 输入框 - 海绵宝宝风格 */
        ::v-deep .el-input {
          .el-input__inner {
            background: $spongebob-white;
            border: 3px solid $spongebob-brown;
            color: $spongebob-brown;
            border-radius: $cartoon-radius-sm;
            font-size: 14px;
            font-family: 'Comic Sans MS', 'Microsoft YaHei', cursive, sans-serif;
            font-weight: bold;
            height: 48px;
            transition: $cartoon-transition;
            padding-left: 44px;
            box-shadow: inset 0 2px 4px rgba(139, 69, 19, 0.1);

            &::placeholder {
              color: rgba(139, 69, 19, 0.5);
              font-weight: normal;
            }

            &:hover {
              border-color: $spongebob-orange;
              background: $spongebob-bright-yellow;
            }

            &:focus {
              border-color: $spongebob-orange;
              background: $spongebob-white;
              box-shadow: 0 0 0 3px rgba(255, 149, 0, 0.3), inset 0 2px 4px rgba(139, 69, 19, 0.1);
            }
          }

          .el-input__prefix {
            left: 12px;
          }

          .el-input__icon {
            color: $spongebob-brown;
            font-size: 18px;
          }
        }

        /* 提交按钮 - 海绵宝宝风格 */
        .login-btn {
          width: 100%;
          margin-top: 8px;
          height: 52px;
          font-size: 18px;
          font-weight: bold;
          letter-spacing: 3px;
          font-family: 'Comic Sans MS', 'Microsoft YaHei', cursive, sans-serif;
          background: linear-gradient(180deg, $spongebob-red, #CC2F26);
          border: 4px solid #AA2520;
          border-radius: $cartoon-radius-md;
          color: $spongebob-white;
          cursor: pointer;
          transition: $cartoon-transition;
          box-shadow: 0 6px 0 #AA2520, 0 8px 20px rgba(255, 59, 48, 0.3);
          text-shadow: 2px 2px 0 rgba(0, 0, 0, 0.2);
          margin-bottom: 16px;
          position: relative;
          overflow: hidden;

          /* 按钮高光 */
          &::before {
            content: '';
            position: absolute;
            top: 5px;
            left: 10%;
            right: 10%;
            height: 40%;
            background: linear-gradient(180deg, rgba(255, 255, 255, 0.4), transparent);
            border-radius: 50%;
            pointer-events: none;
          }

          &:hover {
            transform: translateY(-3px);
            box-shadow: 0 9px 0 #AA2520, 0 12px 30px rgba(255, 59, 48, 0.4), 0 0 25px rgba(255, 59, 48, 0.4);
            background: linear-gradient(180deg, #FF5252, $spongebob-red);
          }

          &:active {
            transform: translateY(3px);
            box-shadow: 0 3px 0 #AA2520, 0 5px 15px rgba(255, 59, 48, 0.3);
          }

          &:disabled {
            opacity: 0.7;
            cursor: not-allowed;
            transform: none;
          }
        }
      }
    }
  }
}
</style>
