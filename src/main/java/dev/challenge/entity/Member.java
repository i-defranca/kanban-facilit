package dev.challenge.entity;

import dev.challenge.domain.MemberRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@SuperBuilder
public class Member extends Base {
    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @ManyToOne
    private Department department;

    @ManyToMany(mappedBy = "members")
    private Set<Project> projects = new HashSet<>();

    public Member() {
        super();
    }
}
