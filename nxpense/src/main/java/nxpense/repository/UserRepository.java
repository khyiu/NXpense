package nxpense.repository;

import nxpense.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer>{
	
	public User findByEmail(String email);
}