package com.transaction;

public class TransactionInput {
    private String transactionOutputId; // Reference to TransactionOutputs -> transactionId
    private TransactionOutput UTXO; // Contains the Unspent transaction output

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }

    public String getTransactionOutputId() {
        return transactionOutputId;
    }

    public void setTransactionOutputId(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }

    public TransactionOutput getUTXO() {
        return UTXO;
    }

    public void setUTXO(TransactionOutput uTXO) {
        UTXO = uTXO;
    }

}
