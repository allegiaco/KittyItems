package uk.deliriumdigital.kittyitems.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.deliriumdigital.kittyitems.security.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByUsername(String nome);


}
