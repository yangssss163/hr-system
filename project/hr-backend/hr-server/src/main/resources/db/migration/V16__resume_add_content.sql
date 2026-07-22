-- 简历表新增详细简历内容字段
ALTER TABLE rec_resume ADD COLUMN resume_content TEXT COMMENT '详细简历内容（富文本）' AFTER resume_file;
