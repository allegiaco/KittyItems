package uk.deliriumdigital.kittyitems.flownftservice.abstraction;


import uk.deliriumdigital.kittyitems.exceptions.TransactionException;

import com.nftco.flow.sdk.FlowAccount;
import com.nftco.flow.sdk.FlowAddress;
import com.nftco.flow.sdk.FlowArgument;
import com.nftco.flow.sdk.FlowId;
import com.nftco.flow.sdk.FlowScriptResponse;
import com.nftco.flow.sdk.FlowTransactionResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FlowService {

    public FlowAccount getAccount(FlowAddress address);


    public BigDecimal getAccountBalance(FlowAddress address);

    public Long getLatestBlockHeight();

    public FlowAddress returnMinterAddress();

    public FlowAddress returnUserAddress();

    public boolean setUserCredentials (String userFlowAddress, String userPrivateKey);

    public FlowScriptResponse executeScript(String script, List<FlowArgument> argumentsList,
                                            Map<String, String> scriptChanges);

    public FlowId sendTxMinterPay(String transaction, List<FlowArgument> argumentsList, Map<String, String> scriptChanges,
                                  FlowAddress proposerAddress, List<FlowAddress> authorizers, FlowAddress payerAddress, boolean skipSeal)
            throws TransactionException;

    public FlowId sendTxUserPay(String transaction, List<FlowArgument> argumentsList, Map<String, String> scriptChanges,
                                FlowAddress proposerAddress, List<FlowAddress> authorizers, FlowAddress payerAddress, boolean skipSeal)
            throws TransactionException;

    public FlowTransactionResult getTransactionResult(FlowId txID);

}
