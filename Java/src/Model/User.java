package Model;

import contract.Formatter;
import enums.UserRole;
import contract.Identifiable;
import java.util.UUID;

public class User implements Identifiable, Formatter {

    private final UUID id;
    private String username;
    private String email;
    private UserRole role;

    public User(String username, String email, UserRole role) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.role = role;
    }

    @Override
    public UUID getId() {
        return id;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }

    @Override
    public String format() {
        return String.format("Kullanıcı: %s (%s) - Rol: %s", username, email, role);
    }
}