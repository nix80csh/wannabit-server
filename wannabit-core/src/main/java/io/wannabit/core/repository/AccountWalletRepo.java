package io.wannabit.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.wannabit.core.entity.AccountWallet;
import io.wannabit.core.entity.AccountWalletPK;

public interface AccountWalletRepo extends JpaRepository<AccountWallet, AccountWalletPK> {
  List<AccountWallet> findByAccountIdfAccount(int idfAccount);

  List<AccountWallet> findByAccountIdfAccountAndTypeBlockchainOrderByRegDateAsc(int IdfAccount,
      String typeBlockchain);
}
