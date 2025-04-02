# H NAS

## 开发

### 1.配置环境

- `java` `17+`
- `mysql` `8+`
- `flutter` `^3.7.2`
- `cmake` `3.1.4+`
- `nignx` `1.27.4+`

`mysql`

- `h_nas` 数据库
- `user` 用户
- `123456`密码

可在[application.yaml](server/subs/core/src/main/resources/application.yml)修改

### 2.加载项目

加载`gradle`项目

```shell
./gradlew --refresh-dependencies
```

生成前端类型文件

```shell
./gradlew :server:common-data:genTypes
```

## License

`Apache 2.0`

Copyright (c) 2024-PRESENT Yin-Jinlong@github
