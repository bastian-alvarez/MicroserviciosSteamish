package com.gamestore.auth.util;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Test para generar hash BCrypt de contraseñas.
 * Ejecutar este test para obtener el hash de "Admin123!"
 */
public class PasswordHashGeneratorTest {
    
    @Test
    void generateHashForAdmin123() {
        String password = "Admin123!";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));
        
        System.out.println("\n==========================================");
        System.out.println("Contraseña: " + password);
        System.out.println("Hash BCrypt: " + hash);
        System.out.println("\nSQL UPDATE:");
        System.out.println("UPDATE admins SET password = '" + hash + "' WHERE email = 'admin@steamish.com';");
        System.out.println("==========================================\n");
        
        // Verificar que el hash funciona
        assert BCrypt.checkpw(password, hash) : "El hash no coincide con la contraseña";
    }
}

