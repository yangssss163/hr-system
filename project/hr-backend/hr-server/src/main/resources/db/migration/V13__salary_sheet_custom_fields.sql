-- V13: 工资表增加自定义字段值存储（JSON格式）
ALTER TABLE sal_sheet ADD COLUMN field_values TEXT COMMENT '自定义字段值JSON，格式：{"code":"value",...}';
