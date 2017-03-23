package com.mageddo;

import java.util.HashMap;

import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * @author elvis
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 3/23/17 4:02 PM
 */
@Configuration
public class MySQLConfig {

    @Bean
    public DataSource mysqlDataSource() {

        MysqlXADataSource mysqlXADataSource = new MysqlXADataSource();
        mysqlXADataSource.setURL("jdbc:mysql://mysql-server.dev:3306/ps_accounting_adm");
        mysqlXADataSource.setPassword("root");
        mysqlXADataSource.setUser("root");
        mysqlXADataSource.setPinGlobalTxToPhysicalConnection(true);

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(mysqlXADataSource);
        xaDataSource.setUniqueResourceName(mysqlXADataSource.getResourceId());
        return xaDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {

        String dialect = "org.hibernate.dialect.MySQL5Dialect";

        final HashMap<String, String> properties = new HashMap<>();
        properties.put("hibernate.dialect", dialect);
        return builder
                .dataSource(mysqlDataSource())
                .packages("mysql.packe.entity")
                .persistenceUnit("mysql")
                .properties(properties)
                .build();
    }

}
