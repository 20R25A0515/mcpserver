CREATE TABLE IF NOT EXISTS employee (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100),
  email VARCHAR(150) UNIQUE,
  position VARCHAR(100),
  sick_leaves INT DEFAULT 0,
  casual_leaves INT DEFAULT 0,
  earned_leaves INT DEFAULT 0
);
