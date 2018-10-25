package com.cloudproject.bean;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="transactionTbl")
public class Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", name = "id")
    private UUID id;

    @Column(name="username")
    private String username;

    @Column(name="description")
    private String description;

    @Column(name="merchant")
    private String merchant;

    @Column(name="amount")
    private String amount;

    @Column(name="date")
    private String date;

    @Column(name="category")
    private String category;

    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = "transaction",orphanRemoval=true)
    private List<Attachment> attachments = new ArrayList<>();

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public Transaction() {
    }

    public Transaction(String username, String description, String merchant, String amount, String date, String category) {
        this.username = username;
        this.description = description;
        this.merchant = merchant;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


}
