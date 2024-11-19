# H NAS

## 开发

### 1.配置环境

- `java` `17+`
- `node` `20+`
- `mysql` `8+`

`mysql`

- `h_nas` 数据库
- `user` 用户
- `123456`密码

可在[application.yaml](server/app/src/main/resources/application.yaml)修改

### 2.加载项目

加载`gradle`项目

```shell
./gradlew --refresh-dependencies
```

生成前端类型文件

```shell
./gradlew :web:genDts
```

加载`node`前端项目

```shell
cd web && pnpm i
```

### 3.开发

前端

```shell
cd web && pnpm run dev
```

## License

`Apache 2.0`

Copyright (c) 2024-PRESENT Yin-Jinlong@github
