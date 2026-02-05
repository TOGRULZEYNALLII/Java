package repository;
import interfaces.Identifiable;
import interfaces.Repository;

import java.util.*;

public class InMemoryRepository<T extends Identifiable> implements Repository<T>{
    private final Map<UUID, T> store = new HashMap<>();

    @Override
    public T save(T entity) {
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<T> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean deleteById(UUID id) {
        return store.remove(id) != null;
    }
}
