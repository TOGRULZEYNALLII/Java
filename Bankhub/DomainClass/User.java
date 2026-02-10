package DomainClass;
import interfaces.Identifiable;
import java.util.UUID;
import interfaces.Formatter;
public class User implements  Identifiable, Formatter  {
//    id, name, email
    private final UUID id;
    private final String name;
    private final String email;
    public User(UUID id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    @Override
    public String format() {
        return "User ID: " + id +
                ", Name: " + name +
                ", Email: " + email;
    }
    @Override
    public UUID getId() {
        return id;
    }

}

