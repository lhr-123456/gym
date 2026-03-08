const getters = {
  token: state => state.user.token,
  username: state => state.user.username,
  userId: state => state.user.userId,
  userType: state => state.user.userType,
  userInfo: state => state.user,
  role: state => state.user.role,
  permissions: state => state.user.permissions,
  routes: state => state.permission.routes,
  addRoutes: state => state.permission.addRoutes
}

export default getters
