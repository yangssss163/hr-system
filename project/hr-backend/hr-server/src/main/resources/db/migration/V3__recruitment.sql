-- HR 管理系统 - 招聘管理模块建表
-- ==========================================

-- 简历表
CREATE TABLE IF NOT EXISTS rec_resume (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) COMMENT '电话',
    email VARCHAR(100) COMMENT '邮箱',
    gender TINYINT COMMENT '性别 1男 2女',
    education VARCHAR(50) COMMENT '学历',
    school VARCHAR(100) COMMENT '毕业院校',
    major VARCHAR(100) COMMENT '专业',
    work_years INT COMMENT '工作年限',
    apply_position VARCHAR(50) COMMENT '应聘职位',
    source VARCHAR(50) COMMENT '来源渠道',
    status VARCHAR(20) DEFAULT 'new' COMMENT '状态: new/screening/interview/offer/hired/eliminated',
    resume_file VARCHAR(255) COMMENT '简历文件URL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '简历表';

-- 面试记录表
CREATE TABLE IF NOT EXISTS rec_interview (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    resume_id BIGINT NOT NULL COMMENT '简历ID',
    interview_round INT DEFAULT 1 COMMENT '第几轮面试',
    interviewer_id BIGINT COMMENT '面试官ID(关联sys_user)',
    interview_time DATETIME COMMENT '面试时间',
    location VARCHAR(200) COMMENT '面试地点',
    result VARCHAR(20) DEFAULT 'pending' COMMENT '结果: pending/pass/fail/offer/hired',
    score DECIMAL(3,1) COMMENT '评分',
    evaluation TEXT COMMENT '评价内容',
    reason VARCHAR(500) COMMENT '淘汰原因',
    offer_salary VARCHAR(50) COMMENT 'Offer薪资',
    offer_date DATE COMMENT 'Offer发放日期',
    offer_remark VARCHAR(500) COMMENT 'Offer备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_resume_id (resume_id)
) COMMENT '面试记录表';

-- 面试题库表
CREATE TABLE IF NOT EXISTS rec_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category VARCHAR(50) NOT NULL COMMENT '分类: technical技术/behavioral行为/hr综合',
    difficulty VARCHAR(20) NOT NULL COMMENT '难度: easy简单/medium中等/hard困难',
    title VARCHAR(500) NOT NULL COMMENT '题目内容',
    answer TEXT COMMENT '参考答案',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '面试题库表';

-- 通知模板表
CREATE TABLE IF NOT EXISTS rec_notify_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '模板名称',
    type VARCHAR(20) NOT NULL COMMENT '类型: email邮件/sms短信',
    title VARCHAR(200) NOT NULL COMMENT '消息标题',
    content TEXT COMMENT '消息内容(支持{name}等占位符)',
    status TINYINT DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '通知模板表';

-- 面试黑名单表
CREATE TABLE IF NOT EXISTS rec_blacklist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    resume_id BIGINT COMMENT '关联简历ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) COMMENT '电话',
    id_card VARCHAR(20) COMMENT '身份证号',
    reason VARCHAR(500) COMMENT '拉黑原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '面试黑名单表';

-- 招聘管理菜单权限
INSERT INTO sys_menu (id, parent_id, name, type, path, component, permission, icon, sort, visible) VALUES
(41, 0, '招聘管理', 1, '/recruitment', '', '', 'EditPen', 3, 1),
(42, 41, '简历管理', 2, '/recruitment/resume', 'recruitment/resume/ResumeManage', 'recruitment:resume:list', 'Document', 1, 1),
(43, 42, '新增简历', 3, '', '', 'recruitment:resume:create', '', 1, 1),
(44, 42, '编辑简历', 3, '', '', 'recruitment:resume:edit', '', 2, 1),
(45, 42, '删除简历', 3, '', '', 'recruitment:resume:delete', '', 3, 1),
(46, 42, '导入简历', 3, '', '', 'recruitment:resume:import', '', 4, 1),
(47, 42, '发送邀请', 3, '', '', 'recruitment:resume:invite', '', 5, 1),
(48, 41, '面试管理', 2, '/recruitment/interview', 'recruitment/interview/InterviewManage', 'recruitment:interview:list', 'ChatRound', 2, 1),
(49, 48, '安排面试', 3, '', '', 'recruitment:interview:create', '', 1, 1),
(50, 48, '修改时间', 3, '', '', 'recruitment:interview:edit', '', 2, 1),
(51, 48, '面试评价', 3, '', '', 'recruitment:interview:evaluate', '', 3, 1),
(52, 48, '通过/淘汰', 3, '', '', 'recruitment:interview:decide', '', 4, 1),
(53, 48, '发送Offer', 3, '', '', 'recruitment:interview:offer', '', 5, 1),
(54, 48, '确认入职', 3, '', '', 'recruitment:interview:hire', '', 6, 1),
(55, 41, '面试题库', 2, '/recruitment/question', 'recruitment/question/QuestionManage', 'recruitment:question:list', 'Edit', 3, 1),
(56, 55, '新增试题', 3, '', '', 'recruitment:question:create', '', 1, 1),
(57, 55, '编辑试题', 3, '', '', 'recruitment:question:edit', '', 2, 1),
(58, 55, '删除试题', 3, '', '', 'recruitment:question:delete', '', 3, 1),
(59, 41, '通知模板', 2, '/recruitment/notify-template', 'recruitment/notify/NotifyTemplateManage', 'recruitment:notify-template:list', 'Message', 4, 1),
(60, 59, '新增模板', 3, '', '', 'recruitment:notify-template:create', '', 1, 1),
(61, 59, '编辑模板', 3, '', '', 'recruitment:notify-template:edit', '', 2, 1),
(62, 59, '删除模板', 3, '', '', 'recruitment:notify-template:delete', '', 3, 1),
(63, 41, '黑名单管理', 2, '/recruitment/blacklist', 'recruitment/blacklist/BlacklistManage', 'recruitment:blacklist:list', 'Warning', 5, 1),
(64, 63, '加入黑名单', 3, '', '', 'recruitment:blacklist:create', '', 1, 1),
(65, 63, '移出黑名单', 3, '', '', 'recruitment:blacklist:delete', '', 2, 1),
(66, 41, '招聘报表', 2, '/recruitment/report', 'recruitment/report/RecruitmentReport', 'recruitment:report:list', 'TrendCharts', 6, 1);

-- 为超级管理员分配招聘管理菜单权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id BETWEEN 41 AND 66;
