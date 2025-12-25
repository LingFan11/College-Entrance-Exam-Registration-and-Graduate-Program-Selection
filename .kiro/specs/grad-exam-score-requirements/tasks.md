                                                                                                                                                                                                                                                                       # Implementation Plan

- [x] 1. 扩展数据库表结构





  - [x] 1.1 扩展 grad_student_profile 表，添加考试成绩字段


    - 添加 exam_total_score, politics_score, english_exam_score, math_score, professional_score, target_institution_group_id 字段
    - 更新 schema.sql
    - _Requirements: 1.1, 1.2, 1.3_

  - [x] 1.2 扩展 grad_mentor_requirement 表，添加科目门槛字段

    - 添加 min_politics, min_english_exam, min_math, min_professional, min_total_score 字段
    - 更新 schema.sql
    - _Requirements: 2.1, 2.2_
  - [x] 1.3 创建 grad_institution_group 表


    - 创建机构分组表，包含 institution_code, institution_name, group_name, min_total_score, max_total_score, priority 字段
    - 添加索引
    - _Requirements: 5.1_
  - [x] 1.4 创建 grad_mentor_group_map 关联表


    - 创建导师与机构分组的多对多关联表
    - _Requirements: 4.4_

- [x] 2. 实现实体类和仓库层





  - [x] 2.1 更新 GradStudentProfile 实体类


    - 添加考试成绩相关字段和 getter/setter
    - _Requirements: 1.1, 1.2_
  - [x] 2.2 更新 GradMentorRequirement 实体类


    - 添加科目门槛字段和 getter/setter
    - _Requirements: 2.1, 2.2_
  - [x] 2.3 创建 GradInstitutionGroup 实体类


    - 实现机构分组实体，包含所有字段映射
    - _Requirements: 5.1_
  - [x] 2.4 创建 GradMentorGroupMap 实体类


    - 实现导师-分组关联实体
    - _Requirements: 4.4_
  - [x] 2.5 创建 GradInstitutionGroupRepository


    - 实现按机构代码查询、按分数范围查询等方法
    - _Requirements: 4.2, 5.3_
  - [x] 2.6 创建 GradMentorGroupMapRepository


    - 实现按导师ID查询分组、按分组ID查询导师等方法
    - _Requirements: 4.4_

- [x] 3. 实现 DTO 和验证逻辑





  - [x] 3.1 更新 ProfileSaveRequest DTO


    - 添加考试成绩字段及 JSR-303 验证注解
    - _Requirements: 1.1, 1.2, 1.5_
  - [x] 3.2 编写属性测试：分数验证一致性


    - **Property 1: Score Validation Consistency**
    - **Validates: Requirements 1.1, 1.2, 1.5, 2.3, 5.2**
  - [x] 3.3 创建 InstitutionGroupDTO


    - 实现机构分组数据传输对象
    - _Requirements: 4.1, 5.3_
  - [x] 3.4 创建 GroupMatchResponse DTO


    - 实现分组匹配结果数据传输对象
    - _Requirements: 4.2, 4.3_

- [x] 4. Checkpoint - 确保所有测试通过





  - Ensure all tests pass, ask the user if questions arise.
-

- [x] 5. 实现服务层核心逻辑




  - [x] 5.1 更新 GradProfileService


    - 扩展保存/获取画像方法，支持考试成绩字段
    - _Requirements: 1.3, 1.4_
  - [x] 5.2 编写属性测试：画像成绩保存-读取一致性


    - **Property 2: Profile Score Round-Trip**
    - **Validates: Requirements 1.3, 1.4**
  - [x] 5.3 创建 GradInstitutionGroupService


    - 实现分组查询、分数匹配分组等方法
    - _Requirements: 4.1, 4.2, 4.3, 5.3_
  - [x] 5.4 编写属性测试：机构分组分配


    - **Property 5: Institution Group Assignment**
    - **Validates: Requirements 4.2, 4.3, 4.4**
  - [x] 5.5 编写属性测试：机构分组持久化和排序


    - **Property 6: Institution Group Persistence and Ordering**
    - **Validates: Requirements 5.1, 5.3**

- [x] 6. 实现推荐策略扩展





  - [x] 6.1 创建 SubjectThresholdPolicy 策略类


    - 实现科目门槛检查逻辑，不达标则标记 unfit 并添加原因
    - _Requirements: 3.1, 3.2, 3.3, 3.4_
  - [x] 6.2 编写属性测试：门槛匹配正确性


    - **Property 4: Threshold Matching Correctness**
    - **Validates: Requirements 3.1, 3.2, 3.3, 3.4**
  - [x] 6.3 创建 InstitutionGroupPolicy 策略类


    - 实现机构分组过滤逻辑
    - _Requirements: 4.4_
  - [x] 6.4 更新 GradRecommendServiceImpl


    - 将新策略类集成到推荐流水线中
    - _Requirements: 3.1, 4.4_

- [x] 7. Checkpoint - 确保所有测试通过





  - Ensure all tests pass, ask the user if questions arise.

- [x] 8. 实现控制器层




  - [x] 8.1 更新 GradProfileController


    - 确保保存/获取画像接口支持新字段
    - _Requirements: 1.3, 1.4_
  - [x] 8.2 扩展 GradDictController


    - 添加机构分组字典查询接口 GET /api/grad/dicts/institution-groups
    - _Requirements: 4.1, 5.3_
  - [x] 8.3 创建分组匹配接口


    - 添加 GET /api/grad/institution-groups/match 接口
    - _Requirements: 4.2, 4.3_
  - [x] 8.4 编写属性测试：导师要求持久化


    - **Property 3: Mentor Requirement Persistence**
    - **Validates: Requirements 2.1, 2.2, 2.4**

- [x] 9. 添加测试数据






  - [x] 9.1 更新 test_data.sql

    - 添加中科院机构分组示例数据
    - 添加导师科目门槛示例数据
    - _Requirements: 4.1, 5.1_

- [x] 10. 更新前端组件



  - [x] 10.1 更新 GradAdvisorSelect.vue 成绩录入表单


    - 添加初试成绩输入区域（总分、政治、英语、数学、专业课）
    - 添加前端验证
    - _Requirements: 1.1, 1.2_
  - [x] 10.2 更新 grad.ts API 封装
    - 更新 ProfileSaveRequest 类型定义
    - 添加机构分组相关 API 调用
    - _Requirements: 1.3, 4.1_
  - [x] 10.3 实现机构分组选择器
    - 当选择中科院等特殊机构时，显示分组选择器
    - 根据成绩自动推荐匹配分组
    - _Requirements: 4.1, 4.2, 4.5_
  - [x] 10.4 更新推荐结果展示

    - 显示科目门槛不达标的具体原因
    - 显示分组匹配信息
    - _Requirements: 3.2, 4.4_

- [x] 11. Final Checkpoint - 确保所有测试通过





  - Ensure all tests pass, ask the user if questions arise.

