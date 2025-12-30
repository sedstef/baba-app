package at.fhj.softsec.baba.storage;

import at.fhj.softsec.baba.storage.model.User;

import java.util.Optional;

public interface UserRepository {

    boolean exists(String userId);

    Optional<User> findById(String userId);

    void save(User user);
}
