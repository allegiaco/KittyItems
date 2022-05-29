package uk.deliriumdigital.kittyitems.controller;

import com.nftco.flow.sdk.FlowId;
import com.nftco.flow.sdk.FlowScriptResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.deliriumdigital.kittyitems.controller.dto.DataTransferObject;
import uk.deliriumdigital.kittyitems.exceptions.TransactionException;
import uk.deliriumdigital.kittyitems.flownftservice.StoreFrontService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/market")
public class StoreFrontRestController {

    private StoreFrontService service;

    public StoreFrontRestController(StoreFrontService service) {
        this.service = service;
    }

    @PostMapping("/buy")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> buy(@RequestBody DataTransferObject dto) {

        try {
            FlowId tx = service.buy(dto.getKittyItemId());
            return new ResponseEntity<String>(tx.getBase16Value(), HttpStatus.OK);
        } catch (TransactionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @PostMapping("/setup")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> setup() {

        try {
            FlowId tx = service.setupAccount();
            return new ResponseEntity<String>(tx.getBase16Value(), HttpStatus.OK);
        } catch (TransactionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @PostMapping("/sell")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> sell(@RequestBody DataTransferObject dto) {

        // Check conversion from Long to BigDecimal

        BigDecimal priceBigDec = BigDecimal.valueOf(dto.getPrice());

        try {
            FlowId tx = service.sell(dto.getKittyItemId(), priceBigDec);
            return new ResponseEntity<String>(tx.getBase16Value(), HttpStatus.OK);
        } catch (TransactionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @GetMapping("/collection/{account}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getItems(@PathVariable String account) {

        var addr = checkFormat(account);

        FlowScriptResponse response = service.getItems(addr);
        return new ResponseEntity<String>(response.getStringValue(), HttpStatus.OK);
    }

    @GetMapping("/collection/{account}/{item}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getItem(@PathVariable String account, @PathVariable int item) {

        var addr = checkFormat(account);

        FlowScriptResponse response = service.getItem(addr, item);
        return new ResponseEntity<String>(response.getStringValue(), HttpStatus.OK);
    }

    @GetMapping("/collection/getlistingitem/{account}/{item}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getListingItem(@PathVariable String account, @PathVariable Long item) {

        var addr = checkFormat(account);

        FlowScriptResponse response = service.getListingItem(addr, item);
        return new ResponseEntity<String>(response.getStringValue(), HttpStatus.OK);
    }

    @GetMapping("/market/latest")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> findMostRecentSales() {

        return new ResponseEntity<>("Still to be implemented", HttpStatus.OK);
    }

    @GetMapping("/market/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> findListing(@PathVariable int id) {

        return new ResponseEntity<>("Still to be implemented", HttpStatus.OK);
    }

    private String checkFormat(String address) {
        if(address.startsWith("0x")) {
            address = address.substring(2);
        }

        return address;
    }
}
