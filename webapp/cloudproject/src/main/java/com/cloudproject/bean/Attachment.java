package com.cloudproject.bean;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name="attachmentTbl")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", name = "attachmentid")
    private UUID attachmentid;

    @Column(name="url")
    private String url;

//    @Column(name="id")
//    private UUID transactionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id", nullable = false)
    @JsonIgnore
    private Transaction transaction;

    public Attachment(String url, UUID transactionId) {
        this.url = url;
//        this.transactionId = transactionId;
    }

    public Attachment() {
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

//    public UUID getTransactionId() {
//        return transactionId;
//    }
//
//    public void setTransactionId(UUID transactionId) {
//        this.transactionId = transactionId;
//    }

    public void setAttachmentid(UUID attachmentid) {
        this.attachmentid = attachmentid;
    }

    public UUID getAttachmentid() {
        return attachmentid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
