package at.fhj.softsec.baba.domain.repository;

import at.fhj.softsec.baba.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    boolean exists(String userId);

    Optional<User> findById(String userId);

    User save(User user);
}
