package uk.deliriumdigital.kittyitems.flownftservice;

import com.nftco.flow.sdk.FlowAddress;
import com.nftco.flow.sdk.FlowArgument;
import com.nftco.flow.sdk.FlowId;
import com.nftco.flow.sdk.FlowScriptResponse;
import org.springframework.stereotype.Service;
import uk.deliriumdigital.kittyitems.builders.ArgumentsBuilder;
import uk.deliriumdigital.kittyitems.exceptions.ArgumentNotFoundException;
import uk.deliriumdigital.kittyitems.exceptions.TransactionException;
import uk.deliriumdigital.kittyitems.flownftservice.abstraction.FlowService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StoreFrontService {

    private static final String nonFungibleTokenPath = "\"../../contracts/NonFungibleToken.cdc\"";
    private static final String metaDataViewsPath = "\"../../contracts/MetadataViews.cdc\"";
    private static final String kittyItemsPath = "\"../../contracts/KittyItems.cdc\"";
    private static final String fungibleTokenPath = "\"../../contracts/FungibleToken.cdc\"";
    private static final String flowTokenPath = "\"../../contracts/FlowToken.cdc\"";
    private static final String storefrontPath = "\"../../contracts/NFTStorefront.cdc\"";


    private FlowService flowService;

    private String nonFungibleTokenAddress = "631e88ae7f1d7c20";
    private String metadataViewsAddress = "631e88ae7f1d7c20";
    private String kittyItemsAddress = "0042e6f28d52f7d7";
    private String fungibleTokenAddress = "9a0766d93b6608b7";
    private String flowTokenAddress = "7e60df042a9c0868";
    private String storefrontAddress = "94b06cfca1d8a476";

    private String minterAddress;

    public StoreFrontService(FlowService flowService) {
        this.flowService = flowService;
        this.minterAddress = flowService.returnMinterAddress().getBase16Value();
    }

    public FlowId setupAccount() throws TransactionException {

        FlowAddress userAddress = this.flowService.returnUserAddress();
        String transaction = "./cadence/transactions/nftStorefront/setup_account.cdc";
        Map<String, String> scriptChanges = new HashMap<String, String>();
        scriptChanges.put(storefrontPath, "0x".concat(storefrontAddress));
        var argumentsList = new ArgumentsBuilder().build();

        return flowService.sendTxMinterPay(transaction, argumentsList, scriptChanges, userAddress,
                Arrays.asList(userAddress), userAddress, false);
    }

    public FlowScriptResponse getItem(String address, int number) throws ArgumentNotFoundException {

        String script = "./cadence/scripts/nftStorefront/get_listing.cdc";
        Map<String, String> scriptChanges = new HashMap<String, String>();
        scriptChanges.put(storefrontPath, "0x".concat(storefrontAddress));

        var argumentsList = new ArgumentsBuilder()
                    .addressField(address)
                    .numberField("UInt64NumberField", String.valueOf(number))
                    .build();

        return flowService.executeScript(script, argumentsList, scriptChanges);

    }

    public FlowScriptResponse getItems(String address) {

        String script = "./cadence/scripts/nftStorefront/get_listings.cdc";
        Map<String, String> scriptChanges = new HashMap<String, String>();
        scriptChanges.put(storefrontPath, "0x".concat(storefrontAddress));

        var argumentsList = new ArgumentsBuilder()
                .addressField(address)
                .build();

        return flowService.executeScript(script, argumentsList, scriptChanges);

    }

    public FlowId buy(Long number) throws TransactionException, ArgumentNotFoundException {

        FlowAddress userAddress = this.flowService.returnUserAddress();
        String transaction = "./cadence/transactions/nftStorefront/purchase_listing.cdc";
        Map<String, String> scriptChanges = new HashMap<String, String>();
        scriptChanges.put(nonFungibleTokenPath, "0x".concat(nonFungibleTokenAddress));
        scriptChanges.put(kittyItemsPath, "0x".concat(kittyItemsAddress));
        scriptChanges.put(fungibleTokenPath, "0x".concat(fungibleTokenAddress));
        scriptChanges.put(flowTokenPath, "0x".concat(flowTokenAddress));
        scriptChanges.put(storefrontPath, "0x".concat(storefrontAddress));

        var argumentsList = new ArgumentsBuilder()
                    .numberField("UInt64NumberField", String.valueOf(number))
                    .addressField(minterAddress)
                    .build();

        return flowService.sendTxUserPay(transaction, argumentsList, scriptChanges, userAddress,
                Arrays.asList(userAddress), userAddress, false);
    }

    public FlowId sell(Long number, BigDecimal price) throws TransactionException, ArgumentNotFoundException {

        FlowAddress userAddress = this.flowService.returnUserAddress();
        String transaction = "./cadence/transactions/nftStorefront/create_listing.cdc";
        Map<String, String> scriptChanges = new HashMap<String, String>();
        scriptChanges.put(nonFungibleTokenPath, "0x".concat(nonFungibleTokenAddress));
        scriptChanges.put(kittyItemsPath, "0x".concat(userAddress.getBase16Value()));
        scriptChanges.put(fungibleTokenPath, "0x".concat(fungibleTokenAddress));
        scriptChanges.put(flowTokenPath, "0x".concat(flowTokenAddress));
        scriptChanges.put(storefrontPath, "0x".concat(storefrontAddress));

        var argumentsList = new ArgumentsBuilder()
                    .numberField("UInt64NumberField", String.valueOf(number))
                    .numberField("UFix64NumberField", price.setScale(8).toPlainString())
                    .build();

        return flowService.sendTxMinterPay(transaction, argumentsList, scriptChanges, userAddress,
                Arrays.asList(userAddress), userAddress, true);
    }

    public FlowScriptResponse getListingItem(String account, Long listingResourceId) throws ArgumentNotFoundException {

        String script = "./cadence/scripts/nftStorefront/get_listing_item.cdc";
        Map<String, String> scriptChanges = new HashMap<String, String>();
        scriptChanges.put(nonFungibleTokenPath, "0x".concat(nonFungibleTokenAddress));
        scriptChanges.put(metaDataViewsPath, "0x".concat(metadataViewsAddress));
        scriptChanges.put(kittyItemsPath, "0x".concat(kittyItemsAddress));
        scriptChanges.put(storefrontPath, "0x".concat(storefrontAddress));

        var argumentsList = new ArgumentsBuilder()
                    .addressField(account)
                    .numberField("UInt64NumberField", String.valueOf(listingResourceId))
                    .build();

        return flowService.executeScript(script, argumentsList, scriptChanges);
    }
}
