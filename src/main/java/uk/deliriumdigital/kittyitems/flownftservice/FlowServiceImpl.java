package uk.deliriumdigital.kittyitems.flownftservice;

import com.nftco.flow.sdk.*;
import uk.deliriumdigital.kittyitems.builders.FlowTransactionBuilder;
import uk.deliriumdigital.kittyitems.builders.ProposalKeyBuilder;
import uk.deliriumdigital.kittyitems.exceptions.ImportsException;
import uk.deliriumdigital.kittyitems.exceptions.TransactionException;
import uk.deliriumdigital.kittyitems.factories.BlockchainConnectionHandler;
import uk.deliriumdigital.kittyitems.reader.ReusableBufferedReader;
import uk.deliriumdigital.kittyitems.flownftservice.abstraction.FlowService;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.protobuf.ByteString;
import com.nftco.flow.sdk.crypto.Crypto;
import com.nftco.flow.sdk.crypto.PrivateKey;

@Service
public class FlowServiceImpl implements FlowService {

    @Value("${flowaccess.minterFlowAddress}")
    private String minterFlowAddress;
    private PrivateKey minterPrivateKey;

    private String userFlowAddress;
    private PrivateKey userPrivateKey;

    private FlowAccessApi accessAPI;

    private ReusableBufferedReader reader;

    public FlowServiceImpl(@Value("${flowaccess.privateKeyHex}") String minterPrivateKeyHex, ReusableBufferedReader rbr) {
        this.accessAPI = BlockchainConnectionHandler.getTestnetConnection();
        this.minterPrivateKey = Crypto.decodePrivateKey(minterPrivateKeyHex);
        this.reader = rbr;
    }

    public FlowAccount getAccount(FlowAddress address) {
        return this.accessAPI.getAccountAtLatestBlock(address);
    }

    public FlowAddress returnMinterAddress() {
        return new FlowAddress(minterFlowAddress);
    }

    public FlowAddress returnUserAddress() {
        return new FlowAddress(userFlowAddress);
    }

    public boolean setUserCredentials (String userFlowAddress, String userPrivateKey) {
        if (userFlowAddress.length() == 16 && userPrivateKey.length() == 64) {
            this.userFlowAddress = userFlowAddress;
            this.userPrivateKey = Crypto.decodePrivateKey(userPrivateKey);
            return true;
        }
        return false;
    }

    public BigDecimal getAccountBalance(FlowAddress address) {
        FlowAccount account = this.getAccount(address);
        return account.getBalance();
    }

    public Long getLatestBlockHeight() {
        return this.accessAPI.getLatestBlock(true).getHeight();
    }

    public FlowId sendTxMinterPay(String transaction, List<FlowArgument> argumentsList, Map<String, String> scriptChanges,
                                  FlowAddress proposerAddress, List<FlowAddress> authorizers, FlowAddress payerAddress, boolean skipSeal)
            throws TransactionException {

        FlowAccountKey payerAccountKey = this.getAccountKey(payerAddress, 0);
        FlowAccountKey proposerAccountKey = this.getAccountKey(proposerAddress, 0);
        FlowTransaction trx = null;
        try {
            trx = new FlowTransactionBuilder()
                    .addScript(new FlowScript(loadScript(transaction, scriptChanges)))
                    .addArgumentsList(argumentsList)
                    .setReferenceBlockId(this.getLatestBlockID())
                    .setGasLimit(9999L)
                    .setProposalKey(new ProposalKeyBuilder().setAddress(proposerAddress)
                                                            .setKeyIndex(proposerAccountKey.getId())
                                                            .setSequenceNumber(proposerAccountKey.getSequenceNumber())
                                                            .build())
                    .setPayerAddress(payerAddress)
                    .setAuthorizers(authorizers)
                    .build();
        } catch (ImportsException e) {
            e.printStackTrace();
        }

        Signer signer = Crypto.getSigner(this.minterPrivateKey, payerAccountKey.getHashAlgo());
        trx = trx.addEnvelopeSignature(payerAddress, payerAccountKey.getId(), signer);

        FlowId txID = this.accessAPI.sendTransaction(trx);

        if (skipSeal)
            return txID;

        this.waitForSeal(txID);
        return txID;
    }

