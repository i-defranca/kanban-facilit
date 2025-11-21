package dev.challenge.factory;

import com.github.javafaker.Faker;
import dev.challenge.api.v1.dto.request.MemberCreateRequest;
import dev.challenge.api.v1.dto.request.MemberUpdateRequest;
import dev.challenge.domain.MemberRole;
import dev.challenge.entity.Member;
import dev.challenge.service.DepartmentService;

public class MemberFactory {
    private static final Faker faker = new Faker();

    public static Member create() {
        return create(faker.name().firstName());
    }

    public static Member create(String name) {
        return Member.builder().name(name).role(MemberRole.LEAD).email(faker.internet().emailAddress()).build();
    }

    public static Member create(DepartmentService dept) {
        return create(faker.name().firstName(), dept);
    }

    public static Member create(String name, DepartmentService dept) {
        Member m = create(name);
        m.setDepartment(dept.create(DepartmentFactory.create()));
        return m;
    }

    private static String uid(DepartmentService service) {
        return service.create(DepartmentFactory.create()).getId().toString();
    }

    public static MemberCreateRequest createRequest(DepartmentService dept) {
        return createRequest(faker.job().field(), dept);
    }

    public static MemberCreateRequest createEmailRequest(String email, DepartmentService dept) {
        return new MemberCreateRequest(faker.job().field(), email, MemberRole.LEAD.name(), uid(dept));
    }

    public static MemberCreateRequest createRequest(String name, DepartmentService dept) {
        return new MemberCreateRequest(name, faker.internet().emailAddress(), MemberRole.LEAD.name(), uid(dept));
    }

    public static MemberUpdateRequest updateRequest(DepartmentService dept) {
        return updateRequest(faker.job().field(), dept);
    }

    public static MemberUpdateRequest updateRequest(String name, DepartmentService dept) {
        return new MemberUpdateRequest(name, faker.internet().emailAddress(), MemberRole.LEAD.name(), uid(dept));
    }
}
