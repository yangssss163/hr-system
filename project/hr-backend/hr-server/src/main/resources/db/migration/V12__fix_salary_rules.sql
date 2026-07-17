-- ============================================================================
-- V12 - 修复薪酬核算规则
-- ============================================================================

-- 修正社保比例: 0.105 → 10.5 (generate() 中会除以100)
UPDATE sal_rule SET value = 10.50 WHERE type = 'social_insurance';

-- 添加综合税率（generate() 代码中使用 getRuleValue(rules, "tax")）
INSERT IGNORE INTO sal_rule (type, value, unit, remark) VALUES
('tax_rate', 3.0, '%', '综合所得税率');

-- 如果有后台代码改为使用 tax_rate，也兼容保留原 tax 类型映射
INSERT INTO sys_menu (id, parent_id, name, type, path, component, permission, icon, sort, visible) VALUES
(134, 133, '编辑工资单', 3, '', '', 'salary:sheet:edit', '', 5, 1)
ON DUPLICATE KEY UPDATE permission = 'salary:sheet:edit';

INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 134) ON DUPLICATE KEY UPDATE role_id = 1;
