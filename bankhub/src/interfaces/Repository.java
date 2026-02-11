package interfaces;
import interfaces.Identifiable;
import java.util.List;     // List kullanabilmek için
import java.util.Optional; // Optional kullanabilmek için
import java.util.UUID;     // UUID kullanabilmek için
public interface Repository <T extends  Identifiable >{

    T save(T entity);
    Optional<T> findById(UUID id);
    List<T> findAll();
    boolean deleteById(UUID id);
}
