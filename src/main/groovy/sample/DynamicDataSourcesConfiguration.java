package sample;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.validation.BindException;

@Configuration
public class DynamicDataSourcesConfiguration implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private ConfigurableEnvironment environment;
    private static Map<String, Object> defaultDsProperties = new HashMap<String, Object>() {
        {
            put("suppressClose", true);
            put("username", "sa");
            put("password", "");
            put("driverClassName", "org.h2.Driver");
        }
    };

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment)environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        DataSourceSettings settings = resolveSettings();
        for (Entry<String, String> entry : settings.clientDataSources().entrySet()) {
            createDsBean(registry, entry.getKey(), entry.getValue());
        }
    }

    private void createDsBean(BeanDefinitionRegistry registry, String beanName, String jdbcUrl) {
        GenericBeanDefinition beanDefinition = createBeanDefinition(SingleConnectionDataSource.class);
        beanDefinition.getPropertyValues().addPropertyValues(defaultDsProperties).addPropertyValue("url", jdbcUrl);
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    private GenericBeanDefinition createBeanDefinition(Class<?> beanClass) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_NO);
        return beanDefinition;
    }

    private DataSourceSettings resolveSettings() {
        DataSourceSettings settings = new DataSourceSettings();
        PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<Object>(settings);
        factory.setTargetName("ds");
        factory.setPropertySources(environment.getPropertySources());
        factory.setConversionService(environment.getConversionService());
        try {
            factory.bindPropertiesToTarget();
        }
        catch (BindException ex) {
            throw new FatalBeanException("Could not bind DataSourceSettings properties", ex);
        }
        return settings;
    }

}
