/*
 * Copyright 2024 Robert A. James
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rajames.forth.config;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;


@Configuration
@EnableTransactionManagement
@PropertySource("classpath:forth.properties")
@ComponentScan("com.rajames.forth")
@EnableJpaRepositories(basePackages = "com.rajames.forth")
public class ForthConfig {

    @Value("${db.driver}")
    private String DB_DRIVER;

    @Value("${db.password}")
    private String DB_PASSWORD;

    @Value("${db.url}")
    private String DB_URL;

    @Value("${db.username}")
    private String DB_USERNAME;

    @Value("${hibernate.dialect}")
    private String HIBERNATE_DIALECT;

    @Value("${hibernate.show_sql}")
    private String HIBERNATE_SHOW_SQL;

    @Value("${hibernate.hbm2ddl.auto}")
    private String HIBERNATE_HBM2DDL_AUTO;

    @Value("${entitymanager.packagesToScan}")
    private String ENTITYMANAGER_PACKAGES_TO_SCAN;

    @Value("hibernate.connection.autocommit")
    private String HIBERNATE_CONNECTION_AUTOCOMMIT;

    @Value("hibernate.cache.use_second_level_cache")
    private String HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE;

    @Value("hibernate.cache.use_query_cache")
    private String HIBERNATE_CACHE_USE_QUERY_CACHE;

    @Value("hibernate.enable_lazy_load_no_trans")
    private String HIBERNATE_ENABLE_LAZY_LOAD_NO_TRANS;

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(this.DB_DRIVER);
        dataSource.setUrl(this.DB_URL);
        dataSource.setUsername(this.DB_USERNAME);
        dataSource.setPassword(this.DB_PASSWORD);
        return dataSource;
    }

    @Autowired
    @Bean(name = "transactionManager")
    JpaTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
        final JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }

    @Autowired
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource) {
        final LocalContainerEntityManagerFactoryBean emFactory = new LocalContainerEntityManagerFactoryBean();
        emFactory.setDataSource(dataSource);
        emFactory.setPackagesToScan(this.ENTITYMANAGER_PACKAGES_TO_SCAN);
        emFactory.setPersistenceProviderClass(HibernatePersistenceProvider.class);

        final Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", this.HIBERNATE_DIALECT);
        jpaProperties.put("hibernate.show_sql", this.HIBERNATE_SHOW_SQL);
        jpaProperties.put("hibernate.hbm2ddl.auto", this.HIBERNATE_HBM2DDL_AUTO);
        jpaProperties.put("hibernate.connection.autocommit", this.HIBERNATE_CONNECTION_AUTOCOMMIT);
        jpaProperties.put("hibernate.cache.use_second_level_cache", this.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE);
        jpaProperties.put("hibernate.cache.use_query_cache", this.HIBERNATE_CACHE_USE_QUERY_CACHE);
        jpaProperties.put("hibernate.enable_lazy_load_no_trans", this.HIBERNATE_ENABLE_LAZY_LOAD_NO_TRANS);

        emFactory.setJpaProperties(jpaProperties);

        return emFactory;
    }
}
