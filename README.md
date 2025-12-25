# 高校智能择校与导师匹配系统

一个为学生提供智能化择校与导师匹配服务的全栈应用系统。

## 功能模块

- **高考模拟填报** - 根据高考成绩、兴趣方向、地域偏好，智能推荐高校与专业
- **研究生择导** - 根据本科专业、科研经历、研究方向，智能匹配导师
- **学校信息查询** - 提供学校概况、强势学科、特色专业、招生简章等信息

## 技术栈

### 后端
- Java 17
- Spring Boot 3.5.6
- Spring Data JPA
- MySQL
- Lombok

### 前端
- Vue 3
- TypeScript
- Vite
- Vue Router
- Element Plus

## 项目结构

```
├── src/                    # 后端源码
│   ├── main/java/com/lingfan/xspp/
│   │   ├── gaokao/        # 高考模拟填报模块
│   │   ├── grad/          # 研究生择导模块
│   │   ├── school/        # 学校信息模块
│   │   └── common/        # 公共组件
│   └── main/resources/
│       ├── schema.sql     # 数据库表结构
│       └── data.sql       # 种子数据
├── frontend/              # 前端源码
│   └── src/
│       ├── components/    # 功能组件
│       ├── pages/         # 页面
│       ├── api/           # API 封装
│       └── router/        # 路由配置
└── doc/                   # 开发文档
```

## 快速开始

### 后端启动

```bash
# 配置数据库连接 (src/main/resources/application.yml)
# 启动应用
./mvnw spring-boot:run
```

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:5173

## 开发文档

详细开发文档请参阅 `doc/` 目录。

## License

MIT
