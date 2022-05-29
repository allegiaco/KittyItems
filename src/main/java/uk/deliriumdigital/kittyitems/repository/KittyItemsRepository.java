package uk.deliriumdigital.kittyitems.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.deliriumdigital.kittyitems.model.KittyItem;

@Repository
public interface KittyItemsRepository extends JpaRepository<KittyItem, Long> {


}
