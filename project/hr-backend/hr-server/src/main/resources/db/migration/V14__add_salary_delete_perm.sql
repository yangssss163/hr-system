-- V14: 添加工资表清除数据按钮权限
INSERT IGNORE INTO sys_menu (id, parent_id, name, type, path, component, permission, icon, sort, visible) VALUES
(135, 129, '清除数据', 3, '', '', 'salary:sheet:delete', '', 6, 1);
