package io.wannabit.core.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.wannabit.core.entity.Account;
import lombok.extern.slf4j.Slf4j;

// 해당 클래스는 사용할일이 많지 않음
// 사용자 메소드를 만드는 규칙만 지킨다면
// 굳이 Repository test class가 존재할 필요가 없음
// Mokito를 이용한 샘플

//@RunWith(SpringRunner.class)
//@DataJpaTest
@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class AccountRepoTest {

  @Mock AccountRepo accountRepo;

  @BeforeClass
  public static void beforeClass() {
    System.out.println("Before Class");
  }

  @Before
  public void before() {
    System.out.println("Before");
    assertNotNull(accountRepo);
  }

  @Test
  public void testFindByEmail() {

    List<Account> accountList = new ArrayList<Account>();
    Account account1 = new Account();
    account1.setIdfAccount(1);
    account1.setEmail("nix80csh@gmail.com");
    account1.setPassword("d6ff13a6fba2e7ccdd30016912fc1f3a15962762f76df8820adcdb6ffad40ec4");
    account1.setAuthCodeEmail("-confirmed");

    Account account2 = new Account();
    account2.setIdfAccount(2);
    account2.setEmail("hoon@wannabit.io");
    account2.setPassword("d6ff13a6fba2e7ccdd30016912fc1f3a15962762f76df8820adcdb6ffad40ec4");
    account2.setAuthCodeEmail("-confirmed");

    assertTrue(account1 != null);
    assertTrue(account2 != null);

    accountList.add(account1);
    accountList.add(account2);
    when(accountRepo.findAll()).thenReturn(accountList);

    log.info("findAll() calling");
    List<Account> resultAccountList = accountRepo.findAll();

    log.info("findAll() - Asserting that the result is not null or empty");
    assertNotNull(resultAccountList);
    assertFalse(resultAccountList.isEmpty());


    when(accountRepo.findByEmail("nix80csh@gmail.com")).thenReturn(account1);
    Account _account = accountRepo.findByEmail("nix80csh@gmail.com");

    verify(accountRepo, times(1)).findByEmail("nix80csh@gmail.com");

    assertThat(_account, is(accountList.get(0)));
    assertThat(_account.getEmail(), is("nix80csh@gmail.com"));
    System.out.println(_account.getAuthCodeEmail());

  }

  @After
  public void after() {
    System.out.println("After");
  }

  @AfterClass
  public static void afterClass() {
    System.out.println("After Class");
  }

}
