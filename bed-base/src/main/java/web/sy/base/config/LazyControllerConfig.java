package web.sy.base.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Configuration

public class LazyControllerConfig {
    @Bean
    public static BeanFactoryPostProcessor lazyControllerProcessor() {
        return new BeanFactoryPostProcessorWithPriority();
    }

    private static class BeanFactoryPostProcessorWithPriority
            implements BeanFactoryPostProcessor, PriorityOrdered {

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            log.debug("Post-processing bean factory");
            String[] beanNames = beanFactory.getBeanDefinitionNames();
            log.debug("Total bean definitions: {}", beanNames.length);
            for (String beanName : beanNames) {
                try {
                    String className = beanFactory.getBeanDefinition(beanName).getBeanClassName();
                    if (className != null) {
                        Class<?> beanClass = Class.forName(className);
                        if (beanClass.isAnnotationPresent(Controller.class) ||
                                beanClass.isAnnotationPresent(RestController.class) ||
                                beanClass.isAnnotationPresent(Service.class)) {

                            if (!isExcludedBean(beanClass)) {
                                log.debug("Setting lazy init for bean: {}", beanName);
                                beanFactory.getBeanDefinition(beanName).setLazyInit(true);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("Error processing bean: {}", beanName, e);
                }
            }

        }

        private boolean isExcludedBean(Class<?> beanClass) {
            // 使用类全限定名进行匹配
            String className = beanClass.getName();
            return  
                    className.equals("web.sy.base.security.service.impl.UserDetailsServiceImpl");
        }

        @Override
        public int getOrder() {
            return PriorityOrdered.HIGHEST_PRECEDENCE; // 设置最高优先级
        }
    }


}
