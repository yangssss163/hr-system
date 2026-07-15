-- =============================================
-- V8: 添加考核记录删除权限
-- 新增 perf:record:delete 权限，挂载到考核记录菜单下
-- 自动授权给超级管理员角色
-- =============================================

-- 1. 添加考核记录-删除按钮权限（父菜单 ID=112 考核记录）
INSERT IGNORE INTO sys_menu (parent_id, name, type, path, component, permission, icon, sort, visible)
VALUES (112, '删除记录', 3, '', '', 'perf:record:delete', '', 3, 1);

-- 2. 授予超级管理员角色该权限
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE permission = 'perf:record:delete';
