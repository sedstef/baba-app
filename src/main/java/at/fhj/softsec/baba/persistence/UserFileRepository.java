package at.fhj.softsec.baba.persistence;

import at.fhj.softsec.baba.domain.model.User;
import at.fhj.softsec.baba.domain.repository.UserRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class UserFileRepository implements UserRepository {

    private final Storage storage;

    public UserFileRepository(Storage storage) {
        this.storage = storage;
    }

    @Override
    public boolean exists(String userId) {
        return Files.exists(userFile(userId));
    }

    @Override
    public Optional<User> findById(String userId) {
        if (!exists(userId)) {
            return Optional.empty();
        }
        return Optional.of(storage.load(userFile(userId), User.class));
    }

    @Override
    public void save(User user) {
        storage.save(userFile(user.userId()), user);
    }

    private Path userFile(String userId) {
        return storage.userDir(userId).resolve("user.enc");
    }
}
