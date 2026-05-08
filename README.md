# 实验室环境监测与智能控制系统

一个基于 Android + MQTT + OneNet 的物联网实验室环境监测与控制系统，实现传感器数据实时显示、阈值设置、远程设备控制以及数据上云存储。

---

## 项目简介

本项目是一个完整的物联网解决方案，包含三个层级：

| 层级 | 角色 | 说明 |
|------|------|------|
| 下位机 | 传感器/设备端 | 采集温度、湿度、光照、火焰、烟雾数据，通过 MQTT 上报 |
| 上位机 | Android App | 接收传感器数据并展示，设置阈值，远程控制设备，转发数据到云平台 |
| 云平台 | OneNet | 存储历史数据，提供可视化界面远程查看 |

**数据流向：**
```
下位机(传感器) → EMQX消息服务器 → Android App → OneNet云平台
                      ↓
                  设备控制命令
```

---

## 功能特性

- ✅ 实时显示传感器数据（温度、湿度、光照、火焰、烟雾）
- ✅ 数据自动上报到 OneNet 云平台
- ✅ 远程控制设备（灯光、通风、空调、窗帘、加湿器）
- ✅ 温湿度阈值设置
- ✅ 网络时间同步显示
- ✅ 友好的用户界面

---

## 技术栈

| 技术 | 用途 |
|------|------|
| Android | 上位机 App 开发 |
| MQTT 协议 | 设备间通信（轻量级物联网协议） |
| EMQX | MQTT 消息服务器（Broker） |
| OneNet | 中国移动物联网云平台 |
| JSON | 数据传输格式 |
| ViewBinding | Android 界面绑定 |

---

## 项目结构

```
app/
├── build.gradle                          # 项目依赖配置
├── libs/
│   ├── simple_emqx.aar                   # MQTT 通信库
│   └── simple_framework.aar              # 基础框架库
└── src/main/
    ├── AndroidManifest.xml               # App 配置清单
    ├── java/com/example/a6yue9hao_app/
    │   ├── TeamActivity.java             # 主导航页（入口）
    │   ├── Activityshow.java             # 环境监测页
    │   ├── AcitivityYuzhi.java           # 阈值设置页
    │   └── ActivityControl.java          # 设备控制页
    └── res/layout/                       # 界面布局文件
```

---

## 环境要求

### 开发环境
- Android Studio Arctic Fox 或更高版本
- JDK 11+
- Android SDK 21+（Android 5.0）

### 运行环境
- Android 手机（Android 5.0 及以上）
- MQTT 服务器（EMQX 或其他 Broker）
- OneNet 账号

---

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/xiaoyangstm/Soft.git
cd Soft
```

### 2. 用 Android Studio 打开项目

1. 打开 Android Studio
2. 选择 `File` → `Open`
3. 选择克隆下来的项目目录
4. 等待 Gradle 同步完成

### 3. 配置 MQTT 服务器地址

在 `Activityshow.java`、`AcitivityYuzhi.java`、`ActivityControl.java` 中修改 MQTT 服务器地址：

```java
mqttTools.setHostUrl("tcp://你的服务器IP:1883");
```

### 4. 配置 OneNet 云平台

在 `Activityshow.java` 中修改 OneNet 连接参数：

```java
onenetMqttTools.setHostUrl("tcp://183.230.40.96:1883");
onenetMqttTools.setUserName("你的产品ID");
onenetMqttTools.setPassword("你的Token");
onenetMqttTools.setClientId("你的设备名称");
```

同时修改发布主题：
```java
String topic = "$sys/你的产品ID/你的设备名称/thing/property/post";
```

### 5. 编译运行

1. 连接 Android 手机（开启 USB 调试）
2. 点击 Android Studio 的 `Run` 按钮
3. 或使用快捷键 `Shift + F10`

---

## MQTT 主题说明

| 主题 | 方向 | 用途 |
|------|------|------|
| `sl_pst` | 下位机 → App | 传感器数据上报 |
| `sl_sub` | App → 下位机 | 控制命令下发 |

---

## 数据格式

### 传感器数据上报（主题：sl_pst）

```json
{
    "temperature": 21,
    "humidity": 60,
    "light": 20,
    "fire": 21,
    "smoke": 15
}
```

### 设备控制命令（主题：sl_sub）

```json
{ "light": 1 }          // 灯光：1开 0关
{ "ventilation": 1 }    // 通风：1开 0关
{ "air": 1 }            // 空调：1开 0关
{ "curtaion": 1 }       // 窗帘：1开 0关
{ "jiashiqi": 1 }       // 加湿器：1开 0关
```

### 阈值设置（主题：sl_sub）

```json
{
    "temp_l": 15,
    "temp_h": 30,
    "humi_l": 40,
    "humi_h": 80
}
```

### OneNet 数据上报格式

```json
{
    "id": "123",
    "version": "1.0",
    "params": {
        "t": { "value": 21 },
        "h": { "value": 60 },
        "l": { "value": 20 },
        "f": { "value": 21 },
        "s": { "value": 15 }
    }
}
```

---

## OneNet 云平台配置

### 创建产品和设备

1. 登录 [OneNet 控制台](https://open.iot.10086.cn/)
2. 创建产品，选择 MQTT 协议
3. 创建设备，记录产品 ID 和设备密钥

### 定义物模型

| 标识符 | 名称 | 数据类型 |
|--------|------|----------|
| `t` | 温度 | int |
| `h` | 湿度 | int |
| `l` | 光照 | int |
| `f` | 火焰 | int |
| `s` | 烟雾 | int |

### 生成 Token

使用 OneNet 提供的 Token 生成工具，输入：
- 产品 ID
- 设备名称
- 设备密钥
- 过期时间戳

将生成的 Token 填入 App 代码中。

---

## 界面说明

### 主导航页（TeamActivity）

App 启动后显示的主页面，包含三个功能入口：
- 环境监测
- 阈值设置
- 设备控制

### 环境监测页（Activityshow）

- 实时显示温度、湿度、光照、火焰、烟雾数据
- 显示当前网络时间
- 自动将数据转发到 OneNet 云平台

### 阈值设置页（AcitivityYuzhi）

- 设置温度上下限
- 设置湿度上下限
- 点击确认后发送到下位机

### 设备控制页（ActivityControl）

- 灯光开关控制
- 通风开关控制
- 空调开关控制
- 窗帘开关控制
- 加湿器开关控制

---

## 常见问题

### Q: OneNet 显示设备在线，但数据没有上报？

A: `simple_emqx` 库要求先订阅主题才能发布。确保在连接成功回调中先订阅发布主题：

```java
onenetMqttTools.subscribe("$sys/产品ID/设备名/thing/property/post", 0);
```

### Q: App 无法连接 MQTT 服务器？

A: 检查以下项：
1. 服务器 IP 和端口是否正确
2. 手机和服务器网络是否通畅
3. 服务器防火墙是否开放 1883 端口
4. App 是否有网络权限

### Q: 数据显示为空？

A: 检查以下项：
1. 下位机是否正常发送数据
2. MQTT 主题是否正确（`sl_pst`）
3. 数据格式是否为 JSON
4. 查看 Logcat 日志排查问题

---

## 详细文档

完整的项目实现文档请查看 [项目实现文档.md](./项目实现文档.md)，包含：
- EMQX 服务器搭建步骤
- OneNet 云平台详细配置
- Android 代码逐行解析
- MQTT 双连接架构说明
- 页面交互流程图

---

## 许可证

MIT License

---

## 联系方式

如有问题，欢迎提 Issue 或 PR。
