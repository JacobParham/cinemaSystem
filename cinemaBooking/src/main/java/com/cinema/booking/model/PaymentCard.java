package com.cinema.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment_cards")
public class PaymentCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private int cardId;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "card_holder", nullable = false)
    private String cardHolder;

    // encrypted card number
    @Column(name = "card_number_enc", nullable = false, columnDefinition = "TEXT")
    private String cardNumberEnc;

    @Column(name = "card_last4", nullable = false)
    private String last4;

    @Column(name = "expiration", nullable = false)
    private String expiration;

    // encrypted CVV (if stored at all)
    @Column(name = "cvv_enc", columnDefinition = "TEXT")
    private String cvvEnc;

    public PaymentCard() {}

    public int getCardId() { return cardId; }
    public void setCardId(int cardId) { this.cardId = cardId; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public String getCardHolder() { return cardHolder; }
    public void setCardHolder(String cardHolder) { this.cardHolder = cardHolder; }

    public String getCardNumberEnc() { return cardNumberEnc; }
    public void setCardNumberEnc(String cardNumberEnc) { this.cardNumberEnc = cardNumberEnc; }

    public String getLast4() { return last4; }
    public void setLast4(String last4) { this.last4 = last4; }

    public String getExpiration() { return expiration; }
    public void setExpiration(String expiration) { this.expiration = expiration; }

    public String getCvvEnc() { return cvvEnc; }
    public void setCvvEnc(String cvvEnc) { this.cvvEnc = cvvEnc; }
}
