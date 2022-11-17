package com.wingedtech.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wingedtech.common.util.MongoMigrationUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class DbMigrationUtilsTest {

    static class Car {
        private String name;
        private String brand;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }
    }

    @Test
    public void testJsonImport() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Car> cars = MongoMigrationUtils.loadObjectsFromJson(objectMapper, new TypeReference<List<Car>>() {}, "cars.json");
        assertThat(cars).isNotEmpty();
        assertThat(cars.get(0).getName()).isEqualTo("Focus");
    }
}
