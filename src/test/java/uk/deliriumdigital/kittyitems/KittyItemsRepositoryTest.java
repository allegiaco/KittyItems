package uk.deliriumdigital.kittyitems;

import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.deliriumdigital.kittyitems.model.KittyItem;
import uk.deliriumdigital.kittyitems.model.enums.Kind;
import uk.deliriumdigital.kittyitems.model.enums.Rarity;
import uk.deliriumdigital.kittyitems.repository.KittyItemsRepository;

import java.util.List;

@SpringBootTest
@Testcontainers
public class KittyItemsRepositoryTest {

    @Container
    private static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private KittyItemsRepository kittyItemsRepository;

    private KittyItem kittyItem1;
    private KittyItem kittyItem2;


    public KittyItemsRepositoryTest() {
        this.kittyItem1 = KittyItem.builder().itemID(1L).description("A purple milkshake with serial number 1")
                                             .kind(Kind.Milkshake).name("Purple Milkshake").owner("0xd34e6d685806bcd1")
                                             .rarity(Rarity.PURPLE).resourceID(12345678L).thumbnail("url here").build();

        this.kittyItem2 = KittyItem.builder().itemID(2L).description("A blue skateboard with serial number 2")
                .kind(Kind.Skateboard).name("Blue Skateboard").owner("0xd34e6d685806efg2")
                .rarity(Rarity.BLUE).resourceID(24680246L).thumbnail("url2 here").build();
    }

    @Test
    public void testFindAllKittyItems (){

        List<KittyItem> kittyItemList = List.of(kittyItem1, kittyItem2);
        kittyItemsRepository.saveAll(kittyItemList);

        List<KittyItem> returnedKittyItemList = kittyItemsRepository.findAll();

        assertEquals(kittyItemList.size(), returnedKittyItemList.size());

        KittyItem KittyItemReturned1 = returnedKittyItemList.get(0);
        KittyItem KittyItemReturned2 = returnedKittyItemList.get(1);

        assertEquals(KittyItemReturned1.getItemID().longValue(), 1L);
        assertEquals(KittyItemReturned1.getDescription(), "A purple milkshake with serial number 1");
        assertEquals(KittyItemReturned1.getKind().ordinal(), 2);
        assertEquals(KittyItemReturned1.getName(), "Purple Milkshake");
        assertEquals(KittyItemReturned1.getOwner(), "0xd34e6d685806bcd1");
        assertEquals(KittyItemReturned1.getRarity().ordinal(), 1);
        assertEquals(KittyItemReturned1.getResourceID().longValue(), 12345678L);
        assertEquals(KittyItemReturned1.getThumbnail(), "url here");

        assertEquals(KittyItemReturned2.getItemID().longValue(), 2L);
        assertEquals(KittyItemReturned2.getDescription(), "A blue skateboard with serial number 2");
        assertEquals(KittyItemReturned2.getKind().ordinal(), 4);
        assertEquals(KittyItemReturned2.getName(), "Blue Skateboard");
        assertEquals(KittyItemReturned2.getOwner(), "0xd34e6d685806efg2");
        assertEquals(KittyItemReturned2.getRarity().ordinal(), 3);
        assertEquals(KittyItemReturned2.getResourceID().longValue(), 24680246L);
        assertEquals(KittyItemReturned2.getThumbnail(), "url2 here");



    }
}
