INSERT INTO users (username, email, password_hash, first_name, last_name, bio, created_at, updated_at, user_role)
-- TODO: Password need to be real hashed
VALUES
    ('john_doe', 'john@example.com', 'hash1', 'John', 'Doe', 'Hello, I am John', NOW(), NOW(), 'ROLE_USER'),
    ('jane_smith', 'jane@example.com', 'hash2', 'Jane', 'Smith', 'Hello, I am Jane', NOW(), NOW(), 'ROLE_USER'),
    ('admin', 'admin@example.com', 'hash3', 'Admin', 'User', 'Administrator account', NOW(), NOW(), 'ROLE_ADMIN');

INSERT INTO friendships (sender_id, receiver_id, status, created_at)
VALUES
    (1, 2, 'ACCEPTED', NOW()),
    (2, 3, 'PENDING', NOW()),
    (1, 3, 'BLOCKED', NOW());

INSERT INTO messages (sender_id, receiver_id, msg_content, sent_at)
VALUES
    (1, 2, 'Hello Jane!', NOW()),
    (2, 1, 'Hi John!', NOW());

INSERT INTO posts (user_id, post_content, created_at)
VALUES
    (1, 'John''s first post', NOW()),
    (2, 'Jane''s post', NOW());

INSERT INTO comments (post_id, user_id, comm_content, created_at)
VALUES
    (1, 2, 'Nice post, John!', NOW()),
    (2, 1, 'Thanks Jane!', NOW());

INSERT INTO communities (community_name, description, created_at)
VALUES
    ('Tech Enthusiasts', 'Community for tech lovers', NOW()),
    ('Book Club', 'Community for book readers', NOW());

INSERT INTO members (community_id, user_id, member_role, joined_at)
VALUES
    (1, 1, 'OWNER', NOW()),
    (1, 2, 'MEMBER', NOW()),
    (2, 2, 'OWNER', NOW()),
    (2, 3, 'MEMBER', NOW());

INSERT INTO community_posts (community_id, user_id, post_content, created_at)
VALUES
    (1, 1, 'Welcome to Tech Enthusiasts!', NOW()),
    (2, 2, 'Welcome to Book Club!', NOW());
