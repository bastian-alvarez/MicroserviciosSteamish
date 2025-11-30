package com.gamestore.auth.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad para generar hashes BCrypt de contraseñas.
 * Ejecutar como aplicación Java para generar hashes.
 */
public class PasswordHashGenerator {
    public static void main(String[] args) {
        String password = "Admin123!";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));
        System.out.println("Contraseña: " + password);
        System.out.println("Hash BCrypt: " + hash);
        System.out.println();
        System.out.println("SQL UPDATE:");
        System.out.println("UPDATE admins SET password = '" + hash + "' WHERE email = 'admin@steamish.com';");
    }
}

