package com.github.fnpac.jpa.config;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Created by 刘春龙 on 2018/3/9.
 */
@Configuration
/**
 * Enable spring data JPA
 */
@EnableJpaRepositories(
        basePackages = "com.github.fnpac.jpa.dao",
        entityManagerFactoryRef = "jpaLocalContainerEntityManagerFactory",
        transactionManagerRef = "jpaTransactionManager"
)
public class SpringJpaConfig implements EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * JPA EntityManagerFactory for persistence unit 'default'
     *
     * @return
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean jpaLocalContainerEntityManagerFactory(DataSource dataSource) {

        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.valueOf(environment.getProperty("jpa.database")));
        jpaVendorAdapter.setGenerateDdl(Boolean.parseBoolean(environment.getProperty("jpa.generateDdl")));
        jpaVendorAdapter.setShowSql(Boolean.parseBoolean(environment.getProperty("jpa.showSql")));

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.github.fnpac.jpa");
        emf.setJpaVendorAdapter(jpaVendorAdapter);
        return emf;
    }

    @Bean
    public PlatformTransactionManager jpaTransactionManager(DataSource dataSource) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();

        // 配置基于容器管理类型的JPA事务管理器
        transactionManager.setEntityManagerFactory(jpaLocalContainerEntityManagerFactory(dataSource).getObject());

        // 设置用于此事务管理器的JPA方言。方言对象可用于检索底层的JDBC连接，从而允许将JPA事务公开为JDBC事务。
        transactionManager.setJpaDialect(hibernateJpaDialect());
        return transactionManager;
    }

    /**
     * JPA方言
     * <p>
     * 以同时支持JPA/JDBC访问，除了HibernateJpaDialect，其他的还有EclipseLinkJpaDialect、OpenJpaDialect
     * <p>
     * 默认的DefaultJpaDialect不支持，因为其getJdbcConnection()方法返回null
     *
     * @return
     */
    private JpaDialect hibernateJpaDialect() {
        return new HibernateJpaDialect();
    }
}