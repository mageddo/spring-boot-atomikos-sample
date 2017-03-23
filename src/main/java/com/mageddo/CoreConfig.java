package com.mageddo;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * @author femello
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 2/21/17 6:27 PM
 */
@EnableAutoConfiguration
@EnableAspectJAutoProxy
@EnableTransactionManagement
@ComponentScan
@Configuration
public class CoreConfig {

    @Bean
    public TransactionManager atomikosTransactionManager(){
        final UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(false);
        return userTransactionManager;
    }

    @Bean
    public UserTransaction atomikosUserTransaction() throws SystemException {
        final UserTransactionImp userTransactionImp = new UserTransactionImp();
        userTransactionImp.setTransactionTimeout(300); // 5 min
        return userTransactionImp;
    }


    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("atomikosTransactionManager")  TransactionManager transactionManager,
            @Qualifier("atomikosUserTransaction") UserTransaction userTransaction
    ) throws Throwable {
        AtomikosJtaPlatform.transaction = userTransaction;
        AtomikosJtaPlatform.transactionManager = transactionManager;
        return new JtaTransactionManager(userTransaction, transactionManager);
    }
}
