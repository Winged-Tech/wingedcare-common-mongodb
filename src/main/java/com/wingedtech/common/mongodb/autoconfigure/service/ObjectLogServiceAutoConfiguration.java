package com.wingedtech.common.mongodb.autoconfigure.service;

import com.wingedtech.common.service.logging.ObjectLogServiceConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ObjectLogServiceConfiguration.class)
public class ObjectLogServiceAutoConfiguration {
}
