package uk.deliriumdigital.kittyitems.controller;

import com.nftco.flow.sdk.FlowId;
import com.nftco.flow.sdk.FlowScriptResponse;
import com.nftco.flow.sdk.cadence.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.deliriumdigital.kittyitems.controller.dto.DataTransferObject;
import uk.deliriumdigital.kittyitems.exceptions.TransactionException;
import uk.deliriumdigital.kittyitems.flownftservice.KittyItemsFlowService;
import uk.deliriumdigital.kittyitems.model.KittyItem;
import uk.deliriumdigital.kittyitems.service.KittyItemsPersistenceService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/kitty-items")
public class KittyItemsRestController {

    private KittyItemsFlowService kittyService;
    private KittyItemsPersistenceService kittyPersistenceService;

    public KittyItemsRestController(KittyItemsFlowService kittyService, KittyItemsPersistenceService kittyPersistenceService) {
        this.kittyService = kittyService;
        this.kittyPersistenceService = kittyPersistenceService;
    }

    @GetMapping("/persist/{account}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> persistAll(@PathVariable String account) {

        var addr = checkFormat(account);

        FlowScriptResponse response = kittyService.getCollectionIds(addr);
        List<KittyItem> savedKittyItems = kittyPersistenceService.saveAll(response, addr);
        if(!savedKittyItems.isEmpty()) {
            return new ResponseEntity<String>("All Saved.", HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("Errors Encountered", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/mint")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> mint(@RequestBody DataTransferObject recipient) {

        var addr = checkFormat(recipient.getAccount());

        try {
            FlowId tx = kittyService.mint(addr);
            return new ResponseEntity<String>(tx.getBase16Value(), HttpStatus.OK);
        } catch (TransactionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @PostMapping("/mint-and-list")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> mintAndList(@RequestBody DataTransferObject recipient) {

        var addr = checkFormat(recipient.getAccount());

        try {
            FlowId tx = kittyService.mintAndList(addr);
            return new ResponseEntity<String>(tx.getBase16Value(), HttpStatus.OK);
        } catch (TransactionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @PostMapping("/setup")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> setupAccount() {

        try {
            FlowId tx = kittyService.setupAccount();
            return new ResponseEntity<String>(tx.getBase16Value(), HttpStatus.OK);
        } catch (TransactionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> transfer(@RequestBody DataTransferObject recipient) {

        var addr = checkFormat(recipient.getAccount());

        try {
            FlowId tx = kittyService.transfer(addr, recipient.getKittyItemId());
            return new ResponseEntity<String>(tx.getBase16Value(), HttpStatus.OK);
        } catch (TransactionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @GetMapping("/collection/{account}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getCollectionIds(@PathVariable String account) {

        var addr = checkFormat(account);

        FlowScriptResponse response = kittyService.getCollectionIds(addr);
        return new ResponseEntity<String>(response.getStringValue(), HttpStatus.OK);
    }

    @GetMapping("/item/{address}/{itemId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getKittyItem(@PathVariable("address") String address, @PathVariable("itemId") int itemId) {

        var addr = checkFormat(address);

        FlowScriptResponse response = kittyService.getKittyItem(addr, itemId);

        return new ResponseEntity<String>(response.getStringValue(), HttpStatus.OK);
    }

    @GetMapping("/supply")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getSupply() {

        FlowScriptResponse response = kittyService.getSupply();
        return new ResponseEntity<String>(response.getStringValue(), HttpStatus.OK);
    }

    private String checkFormat(String address) {
        if(address.startsWith("0x")) {
            address = address.substring(2);
        }

        return address;
    }
}
