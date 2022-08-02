package uk.deliriumdigital.kittyitems.security.service;


import uk.deliriumdigital.kittyitems.security.model.User;

import java.util.Optional;

public interface AbstractUserService {

	Optional<User> saveUser(User u);

}
