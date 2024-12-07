const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app){
  app.use(
    createProxyMiddleware('/naver', {
      target: 'https://localhost:8080',
      pathRewrite: {
        '^/naver':''
      },
      changeOrigin: true
    })
  )
  

};