# H NAS

## 开发

### 1.配置环境

- `java` `17+`
- `mysql` `8+`
- `flutter` `^3.7.2`
- `cmake` `3.1.4+`
- `nignx` `1.27.4+`
- `dragonfly` or  `redis`

`mysql`

- `h_nas` 数据库
- `user` 用户
- `123456`密码

可在[application.yaml](server/subs/core/src/main/resources/application.yml)修改

`AI Service`配置：

须在命令行添加jvm参数或环境变量

```text
-Dqweather.host=api host
-Dqweather.jwt.privateKey=ed25519-private不包含前后缀
-Dqweather.jwt.keyId=keyID
-Dqweather.jwt.projectId=项目id
```

### 2.加载项目

加载`gradle`项目

```shell
./gradlew --refresh-dependencies
```

前端项目

```shell
cd client && flutter pub get
```

### 3.项目结构

```yaml
.:
  - buildSrc  # gradle插件
  - client: # 客户端
      - android # android端
      - assets  # 资源目录
      - lib     # 源码目录
      - web     # web端
      - windows # windows端
  - server:
      - annotation          # 通用注解
      - common-data         # 通用数据，前后端通用
      - entity              # 实体类预先义（接口）
      - fs                  # 虚拟文件系统
      - utils               # 通用工具类
      - subs:
          - broadcast-service # （总）服务广播
          - core              # 服务核心（非服务）
          - file-service      # 文件服务
          - user-service      # 用户服务
```

## 部署

### Docker

- 构建镜像

```shell
gradle :server:subs:user-service:image
gradle :server:subs:file-service:image
```

- [docker-compose.yml](server/docker-compose.yml)

默认使用`nivdia_cuda`加速。

- `nginx`

[nginx.conf](server/nginx.conf)

## License

- 主体 `Apache 2.0`

- `FFmpeg` 主体 `LGPL v2.1+`

- `FFmpeg` 部分 `GPL v2+`

Copyright (c) 2024-PRESENT Yin-Jinlong@github
