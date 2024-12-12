package web.sy.base.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import web.sy.base.config.event.BedSystemInitializedEvent;
import web.sy.base.config.event.MybatisAvailableEvent;

import javax.sql.DataSource;

@Slf4j
@Component
public class DataSourceInitializer  implements ApplicationListener<BedSystemInitializedEvent> {

    private final ApplicationContext applicationContext;

    public DataSourceInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(BedSystemInitializedEvent event) {
        log.debug("Initializing MySQL DataSource after system initialization");

        try {
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext
                    .getAutowireCapableBeanFactory();

            // 创建并注册 DataSource
            DataSource mysqlDataSource = createDataSource();
            beanFactory.registerSingleton("mysqlDataSource", mysqlDataSource);
            log.debug("MySQL DataSource and MyBatis components registered successfully");

            applicationContext.publishEvent(new MybatisAvailableEvent(this));

        } catch (Exception e) {
            log.error("Failed to initialize MySQL components", e);
            throw new RuntimeException("Failed to initialize MySQL components", e);
        }
    }


    private DataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(System.getProperty("spring.datasource.url"));
        config.setUsername(System.getProperty("spring.datasource.username"));
        config.setPassword(System.getProperty("spring.datasource.password"));
        config.setDriverClassName(System.getProperty("spring.datasource.driver-class-name"));
        
        return new HikariDataSource(config);
    }

    private SqlSessionFactory createSqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        
        // 设置mapper.xml文件位置
        sessionFactory.setMapperLocations(
            new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/*.xml")
        );
        
        // 设置配置
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        sessionFactory.setConfiguration(configuration);
        
        return sessionFactory.getObject();
    }



}