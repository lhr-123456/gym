module.exports = {
  publicPath: '/',
  outputDir: 'dist',
  assetsDir: 'static',
  lintOnSave: false,
  productionSourceMap: false,
  devServer: {
    port: 8081,
    host: '0.0.0.0',
    open: false,
    allowedHosts: 'all',
    historyApiFallback: {
      index: '/index.html'
    },
    client: {
      overlay: {
        warnings: false,
        errors: true
      }
    },
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: true,
        logLevel: 'debug',
        pathRewrite: {
          '^/api': ''
        }
      },
      // 器材等静态上传文件由后端 8080 提供；开发时避免请求打到 8081 导致图片 404
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
}
