package com.mageddo;

import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.DataSource;

import oracle.jdbc.xa.client.OracleXADataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
public class OracleConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix="datasource.otis_ds")
    public DataSource otisDataSource() throws SQLException {
        final OracleXADataSource oracleXADataSource = new OracleXADataSource();
        oracleXADataSource.setUser("user");
        oracleXADataSource.setPassword("pass");
        oracleXADataSource.setURL("jdbc:...");

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(oracleXADataSource);
        xaDataSource.setUniqueResourceName("xa1");
        xaDataSource.setMaxPoolSize(8);
        return xaDataSource;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean otisEntityManagerFactory(

            EntityManagerFactoryBuilder builder) throws SQLException {

        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(true);
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        hibernateJpaVendorAdapter.setDatabase(Database.ORACLE);

        final LocalContainerEntityManagerFactoryBean build = new LocalContainerEntityManagerFactoryBean();
        build.setPackagesToScan("oracle.pack.entity");
        build.setPersistenceUnitName("oracle");
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
        properties.put("javax.persistence.transactionType", "JTA");
        build.setJtaDataSource(otisDataSource());
        build.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        build.setJpaPropertyMap(properties);
        return build;
    }

}
