package dev.challenge.factory;

import com.github.javafaker.Faker;
import dev.challenge.api.v1.dto.request.ProjectCreateRequest;
import dev.challenge.api.v1.dto.request.ProjectStatusUpdateRequest;
import dev.challenge.api.v1.dto.request.ProjectUpdateRequest;
import dev.challenge.domain.ProjectStatus;
import dev.challenge.entity.Project;

import java.time.LocalDate;

public class ProjectFactory {
    private static final Faker faker = new Faker();

    public static Project create() {
        return create(faker.programmingLanguage().name());
    }

    public static Project create(String name) {
        return Project.builder().name(name).build();
    }

    public static ProjectCreateRequest createRequest() {
        return createRequest(faker.programmingLanguage().name());
    }

    public static ProjectCreateRequest createRequest(Project project) {
        return new ProjectCreateRequest(project.getName(), project.getExpectedStart(), project.getExpectedEnd(), project.getActualStart(), project.getActualEnd());
    }

    public static ProjectCreateRequest createRequest(String name) {
        return new ProjectCreateRequest(name, LocalDate.now().minusDays(5), LocalDate.now().plusDays(10), null, null);
    }

    public static ProjectUpdateRequest updateRequest() {
        return updateRequest(faker.programmingLanguage().name());
    }

    public static ProjectStatusUpdateRequest updateStatusRequest() {
        return updateStatusRequest(ProjectStatus.ACTIVE);
    }

    public static ProjectStatusUpdateRequest updateStatusRequest(ProjectStatus status) {
        return new ProjectStatusUpdateRequest(status.name());
    }

    public static ProjectUpdateRequest updateRequest(Project project) {
        return new ProjectUpdateRequest(project.getName(), project.getExpectedStart(), project.getExpectedEnd(), project.getActualStart(), project.getActualEnd());
    }

    public static ProjectUpdateRequest updateRequest(String name) {
        return new ProjectUpdateRequest(name, null, null, null, null);
    }

    public static Project.ProjectBuilder<?, ?> builder() {
        return builder(faker.programmingLanguage().name());
    }

    public static Project.ProjectBuilder<?, ?> builder(String name) {
        return Project.builder().name(name);
    }

    public static Project todo() {
        return builder().status(ProjectStatus.TODO).build();
    }

    public static Project active() {
        return builder().expectedEnd(LocalDate.now().plusDays(5))
                        .expectedStart(LocalDate.now().minusDays(5))
                        .actualStart(LocalDate.now())
                        .status(ProjectStatus.ACTIVE)
                        .build();
    }

    public static Project late(String name, int delay) {
        return builder(name).expectedEnd(LocalDate.now().minusDays(delay))
                            .actualStart(LocalDate.now())
                            .status(ProjectStatus.LATE)
                            .build();
    }

    //
    //    public static Project done() {
    //        Project p = new Project();
    //        p.setActualStart(LocalDate.now().minusDays(5));
    //        p.setActualEnd(LocalDate.now().minusDays(3));
    //        p.setStatus(ProjectStatus.DONE);
    //        return p;
    //    }
}
