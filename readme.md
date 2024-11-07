# 基于兰空图床 API 重写的图床服务

这是根据 [兰空图床](https://github.com/lsky-org/lsky-pro) API 重写的图床服务，实现了与兰空图床在 [API](https://github.com/lsky-org/lsky-pro/blob/911275c13b038c7a8b710de44664f23887eeb6f6/resources/views/common/api.blade.php#L9) 上相同的功能，同时 100% 兼容了 Halo 博客的 [Lsky Pro 插件](https://github.com/ichenhe/halo-lsky-pro#readme)。

你可以根据 `Dockerfile` 快速启动一个图床服务。

⚠️ 需要Mysql环境

使用.env来配置环境变量，请在jar包的同级目录下创建.env文件，内容参见.env.example

## 写在前面

写这个图床的主要原因有两点：

1. **内网与公网地址问题**：我的图床服务配置在内网主机上，通过端口转发到公网服务器，配置了 CDN，并启用了 HTTPS。然而由于某种验证机制，导致在内网初始化的兰空图床返回的图片 URL 全是内网的地址，而如果使用外网域名则根本无法完成初始化。
2. **CDN 鉴权与防盗链**：为了实现 CDN 鉴权，防止图像盗链，我需要在图片 URL 后面加上一个临时访问的 token。这个功能配合上 Halo 的密码阅读保护插件，能够减少图片被盗用的风险。即没有阅读完整博文的权限的情况下，也无法从 CDN 访问图片。

## 注意

1. **HTTPS 配置**：如果博客使用了 HTTPS，则图床也需要使用 HTTPS，否则浏览器会在前端拦截。
2. **域名配置**：请在配置文件中配置博客的服务器域名地址，否则无法使用。域名地址类似于这样：`https://static.blogdomain.com`
3. **挂载磁盘**： 在容器启动时，挂载一个磁盘到 `/app` 目录，用于存储图片。如果不挂载磁盘，存储的图片会在容器关闭时丢失。

## 启动方法
我没有DockerHub账号，所以你需要自己编译Jar包然后构建镜像，或者使用Github Actions构建的Jar包构建镜像。
参照docker-compose.yml,准备好.env和app.jar后，使用docker-compose启动容器:

    docker-compose up -d
无论使用哪种方式，都需要保证jar包同级目录下能够读取到.env配置文件。

使用外部.env启动容器: 指定.env文件路径 --env-file /path/to/.env

    docker run --name BasicImageBed --env-file /path/to/.env -p 28123:8080 -v /opt/BasicImageBed/upload:/app/upload basic_image_bed



## 完成功能
- ✔ 基本的API功能(Token、上传、下载、相册、删除)
- □ 多存储策略(当前仅完成本地存储)
- □ 权限管理
- □ 多用户
- □ 缩略图
- □ Web界面以及更详细的管理功能

防盗链服务需要在Halo上进行额外的插件开发，让每次发出图片请求时都带上token，这个功能暂时没有实现。正在研究halo插件开发中。
