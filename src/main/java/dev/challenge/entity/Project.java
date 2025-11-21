package dev.challenge.entity;

import dev.challenge.domain.ProjectStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@SuperBuilder
public class Project extends Base {
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    private LocalDate expectedStart;
    private LocalDate expectedEnd;

    private LocalDate actualStart;
    private LocalDate actualEnd;

    private Integer delayDays;

    private BigDecimal remainingTimePercentage;

    @ManyToMany
    private Set<Member> members = new HashSet<>();

    public Project() {
        super();
    }
}