    public FlowId sendTxUserPay(String transaction, List<FlowArgument> argumentsList, Map<String, String> scriptChanges,
                                FlowAddress proposerAddress, List<FlowAddress> authorizers, FlowAddress payerAddress, boolean skipSeal)
            throws TransactionException {

        if(this.userPrivateKey.equals(null)) {
            throw new TransactionException("User not set", PrivateKey.class);
        }

        FlowAccountKey payerAccountKey = this.getAccountKey(payerAddress, 0);
        FlowAccountKey proposerAccountKey = this.getAccountKey(proposerAddress, 0);

        FlowTransaction trx = null;
        try {
            trx = new FlowTransactionBuilder()
                    .addScript(new FlowScript(loadScript(transaction, scriptChanges)))
                    .addArgumentsList(argumentsList)
                    .setReferenceBlockId(this.getLatestBlockID()).setGasLimit(9999L)
                    .setProposalKey(new ProposalKeyBuilder().setAddress(proposerAddress)
                                                            .setKeyIndex(proposerAccountKey.getId())
                                                            .setSequenceNumber(proposerAccountKey.getSequenceNumber())
                                                            .build())
                    .setPayerAddress(payerAddress)
                    .setAuthorizers(authorizers)
                    .build();
        } catch (ImportsException e) {
            e.printStackTrace();
        }

        Signer signer = Crypto.getSigner(this.userPrivateKey, payerAccountKey.getHashAlgo());

        trx = trx.addEnvelopeSignature(payerAddress, payerAccountKey.getId(), signer);

        FlowId txID = this.accessAPI.sendTransaction(trx);

        if (skipSeal)
            return txID;

        this.waitForSeal(txID);
        return txID;
    }

    public FlowScriptResponse executeScript(String script, List<FlowArgument> argumentsList,
                                            Map<String, String> scriptChanges) {

        FlowScript flowScript = null;
        try {
          flowScript = new FlowScript(loadScript(script, scriptChanges));
        } catch (ImportsException e) {
            e.printStackTrace();
        }
        List<ByteString> arList = argumentsList.stream().map(flowArgument -> flowArgument.getByteStringValue())
                .collect(Collectors.toList());

        FlowScriptResponse response = this.accessAPI.executeScriptAtLatestBlock(flowScript, arList);
        return response;

    }

    private FlowId getLatestBlockID() {
        return this.accessAPI.getLatestBlockHeader().getId();
    }

    private FlowAccountKey getAccountKey(FlowAddress address, int keyIndex) {
        FlowAccount account = this.getAccount(address);
        return account.getKeys().get(keyIndex);
    }

    public FlowTransactionResult getTransactionResult(FlowId txID) {
        FlowTransactionResult result = this.accessAPI.getTransactionResultById(txID);
        return result;
    }

    private byte[] loadScript(String scriptPath, Map<String, String> scriptChanges) throws ImportsException {

        File scriptFile = new File(scriptPath);
        StringBuilder sb = new StringBuilder();

        try {
            reader.setSource(new FileReader(scriptFile));
            List<String> imports = new ArrayList<>();
            List<String> code = new ArrayList<>();
            String line = "";
            int numberOfImports = scriptChanges.size();

            // READ THE WHOLE CONTRACT AND DIVIDE IMPORTS FROM CODE

            do {
                line = reader.readLine();
                if (line != null) {
                    if(line.contains("import") && line.contains("from")) {
                        imports.add(line);
                    } else {
                        code.add(line);
                    }
                }

            } while (line != null);


            // CHECK IF THE IMPORTS CORRESPOND

            if(imports.size() != numberOfImports) {
                throw new ImportsException("Imports mismatch, check if you are using the correct script");
            }

            // CHANGE THE VALUES OF THE IMPORT

            imports = imports.stream().map(i -> {
                for (Map.Entry<String, String> change : scriptChanges.entrySet()) {
                    if (i.contains(change.getKey())) {
                        i = i.replace(change.getKey(), change.getValue());
                        scriptChanges.remove(change.getKey());
                        return i;
                    }
                }
                return i;
            }).collect(Collectors.toList());

            if (scriptChanges.size() != 0) {
                throw new ImportsException("Imports doesn't match");
            }

            imports.forEach(im -> sb.append(im + "\n"));
            code.forEach(co -> sb.append(co + "\n"));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString().getBytes();
    }

    private FlowTransactionResult waitForSeal(FlowId txID) {
        FlowTransactionResult txResult;

        while (true) {

            txResult = this.getTransactionResult(txID);
            if (txResult.getStatus().equals(FlowTransactionStatus.SEALED)) {
                return txResult;
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
