package io.wannabit.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.wannabit.core.entity.AccountTx;

public interface AccountTxRepo extends JpaRepository<AccountTx, Integer> {

  boolean existsByTxhash(String txhash);

  List<AccountTx> findByAccountIdfAccountAndTypeBlockchainOrderByIdfAccountTxDesc(
      Integer idfAccount, String typeBlockchain);

}
