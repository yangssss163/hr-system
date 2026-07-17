-- 补充系统管理菜单 (ID 1-27) 和 办公/CRM 模块菜单权限
-- ==============================================

-- 重新插入系统管理菜单（如果已删除）
INSERT IGNORE INTO sys_menu (id, parent_id, name, type, path, component, permission, icon, sort, visible) VALUES
(1, 0, '系统管理', 1, '/system', '', '', 'Setting', 1, 1),
(2, 1, '公司架构', 2, '/system/company', 'system/company/CompanyManage', 'system:company:list', 'OfficeBuilding', 1, 1),
(3, 2, '新增公司', 3, '', '', 'system:company:create', '', 1, 1),
(4, 2, '编辑公司', 3, '', '', 'system:company:edit', '', 2, 1),
(5, 2, '删除公司', 3, '', '', 'system:company:delete', '', 3, 1),
(6, 1, '部门架构', 2, '/system/dept', 'system/dept/DeptManage', 'system:dept:list', 'Menu', 2, 1),
(7, 6, '新增部门', 3, '', '', 'system:dept:create', '', 1, 1),
(8, 6, '编辑部门', 3, '', '', 'system:dept:edit', '', 2, 1),
(9, 6, '删除部门', 3, '', '', 'system:dept:delete', '', 3, 1),
(10, 1, '职位管理', 2, '/system/position', 'system/position/PositionManage', 'system:position:list', 'Medal', 3, 1),
(11, 10, '新增职位', 3, '', '', 'system:position:create', '', 1, 1),
(12, 10, '编辑职位', 3, '', '', 'system:position:edit', '', 2, 1),
(13, 10, '删除职位', 3, '', '', 'system:position:delete', '', 3, 1),
(14, 1, '用户管理', 2, '/system/user', 'system/user/UserManage', 'system:user:list', 'User', 4, 1),
(15, 14, '新增用户', 3, '', '', 'system:user:create', '', 1, 1),
(16, 14, '编辑用户', 3, '', '', 'system:user:edit', '', 2, 1),
(17, 14, '删除用户', 3, '', '', 'system:user:delete', '', 3, 1),
(18, 14, '分配角色', 3, '', '', 'system:user:assign-role', '', 4, 1),
(19, 1, '角色权限', 2, '/system/role', 'system/role/RoleManage', 'system:role:list', 'Key', 5, 1),
(20, 19, '新增角色', 3, '', '', 'system:role:create', '', 1, 1),
(21, 19, '编辑角色', 3, '', '', 'system:role:edit', '', 2, 1),
(22, 19, '删除角色', 3, '', '', 'system:role:delete', '', 3, 1),
(23, 19, '分配权限', 3, '', '', 'system:role:assign-menu', '', 4, 1),
(24, 1, '菜单管理', 2, '/system/menu', 'system/menu/MenuManage', 'system:menu:list', 'List', 6, 1),
(25, 24, '新增菜单', 3, '', '', 'system:menu:create', '', 1, 1),
(26, 24, '编辑菜单', 3, '', '', 'system:menu:edit', '', 2, 1),
(27, 24, '删除菜单', 3, '', '', 'system:menu:delete', '', 3, 1);

-- ============ 办公管理菜单 ============
INSERT IGNORE INTO sys_menu (id, parent_id, name, type, path, component, permission, icon, sort, visible) VALUES
(134, 0, '办公管理', 1, '/office', '', '', 'Notebook', 7, 1),
(135, 134, '公告管理', 2, '/office/notice', 'office/notice/NoticeManage', 'office:notice:list', 'Bell', 1, 1),
(136, 135, '发布公告', 3, '', '', 'office:notice:create', '', 1, 1),
(137, 135, '编辑公告', 3, '', '', 'office:notice:update', '', 2, 1),
(138, 135, '删除公告', 3, '', '', 'office:notice:delete', '', 3, 1),
(139, 134, '文档管理', 2, '/office/document', 'office/document/DocumentManage', 'office:document:list', 'Files', 2, 1),
(140, 139, '创建文档', 3, '', '', 'office:document:create', '', 1, 1),
(141, 139, '编辑文档', 3, '', '', 'office:document:update', '', 2, 1),
(142, 139, '删除文档', 3, '', '', 'office:document:delete', '', 3, 1),
(143, 134, '任务管理', 2, '/office/task', 'office/task/TaskManage', 'office:task:list', 'Check', 3, 1),
(144, 143, '创建任务', 3, '', '', 'office:task:create', '', 1, 1),
(145, 143, '编辑任务', 3, '', '', 'office:task:update', '', 2, 1),
(146, 143, '删除任务', 3, '', '', 'office:task:delete', '', 3, 1),
(147, 134, '日程管理', 2, '/office/schedule', 'office/schedule/ScheduleManage', 'office:schedule:list', 'Calendar', 4, 1),
(148, 147, '创建日程', 3, '', '', 'office:schedule:create', '', 1, 1),
(149, 147, '编辑日程', 3, '', '', 'office:schedule:update', '', 2, 1),
(150, 147, '删除日程', 3, '', '', 'office:schedule:delete', '', 3, 1),
(151, 134, '消息管理', 2, '/office/message', 'office/message/MessageManage', 'office:message:list', 'ChatLineSquare', 5, 1),
(152, 151, '发送消息', 3, '', '', 'office:message:create', '', 1, 1),
(153, 151, '删除消息', 3, '', '', 'office:message:delete', '', 2, 1);

