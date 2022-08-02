package uk.deliriumdigital.kittyitems.flownftservice;

import com.nftco.flow.sdk.FlowAddress;
import com.nftco.flow.sdk.FlowArgument;
import com.nftco.flow.sdk.FlowId;
import com.nftco.flow.sdk.FlowScriptResponse;
import org.springframework.stereotype.Service;
import uk.deliriumdigital.kittyitems.builders.ArgumentsBuilder;
import uk.deliriumdigital.kittyitems.exceptions.ArgumentNotFoundException;
import uk.deliriumdigital.kittyitems.exceptions.TransactionException;
import uk.deliriumdigital.kittyitems.model.enums.Kind;
import uk.deliriumdigital.kittyitems.model.enums.Rarity;
import uk.deliriumdigital.kittyitems.flownftservice.abstraction.FlowService;

import java.util.*;

@Service
public class KittyItemsFlowService {

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

    public KittyItemsFlowService(FlowService flowService) {
        this.flowService = flowService;
    }

    private Random rand = new Random();

    private Kind randomKind() {

        Kind[] kindArray = Kind.values();
        return kindArray[rand.nextInt(kindArray.length)];
    }

    private Rarity randomRarity() {
        Rarity[] rarityArray = Rarity.values();
        return rarityArray[rand.nextInt(rarityArray.length)];
    }

    public boolean setUser(String userFlowAddress, String userPrivateKey) {
        return flowService.setUserCredentials(userFlowAddress, userPrivateKey);
    }

    public FlowId setupAccount() throws TransactionException {

        FlowAddress userAddress = this.flowService.returnUserAddress();

        String transaction = "./cadence/transactions/kittyItems/setup_account.cdc";
        Map<String, String> scriptChanges = new HashMap<String, String>();
        scriptChanges.put(nonFungibleTokenPath, "0x".concat(nonFungibleTokenAddress));
        scriptChanges.put(kittyItemsPath, "0x".concat(kittyItemsAddress));
        List<FlowArgument> argumentsList = new ArrayList<>();

        return flowService.sendTxUserPay(transaction, argumentsList, scriptChanges, userAddress,
                Arrays.asList(userAddress), userAddress, false);
    }

    public FlowId setupStoreFrontOnAccount() throws TransactionException {

        var minterAddress = flowService.returnMinterAddress();
        String transaction = "./cadence/transactions/nftStorefront/setup_account.cdc";
        Map<String, String> scriptChanges = new HashMap<String, String>();
        scriptChanges.put(storefrontPath, "0x".concat(storefrontAddress));
        List<FlowArgument> argumentsList = new ArrayList<>();

        return flowService.sendTxMinterPay(transaction, argumentsList, scriptChanges, minterAddress,
                Arrays.asList(minterAddress), minterAddress, false);
    }

    public FlowId deleteOldCollection() throws TransactionException {

        var minterAddress = flowService.returnMinterAddress();
        String transaction = "./cadence/transactions/kittyItems/delete_old_transaction.cdc";
        Map<String, String> scriptChanges = new HashMap<String, String>();
        List<FlowArgument> argumentsList = new ArrayList<>();

        return flowService.sendTxMinterPay(transaction, argumentsList, scriptChanges, minterAddress,
                Arrays.asList(minterAddress), minterAddress, false);
    }

    public FlowId mint(String recipient) throws TransactionException, ArgumentNotFoundException {

        var minterAddress = flowService.returnMinterAddress();
        String transaction = "./cadence/transactions/kittyItems/mint_kitty_item.cdc";
        Map<String, String> scriptChanges = new HashMap<String, String>();
        scriptChanges.put(nonFungibleTokenPath, "0x".concat(nonFungibleTokenAddress));
        scriptChanges.put(kittyItemsPath, "0x".concat(kittyItemsAddress));

        var kind = randomKind();
        var rarity = randomRarity();

        var argumentsList = new ArgumentsBuilder()
                    .addressField(recipient)
                    .numberField("UInt8NumberField", String.valueOf(kind.ordinal()))
                    .numberField("UInt8NumberField", String.valueOf(rarity.ordinal()))
                    .build();

        return flowService.sendTxMinterPay(transaction, argumentsList, scriptChanges, minterAddress,
                Arrays.asList(minterAddress), minterAddress, true);
    }

