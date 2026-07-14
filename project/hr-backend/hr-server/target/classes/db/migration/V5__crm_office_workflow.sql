-- HR 管理系统 - CRM/办公/工作流模块建表
-- ==========================================

-- ============ CRM 模块 ============

-- 客户表
CREATE TABLE IF NOT EXISTS crm_customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '客户名称',
    phone VARCHAR(20) COMMENT '电话',
    email VARCHAR(100) COMMENT '邮箱',
    industry VARCHAR(50) COMMENT '行业',
    source VARCHAR(50) COMMENT '来源',
    level VARCHAR(20) COMMENT '客户等级',
    status VARCHAR(20) COMMENT '状态',
    owner_id BIGINT COMMENT '负责人ID',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    address VARCHAR(200) COMMENT '地址',
    contact_name VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系人电话',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'CRM客户表';

-- 商机表
CREATE TABLE IF NOT EXISTS crm_opportunity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL COMMENT '商机名称',
    customer_id BIGINT COMMENT '客户ID',
    amount DECIMAL(12,2) COMMENT '金额',
    stage VARCHAR(20) COMMENT '阶段',
    probability INT COMMENT '概率',
    expected_close_date DATE COMMENT '预计成交日期',
    owner_id BIGINT COMMENT '负责人ID',
    contact_name VARCHAR(50) COMMENT '联系人姓名',
    contact_phone VARCHAR(20) COMMENT '联系人电话',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'CRM商机表';

-- 订单表
CREATE TABLE IF NOT EXISTS crm_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    opportunity_id BIGINT COMMENT '商机ID',
    customer_id BIGINT COMMENT '客户ID',
    order_no VARCHAR(50) COMMENT '订单编号',
    amount DECIMAL(12,2) COMMENT '金额',
    status VARCHAR(20) COMMENT '状态',
    sign_date DATE COMMENT '签约日期',
    owner_id BIGINT COMMENT '负责人ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'CRM订单表';

-- 回款表
CREATE TABLE IF NOT EXISTS crm_payment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT COMMENT '订单ID',
    customer_id BIGINT COMMENT '客户ID',
    payment_no VARCHAR(50) COMMENT '付款编号',
    amount DECIMAL(12,2) COMMENT '金额',
    payment_date DATE COMMENT '付款日期',
    payment_method VARCHAR(20) COMMENT '付款方式',
    status VARCHAR(20) COMMENT '状态',
    owner_id BIGINT COMMENT '负责人ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'CRM回款表';

-- 退款表
CREATE TABLE IF NOT EXISTS crm_refund (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT COMMENT '订单ID',
    customer_id BIGINT COMMENT '客户ID',
    refund_no VARCHAR(50) COMMENT '退款编号',
    amount DECIMAL(12,2) COMMENT '金额',
    refund_date DATE COMMENT '退款日期',
    reason VARCHAR(500) COMMENT '退款原因',
    status VARCHAR(20) COMMENT '状态',
    owner_id BIGINT COMMENT '负责人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'CRM退款表';

-- 费用表
CREATE TABLE IF NOT EXISTS crm_expense (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '费用名称',
    amount DECIMAL(12,2) COMMENT '金额',
    expense_date DATE COMMENT '费用日期',
    category VARCHAR(50) COMMENT '类别',
    status VARCHAR(20) COMMENT '状态',
    applicant_id BIGINT COMMENT '申请人ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'CRM费用表';

-- ============ 办公模块 ============

-- 文档表
CREATE TABLE IF NOT EXISTS sys_document (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    category VARCHAR(50) COMMENT '类别',
    parent_id BIGINT COMMENT '父级ID',
    creator_id BIGINT COMMENT '创建人ID',
    is_public TINYINT DEFAULT 0 COMMENT '是否公开',
    file_url VARCHAR(255) COMMENT '文件URL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '文档表';

-- 任务表
CREATE TABLE IF NOT EXISTS sys_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    creator_id BIGINT COMMENT '创建人ID',
    assignee_id BIGINT COMMENT '被指派者ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    priority VARCHAR(20) COMMENT '优先级',
    status VARCHAR(20) COMMENT '状态',
    start_date DATE COMMENT '开始日期',
    due_date DATE COMMENT '截止日期',
    complete_time DATETIME COMMENT '完成时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '任务表';

-- 日程表
CREATE TABLE IF NOT EXISTS sys_schedule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '用户ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    all_day TINYINT DEFAULT 0 COMMENT '是否全天',
    color VARCHAR(20) COMMENT '颜色',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '日程表';

-- 消息表
CREATE TABLE IF NOT EXISTS sys_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT COMMENT '发送人ID',
    receiver_id BIGINT COMMENT '接收人ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读',
    send_time DATETIME COMMENT '发送时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '消息表';

-- 公告表
CREATE TABLE IF NOT EXISTS sys_notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT COMMENT '公告内容',
    type TINYINT COMMENT '类型',
    status TINYINT DEFAULT 0 COMMENT '状态',
    publisher_id BIGINT COMMENT '发布人ID',
    publish_time DATETIME COMMENT '发布时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '系统公告表';

-- ============ 工作流模块 ============

-- 报销流程表
CREATE TABLE IF NOT EXISTS flow_expense (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    applicant_id BIGINT COMMENT '申请人ID',
    amount DECIMAL(12,2) COMMENT '金额',
    expense_date DATE COMMENT '报销日期',
    category VARCHAR(50) COMMENT '类别',
    description VARCHAR(500) COMMENT '描述',
    status VARCHAR(20) COMMENT '状态',
    approver_id BIGINT COMMENT '审批人ID',
    approve_time DATETIME COMMENT '审批时间',
    approve_comment VARCHAR(500) COMMENT '审批意见',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '报销流程表';

-- 借款流程表
CREATE TABLE IF NOT EXISTS flow_loan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    applicant_id BIGINT COMMENT '申请人ID',
    amount DECIMAL(12,2) COMMENT '金额',
    loan_date DATE COMMENT '借款日期',
    reason VARCHAR(500) COMMENT '借款原因',
    repayment_date DATE COMMENT '还款日期',
    status VARCHAR(20) COMMENT '状态',
    approver_id BIGINT COMMENT '审批人ID',
    approve_time DATETIME COMMENT '审批时间',
    approve_comment VARCHAR(500) COMMENT '审批意见',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '借款流程表';

-- 出差流程表
CREATE TABLE IF NOT EXISTS flow_travel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    applicant_id BIGINT COMMENT '申请人ID',
    destination VARCHAR(200) COMMENT '目的地',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    days DOUBLE COMMENT '天数',
    reason VARCHAR(500) COMMENT '出差原因',
    companions VARCHAR(200) COMMENT '同行人',
    budget DECIMAL(12,2) COMMENT '预算',
    status VARCHAR(20) COMMENT '状态',
    approver_id BIGINT COMMENT '审批人ID',
    approve_time DATETIME COMMENT '审批时间',
    approve_comment VARCHAR(500) COMMENT '审批意见',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '出差流程表';

-- 请假流程表
CREATE TABLE IF NOT EXISTS flow_leave (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    applicant_id BIGINT COMMENT '申请人ID',
    leave_type VARCHAR(50) COMMENT '请假类型',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    days DOUBLE COMMENT '天数',
    reason VARCHAR(500) COMMENT '请假原因',
    status VARCHAR(20) COMMENT '状态',
    approver_id BIGINT COMMENT '审批人ID',
    approve_time DATETIME COMMENT '审批时间',
    approve_comment VARCHAR(500) COMMENT '审批意见',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '请假流程表';
