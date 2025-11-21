package dev.challenge.factory;

import com.github.javafaker.Faker;
import dev.challenge.api.v1.dto.request.DepartmentRequest;
import dev.challenge.entity.Department;

public class DepartmentFactory {
    private static final Faker faker = new Faker();

    public static Department create() {
        return create(faker.job().field());
    }

    public static Department create(String name) {
        return Department.builder().name(name).build();
    }

    public static DepartmentRequest request() {
        return request(faker.job().field());
    }

    public static DepartmentRequest request(String name) {
        return new DepartmentRequest(name);
    }
}
