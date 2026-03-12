-- Matrimony Application Database Schema

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100),
    gender VARCHAR(10),
    dob DATE,
    premium BOOLEAN DEFAULT FALSE,
    banned BOOLEAN DEFAULT FALSE,
    onboarding_complete BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_active TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Profiles table
CREATE TABLE IF NOT EXISTS profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    religion VARCHAR(50),
    caste VARCHAR(50),
    community VARCHAR(100),
    education VARCHAR(100),
    occupation VARCHAR(100),
    salary VARCHAR(50),
    smoking VARCHAR(20),
    drinking VARCHAR(20),
    diet VARCHAR(20),
    country VARCHAR(100),
    state VARCHAR(100),
    city VARCHAR(100),
    height VARCHAR(20),
    marital_status VARCHAR(30),
    about TEXT,
    photo_url VARCHAR(255),
    profile_completion INT DEFAULT 0,
    verified BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Partner Preferences table
CREATE TABLE IF NOT EXISTS partner_preferences (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    min_age INT,
    max_age INT,
    min_height VARCHAR(20),
    max_height VARCHAR(20),
    religion VARCHAR(50),
    caste VARCHAR(50),
    education VARCHAR(100),
    occupation VARCHAR(100),
    location VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Interests table
CREATE TABLE IF NOT EXISTS interests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    from_user BIGINT NOT NULL,
    to_user BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (from_user) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (to_user) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_interest (from_user, to_user)
);

-- Chats table
CREATE TABLE IF NOT EXISTS chats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user1 BIGINT NOT NULL,
    user2 BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user1) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (user2) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_chat (user1, user2)
);

-- Messages table
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    chat_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    read_status BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (chat_id) REFERENCES chats(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Subscriptions table
CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    plan VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    amount DECIMAL(10,2),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50),
    read_status BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Reports table
CREATE TABLE IF NOT EXISTS reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reporter_id BIGINT NOT NULL,
    reported_user_id BIGINT NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reported_user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- OTP table (temporary OTP storage)
CREATE TABLE IF NOT EXISTS otp_store (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone VARCHAR(20) NOT NULL,
    otp VARCHAR(10) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
