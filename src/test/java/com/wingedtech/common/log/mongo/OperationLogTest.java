package com.wingedtech.common.log.mongo;


import com.wingedtech.common.LoggingConfiguration;
import com.wingedtech.common.log.annotation.OperationLogger;
import com.wingedtech.common.mongodb.autoconfigure.log.MongoOperationLogAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.registerCustomDateFormat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LoggingConfiguration.class, MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoOperationLogAutoConfiguration.class, JacksonAutoConfiguration.class})
@ActiveProfiles("log")
@Slf4j
public class OperationLogTest {

    private static final String RESOURCE = "store";


    @Test
    public void testCreateAndPut() throws IOException {

        LogController logController = new LogController();
        ResponseEntity<Void> test = logController.getUser("test");
        log.info(test.getStatusCode().toString());

    }

    @RestController
    @RequestMapping("/api/log")
    static class LogController {

        @RequestMapping("/get-user")
        @OperationLogger(description = "获取用户信息")
        public ResponseEntity<Void> getUser(@RequestParam String login) {
            return ResponseEntity.ok().build();
        }
    }
}
