# Requirements Document

## Introduction

本功能扩展研究生择导模块，增加考研初试成绩相关的基本信息填写功能。学生可以录入初试总分和各科目成绩，导师可以设置各科目的最低分要求。系统根据这些信息进行更精准的匹配筛选。特别地，针对中科院等特殊机构，支持根据成绩进行进一步分组推荐。

## Glossary

- **Grad_System**: 研究生择导系统
- **Student_Profile**: 学生画像，包含学生的基本信息、成绩、偏好等
- **Exam_Score**: 考研初试成绩，包括总分和各科目分数
- **Subject_Score**: 单科成绩，如政治、英语、数学、专业课等
- **Mentor_Requirement**: 导师招生要求，包括各科目最低分限制
- **Institution_Group**: 机构分组，如中科院下属各研究所
- **Score_Threshold**: 分数门槛，导师设置的最低分要求

## Requirements

### Requirement 1

**User Story:** As a 考研学生, I want to 录入我的初试成绩信息, so that 系统可以根据我的实际成绩进行更精准的导师匹配。

#### Acceptance Criteria

1. WHEN a student submits exam scores THEN the Grad_System SHALL validate that the total score is within the range of 0 to 500
2. WHEN a student submits subject scores THEN the Grad_System SHALL validate that each subject score is within its valid range (政治/英语: 0-100, 数学/专业课: 0-150)
3. WHEN a student saves exam scores THEN the Grad_System SHALL persist the scores to the Student_Profile immediately
4. WHEN exam scores are saved THEN the Grad_System SHALL return a success response with the updated profile data
5. WHEN a student submits invalid score values THEN the Grad_System SHALL reject the submission and return specific validation error messages

### Requirement 2

**User Story:** As a 导师/管理员, I want to 设置各科目的最低分要求, so that 系统可以筛选出符合基本条件的学生。

#### Acceptance Criteria

1. WHEN a mentor requirement includes subject score thresholds THEN the Grad_System SHALL store each subject's minimum score requirement
2. WHEN subject thresholds are configured THEN the Grad_System SHALL support setting thresholds for politics, english, math, and professional subjects independently
3. WHEN a mentor requirement is updated THEN the Grad_System SHALL validate that threshold values are within valid ranges for each subject type
4. WHEN retrieving mentor requirements THEN the Grad_System SHALL return all configured subject thresholds along with other requirements

### Requirement 3

**User Story:** As a 考研学生, I want to 在推荐计算时根据我的成绩进行筛选, so that 我只看到我有资格申请的导师。

#### Acceptance Criteria

1. WHEN computing recommendations THEN the Grad_System SHALL compare student subject scores against mentor subject thresholds
2. WHEN a student's score is below any mentor's subject threshold THEN the Grad_System SHALL mark the mentor as unfit with specific reason indicating which subject is insufficient
3. WHEN a student meets all subject thresholds THEN the Grad_System SHALL include the mentor in the recommendation list
4. WHEN a student has missing subject scores THEN the Grad_System SHALL treat the comparison as inconclusive and apply a penalty score

### Requirement 4

**User Story:** As a 报考中科院的学生, I want to 根据我的成绩被分配到合适的研究所分组, so that 我可以看到与我成绩匹配的研究所和导师。

#### Acceptance Criteria

1. WHEN a student selects CAS (中科院) as target institution THEN the Grad_System SHALL display available institute groups
2. WHEN institute groups are configured with score ranges THEN the Grad_System SHALL assign students to appropriate groups based on their total score
3. WHEN a student's score falls within multiple group ranges THEN the Grad_System SHALL assign the student to the highest matching group
4. WHEN displaying recommendations for CAS THEN the Grad_System SHALL filter mentors by the student's assigned institute group
5. WHEN no institute group matches the student's score THEN the Grad_System SHALL display a message indicating no matching groups and suggest alternatives

### Requirement 5

**User Story:** As a 系统管理员, I want to 配置机构分组规则, so that 不同机构可以有不同的分组策略。

#### Acceptance Criteria

1. WHEN an institution group is created THEN the Grad_System SHALL store the group name, parent institution, and score range
2. WHEN score ranges are configured THEN the Grad_System SHALL validate that ranges do not have invalid boundaries (min > max)
3. WHEN retrieving institution groups THEN the Grad_System SHALL return groups ordered by score range in descending order
4. WHEN an institution has no configured groups THEN the Grad_System SHALL treat all mentors under that institution as a single group

