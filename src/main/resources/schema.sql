-- Schema for 高考模拟填报模块 (MySQL)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS rank_band;
DROP TABLE IF EXISTS rank_mapping;
DROP TABLE IF EXISTS university_major_stat;
DROP TABLE IF EXISTS university_major_plan;
DROP TABLE IF EXISTS major;
DROP TABLE IF EXISTS university;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE university (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  city VARCHAR(64) NOT NULL,
  tier VARCHAR(16) NOT NULL -- 985/211/双一流/普通
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE major (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  category VARCHAR(64) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE university_major_plan (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  university_id BIGINT NOT NULL,
  major_id BIGINT NOT NULL,
  province VARCHAR(32) NOT NULL,
  subject_type VARCHAR(16) NOT NULL, -- history|physics
  batch VARCHAR(32) NOT NULL, -- 本科批 等
  tuition INT NOT NULL, -- 每年学费（元）
  `year` INT NOT NULL,
  plan_count INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Indexes (no foreign keys by request)
CREATE INDEX idx_plan_univ ON university_major_plan(university_id);
CREATE INDEX idx_plan_major ON university_major_plan(major_id);
CREATE INDEX idx_plan_province_subject_year ON university_major_plan(province, subject_type, `year`);

CREATE TABLE university_major_stat (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  university_id BIGINT NOT NULL,
  major_id BIGINT NOT NULL,
  province VARCHAR(32) NOT NULL,
  subject_type VARCHAR(16) NOT NULL,
  `year` INT NOT NULL,
  min_score INT,
  min_rank INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Indexes (no foreign keys by request)
CREATE INDEX idx_stat_univ ON university_major_stat(university_id);
CREATE INDEX idx_stat_major ON university_major_stat(major_id);
CREATE INDEX idx_stat_province_subject_year ON university_major_stat(province, subject_type, `year`);

-- Optional mapping (score->rank) when only score is provided
CREATE TABLE rank_mapping (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  province VARCHAR(32) NOT NULL,
  subject_type VARCHAR(16) NOT NULL,
  `year` INT NOT NULL,
  score INT NOT NULL,
  rank_value INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Optional band (score range -> rank range) for robust mapping
CREATE TABLE rank_band (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  province VARCHAR(32) NOT NULL,
  subject_type VARCHAR(16) NOT NULL,
  `year` INT NOT NULL,
  score_min INT NOT NULL,
  score_max INT NOT NULL,
  rank_min INT NULL,
  rank_max INT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_band_province_subject_year ON rank_band(province, subject_type, `year`);

-- ===================================================================
-- Graduate Advisor Selection Module (研究生择导模块) - Schema (MySQL)
-- Notes:
-- 1) Keep NO foreign keys (align with existing module style); rely on indexes
-- 2) Use utf8mb4; add practical indexes; unique keys for M:N mappings
-- 3) Timestamps default to CURRENT_TIMESTAMP where appropriate
-- ===================================================================

-- Drop existing graduate tables for dev re-init (align with spring.sql.init.mode=always)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS grad_mentor_student_fit;
DROP TABLE IF EXISTS grad_student_preference;
DROP TABLE IF EXISTS grad_student_profile;
DROP TABLE IF EXISTS grad_mentor_publication;
DROP TABLE IF EXISTS grad_mentor_tag_map;
DROP TABLE IF EXISTS grad_mentor_tag;
DROP TABLE IF EXISTS grad_mentor_requirement;
DROP TABLE IF EXISTS grad_mentor_quota;
DROP TABLE IF EXISTS grad_mentor_direction;
DROP TABLE IF EXISTS grad_mentor;
DROP TABLE IF EXISTS grad_department;
DROP TABLE IF EXISTS grad_research_direction;
DROP TABLE IF EXISTS grad_institution_group;
DROP TABLE IF EXISTS grad_mentor_group_map;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE grad_research_direction (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  parent_id BIGINT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_grd_parent ON grad_research_direction(parent_id);

CREATE TABLE grad_department (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  university_id BIGINT NOT NULL,
  name VARCHAR(128) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_gd_univ ON grad_department(university_id);

CREATE TABLE grad_mentor (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  university_id BIGINT NOT NULL,
  department_id BIGINT NOT NULL,
  name VARCHAR(64) NOT NULL,
  title VARCHAR(64) NULL, -- 教授/副教授/讲师 等
  email VARCHAR(128) NULL,
  homepage VARCHAR(256) NULL,
  brief TEXT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_gm_univ_dept ON grad_mentor(university_id, department_id);

CREATE TABLE grad_mentor_direction (
  mentor_id BIGINT NOT NULL,
  direction_id BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX uq_gmd_mentor_dir ON grad_mentor_direction(mentor_id, direction_id);
CREATE INDEX idx_gmd_dir ON grad_mentor_direction(direction_id);

CREATE TABLE grad_mentor_quota (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  mentor_id BIGINT NOT NULL,
  `year` INT NOT NULL,
  total_slots INT NOT NULL,
  filled_slots INT NOT NULL DEFAULT 0,
  notes VARCHAR(256) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX uq_gmq_mentor_year ON grad_mentor_quota(mentor_id, `year`);

CREATE TABLE grad_mentor_tag (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX uq_gmt_name ON grad_mentor_tag(name);

CREATE TABLE grad_mentor_tag_map (
  mentor_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX uq_gmtm ON grad_mentor_tag_map(mentor_id, tag_id);
CREATE INDEX idx_gmtm_tag ON grad_mentor_tag_map(tag_id);

CREATE TABLE grad_mentor_publication (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  mentor_id BIGINT NOT NULL,
  title VARCHAR(256) NOT NULL,
  venue VARCHAR(128) NULL,
  `year` INT NULL,
  link VARCHAR(256) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_gmp_mentor ON grad_mentor_publication(mentor_id);

CREATE TABLE grad_student_profile (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  current_university VARCHAR(128) NULL,
  current_major VARCHAR(128) NULL,
  gpa DECIMAL(3,2) NULL,           -- 0.00 - 5.00（按学校制式）
  english_score INT NULL,          -- 如 CET、雅思等标准化值
  english_type VARCHAR(32) NULL,   -- CET4/CET6/IELTS/TOEFL 等
  competitions TEXT NULL,
  research_exp TEXT NULL,
  preferred_directions JSON NULL,  -- 简单方向偏好列表
  region_pref VARCHAR(64) NULL,    -- 地区偏好
  style_pref VARCHAR(64) NULL,     -- 导师风格偏好
  target_tier VARCHAR(16) NULL,    -- 目标院校层级
  target_universities JSON NULL,   -- 目标院校列表
  target_directions_topn JSON NULL,-- Top-N 方向 + 权重 [{directionId, weight}]
  -- 考研初试成绩字段 (Requirements 1.1, 1.2, 1.3)
  exam_total_score INT NULL,       -- 初试总分 (0-500)
  politics_score INT NULL,         -- 政治成绩 (0-100)
  english_exam_score INT NULL,     -- 英语考试成绩 (0-100)，区别于 english_score（证书分数）
  math_score INT NULL,             -- 数学成绩 (0-150)
  professional_score INT NULL,     -- 专业课成绩 (0-150)
  target_institution_group_id BIGINT NULL, -- 目标机构分组ID
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX uq_gsp_user ON grad_student_profile(user_id);
CREATE INDEX idx_gsp_institution_group ON grad_student_profile(target_institution_group_id);

CREATE TABLE grad_student_preference (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  mentor_id BIGINT NOT NULL,
  priority INT NOT NULL,
  notes VARCHAR(256) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX uq_gsprf_student_mentor ON grad_student_preference(student_id, mentor_id);
CREATE INDEX idx_gsprf_student_priority ON grad_student_preference(student_id, priority);

CREATE TABLE grad_mentor_student_fit (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  mentor_id BIGINT NOT NULL,
  score DECIMAL(5,2) NOT NULL,
  status VARCHAR(16) NOT NULL, -- fit|borderline|unfit
  reasons JSON NULL,
  computed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_gmsf_student_score ON grad_mentor_student_fit(student_id, score);

-- Mentor-level requirements (thresholds per mentor)
CREATE TABLE grad_mentor_requirement (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  mentor_id BIGINT NOT NULL,
  min_gpa DECIMAL(3,2) NULL,
  english_type VARCHAR(32) NULL,       -- CET4/CET6/IELTS/TOEFL 等
  min_english INT NULL,                -- 如 CET-6 500 分
  -- 考研初试科目门槛字段 (Requirements 2.1, 2.2)
  min_politics INT NULL,               -- 政治最低分 (0-100)
  min_english_exam INT NULL,           -- 英语考试最低分 (0-100)
  min_math INT NULL,                   -- 数学最低分 (0-150)
  min_professional INT NULL,           -- 专业课最低分 (0-150)
  min_total_score INT NULL,            -- 总分最低分 (0-500)
  notes VARCHAR(256) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX uq_gmr_mentor ON grad_mentor_requirement(mentor_id);

-- Institution groups for special institutions like CAS (中科院) (Requirements 5.1)
CREATE TABLE grad_institution_group (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  institution_code VARCHAR(32) NOT NULL,  -- 机构代码，如 'CAS' 表示中科院
  institution_name VARCHAR(128) NOT NULL, -- 机构名称
  group_name VARCHAR(128) NOT NULL,       -- 分组名称，如 '计算所'、'软件所'
  min_total_score INT NULL,               -- 该分组最低总分要求
  max_total_score INT NULL,               -- 该分组最高总分（用于范围匹配）
  priority INT DEFAULT 0,                 -- 优先级，用于多范围重叠时选择
  description VARCHAR(512) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_gig_inst ON grad_institution_group(institution_code);
CREATE INDEX idx_gig_score ON grad_institution_group(min_total_score, max_total_score);

-- Mentor to institution group mapping (Requirements 4.4)
CREATE TABLE grad_mentor_group_map (
  mentor_id BIGINT NOT NULL,
  group_id BIGINT NOT NULL,
  PRIMARY KEY (mentor_id, group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_gmgm_group ON grad_mentor_group_map(group_id);

-- ===== Users and Sessions (for auto-generated userId and basic rate limiting) =====
CREATE TABLE IF NOT EXISTS grad_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  created_at DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS grad_session (
  session_id VARCHAR(36) PRIMARY KEY,
  user_id BIGINT NOT NULL,
  ip VARCHAR(64),
  created_at DATETIME NOT NULL,
  last_seen DATETIME NOT NULL,
  INDEX idx_gs_user (user_id),
  INDEX idx_gs_ip (ip)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===== Domains and Mappings (for table-driven domain management) =====
CREATE TABLE IF NOT EXISTS grad_domain (
  code VARCHAR(32) PRIMARY KEY,
  name VARCHAR(64) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS grad_major_domain_map (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  domain_code VARCHAR(32) NOT NULL,
  keyword VARCHAR(64) NOT NULL,
  INDEX idx_gmdm_domain (domain_code),
  INDEX idx_gmdm_keyword (keyword)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS grad_direction_domain_map (
  direction_id BIGINT PRIMARY KEY,
  domain_code VARCHAR(32) NOT NULL,
  INDEX idx_gddm_domain (domain_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 数据插入请使用 data.sql

-- ===================================================================
-- School Information Module (学校信息模块) - Schema (MySQL)
-- Notes:
-- 1) Keep NO foreign keys; manage consistency in service layer
-- 2) Use utf8mb4; practical indexes; unique keys for mapping tables
-- 3) sch_* tables are independent from gaokao module's simple `university/major`
-- ===================================================================

-- Drop existing school information tables for dev re-init
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS sch_tag_map;
DROP TABLE IF EXISTS sch_tag;
DROP TABLE IF EXISTS sch_univ_major;
DROP TABLE IF EXISTS sch_major;
DROP TABLE IF EXISTS sch_univ_discipline_strength;
DROP TABLE IF EXISTS sch_discipline;
DROP TABLE IF EXISTS sch_department;
DROP TABLE IF EXISTS sch_admission_brochure;
DROP TABLE IF EXISTS sch_university;
SET FOREIGN_KEY_CHECKS = 1;

-- University main table
CREATE TABLE sch_university (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  alias VARCHAR(128) NULL,
  level VARCHAR(64) NULL,            -- 多值以逗号分隔：985,211,双一流
  province VARCHAR(32) NULL,
  city VARCHAR(32) NULL,
  website VARCHAR(256) NULL,
  brief TEXT NULL,
  founded_year INT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_schu_name ON sch_university(name);
CREATE INDEX idx_schu_level_province ON sch_university(level, province);

-- Departments
CREATE TABLE sch_department (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  university_id BIGINT NOT NULL,
  name VARCHAR(128) NOT NULL,
  brief VARCHAR(512) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_schd_univ ON sch_department(university_id);

-- Discipline dictionary (tree)
CREATE TABLE sch_discipline (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  field VARCHAR(64) NULL,      -- 工学/理学/文史 等
  parent_id BIGINT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_schdisc_parent ON sch_discipline(parent_id);

-- University strong disciplines (rank/grade per year)
CREATE TABLE sch_univ_discipline_strength (
  university_id BIGINT NOT NULL,
  discipline_id BIGINT NOT NULL,
  rank_grade VARCHAR(16) NULL,  -- 等级：A+/A/B+ 等
  rank_value INT NULL,          -- 可选：数值排名（若有）
  source VARCHAR(128) NULL,
  `year` INT NOT NULL,
  note VARCHAR(256) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX uq_schuds ON sch_univ_discipline_strength(university_id, discipline_id, `year`);
CREATE INDEX idx_schuds_univ ON sch_univ_discipline_strength(university_id);

-- Majors and mapping to university/department
CREATE TABLE sch_major (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  degree VARCHAR(16) NOT NULL,  -- 本科/硕士/博士
  discipline_id BIGINT NULL,
  code VARCHAR(32) NULL,
  brief VARCHAR(512) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_schm_disc ON sch_major(discipline_id);

CREATE TABLE sch_univ_major (
  university_id BIGINT NOT NULL,
  major_id BIGINT NOT NULL,
  department_id BIGINT NULL,
  feature_tag VARCHAR(64) NULL,
  note VARCHAR(256) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX uq_schum_univ_major ON sch_univ_major(university_id, major_id);
CREATE INDEX idx_schum_dept ON sch_univ_major(department_id);

-- Tags and mapping
CREATE TABLE sch_tag (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL,
  type VARCHAR(16) NOT NULL   -- school/department/major
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX uq_schtag_name_type ON sch_tag(name, type);

CREATE TABLE sch_tag_map (
  tag_id BIGINT NOT NULL,
  target_type VARCHAR(16) NOT NULL, -- school/department/major
  target_id BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX uq_schtagmap ON sch_tag_map(tag_id, target_type, target_id);
CREATE INDEX idx_schtagmap_tag ON sch_tag_map(tag_id);

-- Admission brochures / announcements
CREATE TABLE sch_admission_brochure (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  university_id BIGINT NOT NULL,
  degree VARCHAR(16) NOT NULL, -- 本科/硕士/博士
  `year` INT NOT NULL,
  title VARCHAR(256) NOT NULL,
  content_md MEDIUMTEXT NULL,
  link VARCHAR(512) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_schbro_univ_degree_year ON sch_admission_brochure(university_id, degree, `year`);