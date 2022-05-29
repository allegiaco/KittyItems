package uk.deliriumdigital.kittyitems.security.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import uk.deliriumdigital.kittyitems.security.model.Role;


public interface RoleRepository extends PagingAndSortingRepository<Role, Long> {

}
