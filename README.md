# H NAS

## 开发

### 1.配置环境

- [dragonfly](https://www.dragonflydb.io/) or  `redis`
- [flutter](https://flutter.dev/) `^3.32.1`
- [java](https://www.oracle.com/java/) `17+`
- [mysql](https://www.mysql.com/) `9+`
- [nacos3](https://nacos.io/)
- [nignx](https://nginx.org/)
- [ollama](https://ollama.com/)`0.8+`
- [rust](https://www.rust-lang.org/)

`AI Service`配置：

[和风天气](https://dev.qweather.com/)：须在命令行添加jvm参数或环境变量

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

生成前端国际化文件。`l10`需要构建，见[l10n](tools/l10n/README.md)

```shell
l10n --dir <clinet/l10n目录>
```

前端项目

```shell
cd client && flutter pub get
```

### 3.项目结构

```yaml
- buildSrc # gradle插件
- client: # 客户端
    - android # android端
    - assets  # 资源目录
    - lib     # 源码目录
    - web     # web端
    - windows # windows端
- server:
    - annotation  # 通用注解
    - common-data # 通用数据，前后端通用
    - entity      # 实体类预先义（接口）
    - fs          # 虚拟文件系统
    - utils       # 通用工具类
    - subs:
        - ai-service        # AI服务
        - broadcast-service # （总）服务广播
        - core              # 服务核心（非服务）
        - file-service      # 文件服务
        - user-service      # 用户服务
```

## 部署

### Docker

#### Ollama

1. 安装[Ollama](https://ollama.com/download)
2. 启动服务

#### 构建镜像

```shell
gradle image
```

#### [docker-compose.yml](server/docker-compose.yml)

- 配置所有留空的环境变量
- 按需修改或添加配置

默认使用`nivdia_cuda`加速。

#### `nginx`

[nginx.conf](server/nginx.conf)

## License

- 主体 `Apache 2.0`
- `FFmpeg` `h264`部分 `GPL v2+`

Copyright (c) 2024-PRESENT Yin-Jinlong@github
