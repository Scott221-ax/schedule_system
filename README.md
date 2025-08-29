# 排班排课系统 (Schedule System)

一个基于Spring Boot的企业级排班排课管理系统，支持员工排班管理和课程安排功能。

## 项目特性

- 🏗️ **分层架构**: 采用经典的MVC分层架构，代码结构清晰
- 🚀 **Spring Boot**: 基于Spring Boot 3.2.0，支持最新特性
- 💾 **数据持久化**: 使用MyBatis Plus + MySQL，支持分页和逻辑删除
- 🔐 **安全认证**: 集成Spring Security，支持用户认证和授权
- 📊 **缓存支持**: 集成Redis缓存，提升系统性能
- 🎯 **参数校验**: 使用Bean Validation进行参数校验
- 📝 **日志管理**: 完善的日志配置，支持不同环境
- 🔧 **配置管理**: 支持多环境配置（dev/prod）

## 技术栈

- **后端框架**: Spring Boot 3.2.0
- **数据库**: MySQL 8.0+
- **ORM框架**: MyBatis Plus 3.5.4
- **缓存**: Redis
- **安全框架**: Spring Security
- **构建工具**: Maven 3.6+
- **JDK版本**: Java 21+

## 项目结构

```
schedule_system/
├── src/
│   ├── main/
│   │   ├── java/com/meituan/schedule/
│   │   │   ├── controller/          # 控制层
│   │   │   ├── service/             # 业务层
│   │   │   ├── mapper/              # 数据访问层
│   │   │   ├── entity/              # 实体类
│   │   │   ├── dto/                 # 数据传输对象
│   │   │   ├── vo/                  # 视图对象
│   │   │   ├── config/              # 配置类
│   │   │   ├── common/              # 公共类
│   │   │   ├── enums/               # 枚举类
│   │   │   ├── exception/           # 异常处理
│   │   │   ├── utils/               # 工具类
│   │   │   └── ScheduleSystemApplication.java
│   │   └── resources/
│   │       ├── mapper/              # MyBatis XML文件
│   │       ├── static/              # 静态资源
│   │       ├── templates/           # 模板文件
│   │       ├── application.yml      # 主配置文件
│   │       ├── application-dev.yml  # 开发环境配置
│   │       └── application-prod.yml # 生产环境配置
│   └── test/                        # 测试代码
├── docs/                            # 项目文档
├── scripts/                         # 脚本文件
├── sql/                             # 数据库脚本
├── logs/                            # 日志文件
├── pom.xml                          # Maven配置
└── README.md                        # 项目说明
```

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd schedule_system
   ```

2. **创建数据库**
   ```bash
   mysql -u root -p < sql/init.sql
   ```

3. **修改配置**

   复制并修改配置文件：
   ```bash
   cp src/main/resources/application-dev.yml src/main/resources/application-local.yml
   ```

   修改 `application-local.yml` 中的数据库和Redis连接信息。

4. **编译项目**
   ```bash
   mvn clean compile
   ```

5. **运行项目**
   ```bash
   mvn spring-boot:run
   ```

6. **访问应用**

   浏览器访问: http://localhost:8080/schedule-system

### 生产部署

1. **打包应用**
   ```bash
   mvn clean package -Dmaven.test.skip=true
   ```

2. **启动应用**
   ```bash
   ./scripts/start.sh
   ```

## 核心功能

### 用户管理
- 用户注册、登录、权限管理
- 支持管理员和普通用户角色

### 部门管理
- 部门层级结构管理
- 支持多级部门嵌套

### 排班管理
- 员工排班安排
- 支持早班、午班、晚班
- 请假、代班功能

### 课程管理
- 课程安排和管理
- 教师分配
- 容量控制

## API文档

启动应用后，访问以下地址查看API文档：
- Swagger UI: http://localhost:8080/schedule-system/swagger-ui.html

## 配置说明

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/schedule_system
    username: your_username
    password: your_password
```

### Redis配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your_password
```

## 开发指南

### 代码规范
- 遵循阿里巴巴Java开发手册
- 使用Lombok减少样板代码
- 统一异常处理和返回格式

### 分支管理
- `main`: 主分支，用于生产环境
- `develop`: 开发分支
- `feature/*`: 功能分支

### 提交规范
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建过程或辅助工具的变动
```

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

如有问题或建议，请联系：
- 邮箱: dev@meituan.com
- 项目地址: https://github.com/meituan/schedule_system

