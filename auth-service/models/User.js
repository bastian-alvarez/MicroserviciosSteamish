const db = require('../config/database');
const bcrypt = require('bcryptjs');

class User {
    static async create(userData) {
        const hashedPassword = await bcrypt.hash(userData.password, 10);
        const [result] = await db.execute(
            `INSERT INTO users (name, email, phone, password, profile_photo_uri, is_blocked, gender) 
             VALUES (?, ?, ?, ?, ?, ?, ?)`,
            [
                userData.name,
                userData.email,
                userData.phone,
                hashedPassword,
                userData.profilePhotoUri || null,
                userData.isBlocked || false,
                userData.gender || ''
            ]
        );
        return this.findById(result.insertId);
    }

    static async findById(id) {
        const [rows] = await db.execute(
            'SELECT id, name, email, phone, profile_photo_uri, is_blocked, gender, created_at, updated_at FROM users WHERE id = ?',
            [id]
        );
        return rows[0] || null;
    }

    static async findByEmail(email) {
        const [rows] = await db.execute(
            'SELECT * FROM users WHERE email = ?',
            [email]
        );
        return rows[0] || null;
    }

    static async getAll() {
        const [rows] = await db.execute(
            'SELECT id, name, email, phone, profile_photo_uri, is_blocked, gender, created_at FROM users ORDER BY id DESC'
        );
        return rows;
    }

    static async update(id, userData) {
        const updates = [];
        const values = [];

        if (userData.name) {
            updates.push('name = ?');
            values.push(userData.name);
        }
        if (userData.phone) {
            updates.push('phone = ?');
            values.push(userData.phone);
        }
        if (userData.profilePhotoUri !== undefined) {
            updates.push('profile_photo_uri = ?');
            values.push(userData.profilePhotoUri);
        }
        if (userData.gender !== undefined) {
            updates.push('gender = ?');
            values.push(userData.gender);
        }

        if (updates.length === 0) return null;

        values.push(id);
        await db.execute(
            `UPDATE users SET ${updates.join(', ')}, updated_at = CURRENT_TIMESTAMP WHERE id = ?`,
            values
        );
        return this.findById(id);
    }

    static async block(id) {
        await db.execute('UPDATE users SET is_blocked = TRUE, updated_at = CURRENT_TIMESTAMP WHERE id = ?', [id]);
        return this.findById(id);
    }

    static async unblock(id) {
        await db.execute('UPDATE users SET is_blocked = FALSE, updated_at = CURRENT_TIMESTAMP WHERE id = ?', [id]);
        return this.findById(id);
    }

    static async verifyPassword(plainPassword, hashedPassword) {
        return bcrypt.compare(plainPassword, hashedPassword);
    }
}

module.exports = User;

