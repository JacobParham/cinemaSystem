package com.cinema.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "users")
@SecondaryTable(
        name = "customers",
        pkJoinColumns = @PrimaryKeyJoinColumn(
                name = "customer_id",
                referencedColumnName = "user_id"
        )
)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer accountId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role = "CUSTOMER";

    @Column(name = "first_name", table = "customers", nullable = false)
    private String firstName;

    @Column(name = "last_name", table = "customers", nullable = false)
    private String lastName;

    @Column(name = "status", table = "customers", nullable = false)
    private String status = "Inactive";

    @Column(name = "phone", table = "customers")
    private String phone;

    /*
     * Your new schema has no promotions column.
     * Keeping this transient prevents the existing frontend from crashing,
     * but the value will not be saved in the database.
     */
    @Transient
    private Boolean promotions = false;

    public Account() {
    }

    public Account(
            String firstName,
            String lastName,
            String email,
            String password,
            boolean promotions,
            String role
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.promotions = promotions;
        this.role = role;
        this.status = "Inactive";
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getPromotions() {
        return promotions;
    }

    public void setPromotions(Boolean promotions) {
        this.promotions = promotions;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}