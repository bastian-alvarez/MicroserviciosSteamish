const db = require('../config/database');
const bcrypt = require('bcryptjs');

class Admin {
    static async create(adminData) {
        const hashedPassword = await bcrypt.hash(adminData.password, 10);
        const [result] = await db.execute(
            `INSERT INTO admins (name, email, phone, password, role, profile_photo_uri) 
             VALUES (?, ?, ?, ?, ?, ?)`,
            [
                adminData.name,
                adminData.email,
                adminData.phone,
                hashedPassword,
                adminData.role,
                adminData.profilePhotoUri || null
            ]
        );
        return this.findById(result.insertId);
    }

    static async findById(id) {
        const [rows] = await db.execute(
            'SELECT id, name, email, phone, role, profile_photo_uri, created_at FROM admins WHERE id = ?',
            [id]
        );
        return rows[0] || null;
    }

    static async findByEmail(email) {
        const [rows] = await db.execute(
            'SELECT * FROM admins WHERE email = ?',
            [email]
        );
        return rows[0] || null;
    }

    static async getAll() {
        const [rows] = await db.execute(
            'SELECT id, name, email, phone, role, profile_photo_uri, created_at FROM admins ORDER BY id DESC'
        );
        return rows;
    }

    static async verifyPassword(plainPassword, hashedPassword) {
        return bcrypt.compare(plainPassword, hashedPassword);
    }
}

module.exports = Admin;

