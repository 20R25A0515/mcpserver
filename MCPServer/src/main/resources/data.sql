INSERT INTO employee (name, email, position, sick_leaves, casual_leaves, earned_leaves)
VALUES
('John Doe', 'john@example.com', 'Software Engineer', 2, 7, 11)
ON CONFLICT (email) DO NOTHING;
