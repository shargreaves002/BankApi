package com.bankApi.bankApi.repositories;

import com.bankApi.bankApi.models.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositsRepository extends JpaRepository<Deposit, Long> {

    List<Deposit> findAllByAccountId(Long accountId);
}