-- ============ CRM 管理菜单 ============
INSERT IGNORE INTO sys_menu (id, parent_id, name, type, path, component, permission, icon, sort, visible) VALUES
(154, 0, 'CRM管理', 1, '/crm', '', '', 'Connection', 8, 1),
(155, 154, '客户管理', 2, '/crm/customer', 'crm/customer/CustomerManage', 'crm:customer:list', 'Avatar', 1, 1),
(156, 155, '新增客户', 3, '', '', 'crm:customer:create', '', 1, 1),
(157, 155, '编辑客户', 3, '', '', 'crm:customer:update', '', 2, 1),
(158, 155, '删除客户', 3, '', '', 'crm:customer:delete', '', 3, 1),
(159, 154, '商机管理', 2, '/crm/opportunity', 'crm/opportunity/OpportunityManage', 'crm:opportunity:list', 'TrendCharts', 2, 1),
(160, 159, '新增商机', 3, '', '', 'crm:opportunity:create', '', 1, 1),
(161, 159, '编辑商机', 3, '', '', 'crm:opportunity:update', '', 2, 1),
(162, 159, '删除商机', 3, '', '', 'crm:opportunity:delete', '', 3, 1),
(163, 154, '订单管理', 2, '/crm/order', 'crm/order/OrderManage', 'crm:order:list', 'Document', 3, 1),
(164, 163, '新增订单', 3, '', '', 'crm:order:create', '', 1, 1),
(165, 163, '编辑订单', 3, '', '', 'crm:order:update', '', 2, 1),
(166, 163, '删除订单', 3, '', '', 'crm:order:delete', '', 3, 1),
(167, 154, '回款管理', 2, '/crm/payment', 'crm/payment/PaymentManage', 'crm:payment:list', 'Money', 4, 1),
(168, 167, '新增回款', 3, '', '', 'crm:payment:create', '', 1, 1),
(169, 167, '编辑回款', 3, '', '', 'crm:payment:update', '', 2, 1),
(170, 167, '删除回款', 3, '', '', 'crm:payment:delete', '', 3, 1),
(171, 154, '退款管理', 2, '/crm/refund', 'crm/refund/RefundManage', 'crm:refund:list', 'RefreshRight', 5, 1),
(172, 171, '新增退款', 3, '', '', 'crm:refund:create', '', 1, 1),
(173, 171, '编辑退款', 3, '', '', 'crm:refund:update', '', 2, 1),
(174, 171, '删除退款', 3, '', '', 'crm:refund:delete', '', 3, 1),
(175, 154, '费用管理', 2, '/crm/expense', 'crm/expense/ExpenseManage', 'crm:expense:list', 'CreditCard', 6, 1),
(176, 175, '新增费用', 3, '', '', 'crm:expense:create', '', 1, 1),
(177, 175, '编辑费用', 3, '', '', 'crm:expense:update', '', 2, 1),
(178, 175, '删除费用', 3, '', '', 'crm:expense:delete', '', 3, 1);

-- ============ 工作流管理菜单 ============
INSERT IGNORE INTO sys_menu (id, parent_id, name, type, path, component, permission, icon, sort, visible) VALUES
(179, 0, '工作流', 1, '/workflow', '', '', 'Promotion', 9, 1),
(180, 179, '报销审批', 2, '/workflow/expense', 'workflow/expense/ExpenseManage', 'workflow:expense:list', 'Money', 1, 1),
(181, 180, '发起报销', 3, '', '', 'workflow:expense:create', '', 1, 1),
(182, 180, '编辑报销', 3, '', '', 'workflow:expense:update', '', 2, 1),
(183, 180, '删除报销', 3, '', '', 'workflow:expense:delete', '', 3, 1),
(184, 180, '审批报销', 3, '', '', 'workflow:expense:approve', '', 4, 1),
(185, 179, '借款审批', 2, '/workflow/loan', 'workflow/loan/LoanManage', 'workflow:loan:list', 'CreditCard', 2, 1),
(186, 185, '发起借款', 3, '', '', 'workflow:loan:create', '', 1, 1),
(187, 185, '编辑借款', 3, '', '', 'workflow:loan:update', '', 2, 1),
(188, 185, '删除借款', 3, '', '', 'workflow:loan:delete', '', 3, 1),
(189, 185, '审批借款', 3, '', '', 'workflow:loan:approve', '', 4, 1),
(190, 179, '出差审批', 2, '/workflow/travel', 'workflow/travel/TravelManage', 'workflow:travel:list', 'Place', 3, 1),
(191, 190, '发起出差', 3, '', '', 'workflow:travel:create', '', 1, 1),
(192, 190, '编辑出差', 3, '', '', 'workflow:travel:update', '', 2, 1),
(193, 190, '删除出差', 3, '', '', 'workflow:travel:delete', '', 3, 1),
(194, 190, '审批出差', 3, '', '', 'workflow:travel:approve', '', 4, 1),
(195, 179, '请假审批', 2, '/workflow/leave', 'workflow/leave/LeaveManage', 'workflow:leave:list', 'Sunny', 4, 1),
(196, 195, '发起请假', 3, '', '', 'workflow:leave:create', '', 1, 1),
(197, 195, '编辑请假', 3, '', '', 'workflow:leave:update', '', 2, 1),
(198, 195, '删除请假', 3, '', '', 'workflow:leave:delete', '', 3, 1),
(199, 195, '审批请假', 3, '', '', 'workflow:leave:approve', '', 4, 1);

-- 为超级管理员分配所有新菜单权限
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id <= 27;

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id BETWEEN 134 AND 199;
