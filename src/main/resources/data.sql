-- Seed default roles (ignore if they already exist)
INSERT IGNORE INTO roles (role_name) VALUES ('ROLE_USER');
INSERT IGNORE INTO roles (role_name) VALUES ('ROLE_ADMIN');
