package com.bankApi.bankApi.models;

import javax.persistence.*;


public class Account {

        private Long id;
        // A list of named constant and defines a class type, Enumerations can have constructors, methods and instance variables.
        @Enumerated(EnumType.STRING)
        private AccountType type;
        private String nickname;
        private int rewards;
        private double balance;

        private long customerId;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public AccountType getType() {
            return type;
        }

        public void setType(AccountType type) {
            this.type = type;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public Integer getRewards() {
            return rewards;
        }

        public void setRewards(Integer rewards) {
            this.rewards = rewards;
        }

        public Double getBalance() {
            /*Double balance = 0.0;
            if (deposits != null) {
                for (Deposit i : deposits) {
                    if (i.getMedium().equals(TransactionMedium.Balance) && i.getStatus().equals(TransactionStatus.Completed))
                        balance += i.getAmount();
                }
            }
            if (withdraws != null) {
                for (Withdraw i : withdraws) {
                    if (i.getMedium().equals(TransactionMedium.Balance) && i.getStatus().equals(TransactionStatus.Completed))
                        balance -= i.getAmount();
                }
            }*/
            return balance;
        }

        public void setBalance(Double balance) {
            this.balance = balance;
        }

        public long getCustomerId() {
            return customerId;
        }

        public void setCustomerId(long customerId) {
            this.customerId = customerId;
        }
}
