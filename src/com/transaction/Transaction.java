package com.transaction;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import com.NoobChain;
import com.utils.StringUtil;

public class Transaction {

    private String transactionId; // this is also the hash of the transaction.
    private PublicKey sender; // senders address/public key.
    private PublicKey reciepient; // Recipients address/public key.
    private float value;
    private byte[] signature; // this is to prevent anybody else from spending funds in our wallet.

    private List<TransactionInput> inputs = new ArrayList<TransactionInput>();
    private List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0; // a rough count of how many transactions have been generated.

    // Constructor:
    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
    }

    // This Calculates the transaction hash (which will be used as its Id)
    private String calulateHash() {
        sequence++; // increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value) + sequence);
    }

    // Signs all the data we dont wish to be tampered with.
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    // Verifies the data we signed hasnt been tampered with
    public boolean verifiySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    // Returns true if new transaction could be created.
    public boolean processTransaction() {

        if (verifiySignature() == false) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        // gather transaction inputs (Make sure they are unspent):
        for (TransactionInput i : inputs) {
            i.setUTXO(NoobChain.getUTXOs().get(i.getTransactionOutputId()));
        }

        // check if transaction is valid:
        if (getInputsValue() < NoobChain.getMinimumTransaction()) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        // generate transaction outputs:
        float leftOver = getInputsValue() - value; // get value of inputs then the left over change:
        transactionId = calulateHash();
        outputs.add(new TransactionOutput(this.reciepient, value, transactionId)); // send value to recipient
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId)); // send the left over 'change' back to sender

        // add outputs to Unspent list
        for (TransactionOutput o : outputs) {
            NoobChain.getUTXOs().put(o.getId(), o);
        }

        // remove transaction inputs from UTXO lists as spent:
        for (TransactionInput i : inputs) {
            if (i.getUTXO() == null)
                continue; // if Transaction can't be found skip it
            NoobChain.getUTXOs().remove(i.getUTXO().getId());
        }

        return true;
    }

    // returns sum of inputs(UTXOs) values
    public float getInputsValue() {
        float total = 0;
        for (TransactionInput i : inputs) {
            if (i.getUTXO() == null)
                continue; // if Transaction can't be found skip it
            total += i.getUTXO().getValue();
        }
        return total;
    }

    // returns sum of outputs:
    public float getOutputsValue() {
        float total = 0;
        for (TransactionOutput o : outputs) {
            total += o.getValue();
        }
        return total;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PublicKey getSender() {
        return sender;
    }

    public void setSender(PublicKey sender) {
        this.sender = sender;
    }

    public PublicKey getReciepient() {
        return reciepient;
    }

    public void setReciepient(PublicKey reciepient) {
        this.reciepient = reciepient;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public List<TransactionInput> getInputs() {
        return inputs;
    }

    public void setInputs(List<TransactionInput> inputs) {
        this.inputs = inputs;
    }

    public List<TransactionOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<TransactionOutput> outputs) {
        this.outputs = outputs;
    }

}