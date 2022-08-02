package uk.deliriumdigital.kittyitems.service;

import com.nftco.flow.sdk.FlowScriptResponse;
import com.nftco.flow.sdk.cadence.CompositeAttribute;
import com.nftco.flow.sdk.cadence.CompositeValue;
import com.nftco.flow.sdk.cadence.Field;
import com.nftco.flow.sdk.cadence.StructField;
import org.springframework.stereotype.Service;
import uk.deliriumdigital.kittyitems.exceptions.ArgumentNotFoundException;
import uk.deliriumdigital.kittyitems.flownftservice.KittyItemsFlowService;
import uk.deliriumdigital.kittyitems.model.KittyItem;
import uk.deliriumdigital.kittyitems.model.enums.Kind;
import uk.deliriumdigital.kittyitems.model.enums.Rarity;
import uk.deliriumdigital.kittyitems.repository.KittyItemsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KittyItemsPersistenceService {

    private KittyItemsRepository repository;
    private KittyItemsFlowService kittyService;


    public KittyItemsPersistenceService(KittyItemsRepository repository, KittyItemsFlowService kittyItemsFlowService) {
        this.repository = repository;
        this.kittyService = kittyItemsFlowService;
    }

    public KittyItem save(KittyItem kittyItem) {
        return repository.save(kittyItem);
    }

    public List<KittyItem> saveAll(FlowScriptResponse response, String addr) {

        Field<?>[] arrayField = (Field<?>[]) response.getJsonCadence().getValue();
        var listId = Arrays.asList(arrayField).stream()
                .map(v -> v.getValue())
                .map(v -> Integer.valueOf(v.toString()))
                .collect(Collectors.toList());

        var listKittyItems = listId.stream().map(id -> {

            FlowScriptResponse resp = null;

            try {
                resp = kittyService.getKittyItem(addr, id);
            } catch (ArgumentNotFoundException e) {
                e.printStackTrace();
            }
            var sField = (StructField) resp.getJsonCadence().getValue();

                    var map= Arrays.asList(sField.getValue().getFields())
                            .stream().collect(Collectors.toMap(CompositeAttribute::getName, c -> {
                                if (!c.getValue().getValue().getClass().equals(CompositeValue.class)) {
                                    return String.valueOf(c.getValue().getValue());
                                }
                                var compositeValue = (CompositeValue) c.getValue().getValue();
                                var compositeAttributes = Arrays.asList(compositeValue.getFields());
                                var value = String.valueOf(compositeAttributes.get(0)
                                        .getValue().getValue());
                                return value;
                            }));
                    return map;
                }).collect(Collectors.toList());

        List<KittyItem> savedKittyItems = new ArrayList<>();
        listKittyItems.forEach(ki -> {
            var kittyItem = new KittyItem();
            kittyItem.setOwner(ki.get("owner"));
            kittyItem.setItemID(Long.parseLong(ki.get("itemID")));
            kittyItem.setThumbnail(ki.get("thumbnail"));
            kittyItem.setResourceID(Long.valueOf(ki.get("resourceID")));
            kittyItem.setKind(Kind.values()[Integer.parseInt(ki.get("kind"))]);
            kittyItem.setName(ki.get("name"));
            kittyItem.setDescription(ki.get("description"));
            kittyItem.setRarity(Rarity.values()[Rarity.values().length - 1 - Integer.parseInt(ki.get("rarity"))]);
            savedKittyItems.add(repository.save(kittyItem));
        });
        return savedKittyItems;
    }

    public List<KittyItem> getAll() {
        return repository.findAll();
    }

}
