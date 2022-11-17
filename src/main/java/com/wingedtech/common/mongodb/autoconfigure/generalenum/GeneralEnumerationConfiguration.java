package com.wingedtech.common.mongodb.autoconfigure.generalenum;

import com.wingedtech.common.generalenum.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.wingedtech.common.generalenum.GeneralEnumerationRepository.GENERAL_ENUMERATION_REPOSITORY_NAME;

/**
 * Created on 2018/12/27.
 *
 * @author ssy
 */

@Configuration
@ConditionalOnProperty(name = "enabled", prefix = "winged.common.generalenum")
@Import(GeneralEnumerationDatabaseConfiguration.class)
public class GeneralEnumerationConfiguration {

    @Bean
    public GeneralEnumerationMapper generalEnumerationMapper() {
        return new GeneralEnumerationMapperImpl();
    }

    @Bean
    public GeneralEnumerationService generalEnumerationService(GeneralEnumerationMapper mapper, GeneralEnumerationRepository generalEnumerationRepository) {
        return new GeneralEnumerationServiceImpl(generalEnumerationRepository, mapper);
    }

    @Bean
    public GeneralEnumerationResource generalEnumerationResource(GeneralEnumerationService service) {
        return new GeneralEnumerationResource(service);
    }
}
