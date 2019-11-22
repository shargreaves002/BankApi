package com.bankApi.bankApi.repositories;

import com.bankApi.bankApi.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountsRepository extends JpaRepository<Account, Long> {
    List<Account> findAllByCustomerId(Long id);
}