    public FlowId mintAndList(String recipient) throws TransactionException, ArgumentNotFoundException {

        var minterAddress = flowService.returnMinterAddress();
        String transaction = "./cadence/transactions/kittyItems/mint_and_list_kitty_item.cdc";
        Map<String, String> scriptChanges = new HashMap<String, String>();
        scriptChanges.put(nonFungibleTokenPath, "0x".concat(nonFungibleTokenAddress));
        scriptChanges.put(kittyItemsPath, "0x".concat(kittyItemsAddress));
        scriptChanges.put(fungibleTokenPath, "0x".concat(fungibleTokenAddress));
        scriptChanges.put(flowTokenPath, "0x".concat(flowTokenAddress));
        scriptChanges.put(storefrontPath, "0x".concat(storefrontAddress));

        var kind = randomKind();
        var rarity = randomRarity();

        var argumentsList = new ArgumentsBuilder()
                    .addressField(recipient)
                    .numberField("UInt8NumberField", String.valueOf(kind.ordinal()))
                    .numberField("UInt8NumberField", String.valueOf(rarity.ordinal()))
                    .build();


        return flowService.sendTxMinterPay(transaction, argumentsList, scriptChanges, minterAddress,
                Arrays.asList(minterAddress), minterAddress, false);

    }

    public FlowId purchaseListing(String account, Long listingId) throws TransactionException, ArgumentNotFoundException {

        var userAddress = flowService.returnUserAddress();

        String transaction = "./cadence/transactions/nftStorefront/purchase_listing.cdc";
        Map<String, String> scriptChanges = new HashMap<String, String>();
        scriptChanges.put(nonFungibleTokenPath, "0x".concat(nonFungibleTokenAddress));
        scriptChanges.put(kittyItemsPath, "0x".concat(kittyItemsAddress));
        scriptChanges.put(fungibleTokenPath, "0x".concat(fungibleTokenAddress));
        scriptChanges.put(flowTokenPath, "0x".concat(flowTokenAddress));
        scriptChanges.put(storefrontPath, "0x".concat(storefrontAddress));


        var argumentsList = new ArgumentsBuilder()
                    .numberField("UInt64NumberField", listingId.toString())
                    .addressField(account)
                    .build();

        return flowService.sendTxUserPay(transaction, argumentsList, scriptChanges, userAddress,
                Arrays.asList(userAddress), userAddress, false);

    }

    public FlowId transfer(String recipient, Long id) throws TransactionException, ArgumentNotFoundException {

        var minterAddress = flowService.returnMinterAddress();
        String transaction = "./cadence/transactions/kittyItems/transfer_kitty_item.cdc";
        Map<String, String> scriptChanges = new HashMap<String, String>();
        scriptChanges.put(nonFungibleTokenPath, "0x".concat(nonFungibleTokenAddress));
        scriptChanges.put(kittyItemsPath, "0x".concat(kittyItemsAddress));

        var argumentsList = new ArgumentsBuilder()
                    .addressField(recipient)
                    .numberField("UInt64NumberField", String.valueOf(id))
                    .build();

        return flowService.sendTxMinterPay(transaction, argumentsList, scriptChanges, minterAddress,
                Arrays.asList(minterAddress), minterAddress, true);
    }

    public FlowScriptResponse getCollectionIds(String account) {

        String script = "./cadence/scripts/kittyItems/get_collection_ids.cdc";
        Map<String, String> scriptChanges = new HashMap<String, String>();
        scriptChanges.put(nonFungibleTokenPath, "0x".concat(nonFungibleTokenAddress));
        scriptChanges.put(kittyItemsPath, "0x".concat(kittyItemsAddress));


        var argumentsList = new ArgumentsBuilder()
                .addressField(account)
                .build();

        return flowService.executeScript(script, argumentsList, scriptChanges);

    }

    public FlowScriptResponse getListings() {

        String script = "./cadence/scripts/nftStorefront/get_listings.cdc";
        Map<String, String> scriptChanges = new HashMap<String, String>();
        scriptChanges.put(storefrontPath, "0x".concat(storefrontAddress));

        var argumentsList = new ArgumentsBuilder()
                .addressField("0042e6f28d52f7d7")
                .build();

        return flowService.executeScript(script, argumentsList, scriptChanges);

    }


    public FlowScriptResponse getKittyItem(String address, int number) throws ArgumentNotFoundException {

        String script = "./cadence/scripts/kittyItems/get_kitty_item.cdc";
        Map<String, String> scriptChanges = new HashMap<String, String>();
        scriptChanges.put(nonFungibleTokenPath, "0x".concat(nonFungibleTokenAddress));
        scriptChanges.put(metaDataViewsPath, "0x".concat(metadataViewsAddress));
        scriptChanges.put(kittyItemsPath, "0x".concat(kittyItemsAddress));

        var argumentsList = new ArgumentsBuilder()
                    .addressField(address)
                    .numberField("UInt64NumberField", String.valueOf(number))
                    .build();

        return flowService.executeScript(script, argumentsList, scriptChanges);

    }

    public FlowScriptResponse getSupply() {

        String script = "./cadence/scripts/kittyItems/get_kitty_items_supply.cdc";
        Map<String, String> scriptChanges = new HashMap<String, String>();
        scriptChanges.put(kittyItemsPath, "0x".concat(kittyItemsAddress));

        var argumentsList = new ArgumentsBuilder()
                .build();

        return flowService.executeScript(script, argumentsList, scriptChanges);

    }
}
