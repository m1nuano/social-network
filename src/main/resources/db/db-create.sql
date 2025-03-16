CREATE DATABASE socialnetwork;
\c socialnetwork

CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR,
                       email VARCHAR,
                       password_hash VARCHAR,
                       first_name VARCHAR,
                       last_name VARCHAR,
                       bio TEXT,
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP,
                       user_role VARCHAR -- USER, ADMIN
);

CREATE TABLE friendships (
                             id SERIAL PRIMARY KEY,
                             sender_id INT,
                             receiver_id INT,
                             status varchar, -- PENDING, ACCEPTED, DECLINED, BLOCKED
                             created_at TIMESTAMP,
                             FOREIGN KEY (sender_id) REFERENCES Users(id),
                             FOREIGN KEY (receiver_id) REFERENCES Users(id)
);

CREATE TABLE messages (
                                  id SERIAL PRIMARY KEY,
                                  sender_id INT,
                                  receiver_id INT,
                                  msg_content TEXT,
                                  sent_at TIMESTAMP,
                                  FOREIGN KEY (sender_id) REFERENCES Users(id),
                                  FOREIGN KEY (receiver_id) REFERENCES Users(id)
);

CREATE TABLE posts (
                       id SERIAL PRIMARY KEY,
                       user_id INT,
                       post_content TEXT,
                       created_at TIMESTAMP,
                       FOREIGN KEY (user_id) REFERENCES Users(id)
);

CREATE TABLE comments (
                          id SERIAL PRIMARY KEY,
                          post_id INT,
                          user_id INT,
                          comm_content TEXT,
                          created_at TIMESTAMP,
                          FOREIGN KEY (post_id) REFERENCES Posts(id),
                          FOREIGN KEY (user_id) REFERENCES Users(id)
);

CREATE TABLE communities (
                    id SERIAL PRIMARY KEY,
                    community_name varchar,
                    description TEXT,
                    created_at TIMESTAMP
);

CREATE TABLE members (
                         id SERIAL PRIMARY KEY,
                         community_id INT NOT NULL,
                         user_id INT NOT NULL,
                         member_role VARCHAR, -- OWNER, ADMIN, MEMBER, BLOCKED
                         joined_at TIMESTAMP,
                         UNIQUE (community_id, user_id),
                         FOREIGN KEY (community_id) REFERENCES communities(id),
                         FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE community_posts (
                                 id SERIAL PRIMARY KEY,
                                 community_id INT,
                                 user_id INT,
                                 post_content varchar,
                                 created_at TIMESTAMP,
                                 FOREIGN KEY (user_id) REFERENCES Users(id),
                                 FOREIGN KEY (community_id) REFERENCES Communities(id)
);

