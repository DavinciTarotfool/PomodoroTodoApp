# 项目规范

## 技术栈
- Kotlin
- Jetpack Compose
- MVVM
- Retrofit
- Room

## 代码规范
- ViewModel 不允许直接访问数据库
- Repository 负责数据源
- UI 使用 Compose
- 不允许在 Activity 写业务逻辑

## 修改要求
- 修改尽量最小化
- 不要随意重构
- 保持现有命名风格