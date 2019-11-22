package com.bankApi.bankApi.repositories;

import com.bankApi.bankApi.models.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalsRepository extends JpaRepository<Withdrawal, Long> {

    List<Withdrawal> findAllByAccountId(Long accountId);
}
