package uk.deliriumdigital.kittyitems.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.deliriumdigital.kittyitems.model.enums.Kind;
import uk.deliriumdigital.kittyitems.model.enums.Rarity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KittyItem {

    @Id
    private Long itemID;
    private String name;
    private String description;
    private String thumbnail;
    private Long resourceID;
    private Kind kind;
    private Rarity rarity;
    private String owner;


}
