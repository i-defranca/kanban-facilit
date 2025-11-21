package dev.challenge.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@SuperBuilder
public class Department extends Base {
    @OneToMany(mappedBy = "department")
    private List<Member> members = new ArrayList<>();

    public Department() {
        super();
    }
}
