package io.wannabit.wallet;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"io.wannabit.core.repository"})
@EntityScan(basePackages = {"io.wannabit.core.entity"})
public class PersistenceConfig {

  @Autowired DataSource dataSource;

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
    emf.setPackagesToScan("io.wannabit.core.entity");
    emf.setPersistenceProvider(new HibernatePersistenceProvider());
    Properties jpaProperties = new Properties();
    jpaProperties.setProperty("hibernate.hbm2ddl.auto", "");
    jpaProperties.setProperty("hibernate.show_sql", "true");
    // jpaProperties.setProperty("hibernate.format_sql", "true");
    jpaProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");

    emf.setJpaProperties(jpaProperties);
    emf.setDataSource(dataSource);
    return emf;
  }

  @Bean
  public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(emf);
    return transactionManager;
  }
}
