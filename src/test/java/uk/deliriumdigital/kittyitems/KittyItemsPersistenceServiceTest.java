package uk.deliriumdigital.kittyitems;

import com.nftco.flow.sdk.FlowScriptResponse;
import com.nftco.flow.sdk.cadence.*;
import org.junit.jupiter.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import uk.deliriumdigital.kittyitems.flownftservice.KittyItemsFlowService;
import uk.deliriumdigital.kittyitems.model.KittyItem;
import uk.deliriumdigital.kittyitems.model.enums.Kind;
import uk.deliriumdigital.kittyitems.model.enums.Rarity;
import uk.deliriumdigital.kittyitems.repository.KittyItemsRepository;
import uk.deliriumdigital.kittyitems.service.KittyItemsPersistenceService;

import javax.swing.text.html.Option;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class KittyItemsPersistenceServiceTest {

    @Test
    public void testSaveAll() {

        //
        // Given
        //
        KittyItemsRepository kittyItemsRepository = mock(KittyItemsRepository.class);
        KittyItemsFlowService kittyItemsFlowService = mock(KittyItemsFlowService.class);

        CompositeValue kind = new CompositeValue("A.0042e6f28d52f7d7.KittyItems.Kind",
                new CompositeAttribute[] {
                        new CompositeAttribute("rawValue", new UInt8NumberField("2"))
                });

        CompositeValue rarity = new CompositeValue("A.0042e6f28d52f7d7.KittyItems.Rarity",
                new CompositeAttribute[] {
                        new CompositeAttribute("rawValue", new UInt8NumberField("2"))
                });

        CompositeValue v = new CompositeValue("s.aae598d40ca252c1cbe2c9b947c5311a2b7b0588c75160396b4ebca9641e162c.KittyItem",
                new CompositeAttribute[] {
                        new CompositeAttribute("name", new StringField("Green Milkshake")),
                        new CompositeAttribute("description", new StringField("A green milkshake with serial number 3")),
                        new CompositeAttribute("thumbnail", new StringField("https://bafybeihjy4rcbvnw6bcz3zbirq5u454aagnyzjhlrffgkc25wgdcw4csoe.ipfs.dweb.link/sm.png")),
                        new CompositeAttribute("itemID", new UInt64NumberField("3")),
                        new CompositeAttribute("resourceID", new UInt64NumberField("89674497")),
                        new CompositeAttribute("kind", new EnumField(kind)),
                        new CompositeAttribute("rarity", new EnumField(rarity)),
                        new CompositeAttribute("owner", new AddressField("0xd34e6d685806bcd1"))
                });

        FlowScriptResponse flowScriptResponse1 = new FlowScriptResponse(new OptionalField(new StructField(v)));

        CompositeValue kind2 = new CompositeValue("A.0042e6f28d52f7d7.KittyItems.Kind",
                new CompositeAttribute[] {
                        new CompositeAttribute("rawValue", new UInt8NumberField("4"))
                });

        CompositeValue rarity2 = new CompositeValue("A.0042e6f28d52f7d7.KittyItems.Rarity",
                new CompositeAttribute[] {
                        new CompositeAttribute("rawValue", new UInt8NumberField("3"))
                });

        CompositeValue v2 = new CompositeValue("s.aae598d40ca252c1cbe2c9b947c5311a2b7b0588c75160396b4ebca9641e162c.KittyItem",
                new CompositeAttribute[] {
                        new CompositeAttribute("name", new StringField("Blue Skateboard")),
                        new CompositeAttribute("description", new StringField("A blue skateboard with serial number 7")),
                        new CompositeAttribute("thumbnail", new StringField("https://bafybeic55lpwfvucmgibbvaury3rpeoxmcgyqra3vdhjwp74wqzj6oqvpq.ipfs.dweb.link/sm.png")),
                        new CompositeAttribute("itemID", new UInt64NumberField("7")),
                        new CompositeAttribute("resourceID", new UInt64NumberField("95214755")),
                        new CompositeAttribute("kind", new EnumField(kind2)),
                        new CompositeAttribute("rarity", new EnumField(rarity2)),
                        new CompositeAttribute("owner", new AddressField("0xd34e6d685806bcd1"))
                });

        FlowScriptResponse flowScriptResponse2 = new FlowScriptResponse(new OptionalField(new StructField(v2)));

        when(kittyItemsFlowService.getKittyItem("0xd34e6d685806bcd1", 3)).thenReturn(flowScriptResponse1);
        when(kittyItemsFlowService.getKittyItem("0xd34e6d685806bcd1", 7)).thenReturn(flowScriptResponse2);
        when(kittyItemsRepository.save(any(KittyItem.class))).then(returnsFirstArg());

        ArrayField arrayField = new ArrayField( new Field<?>[] {
                new UInt64NumberField("3"),
                new UInt64NumberField("7")
        });

        FlowScriptResponse flowScriptResponse3 = new FlowScriptResponse(arrayField);

        KittyItemsPersistenceService kittyItemsPersistenceService = new KittyItemsPersistenceService(kittyItemsRepository, kittyItemsFlowService);


        //
        // When
        //
        List<KittyItem> savedKittyItems =kittyItemsPersistenceService.saveAll(flowScriptResponse3, "0xd34e6d685806bcd1");
        KittyItem kittyItem1 = savedKittyItems.get(0);
        KittyItem kittyItem2 = savedKittyItems.get(1);

        //
        // Then
        //
        assertAll(() -> assertThat(savedKittyItems.size()).isEqualTo(2),
                () -> assertThat(kittyItem1.getItemID()).isEqualTo(3),
                () -> assertThat(kittyItem1.getResourceID()).isEqualTo(89674497L),
                () -> assertThat(kittyItem1.getDescription()).isEqualTo("A green milkshake with serial number 3"),
                () -> assertThat(kittyItem2.getItemID()).isEqualTo(7),
                () -> assertThat(kittyItem2.getResourceID()).isEqualTo(95214755L),
                () -> assertThat(kittyItem2.getDescription()).isEqualTo("A blue skateboard with serial number 7")
        );

    }
}
