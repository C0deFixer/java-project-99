package hexlet.code.repository;

import hexlet.code.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    public User findByEmail(String email);
}
