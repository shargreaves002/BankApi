package com.bankApi.bankApi.repositories;

import com.bankApi.bankApi.models.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillsRepository extends JpaRepository<Bill, Long> {

    List<Bill> findAllByAccountId(Long id);
}
