package dev.challenge.domain;

import dev.challenge.entity.Project;
import lombok.Getter;

import java.util.Map;
import java.util.function.Predicate;

import static dev.challenge.util.Util.*;

public enum ProjectStatus {
    TODO("A iniciar"),
    ACTIVE("Em andamento"),
    LATE("Atrasado"),
    DONE("Concluído");

    private static final Map<ProjectStatus, Predicate<Project>> RULES = Map.of(
        TODO, p -> empty(p.getActualStart()) && empty(p.getActualEnd()),
        ACTIVE, p -> filled(p.getActualStart()) && empty(p.getActualEnd()) && future(p.getExpectedEnd()),
        LATE, p -> (past(p.getExpectedStart()) && empty(p.getActualStart())) || (past(p.getExpectedEnd()) && empty(p.getActualEnd())),
        DONE, p -> filled(p.getActualEnd())
    );

    @Getter
    private final String label;

    ProjectStatus(String label) {
        this.label = label;
    }

    public static ProjectStatus forProject(Project project) {
        return RULES.entrySet()
                    .stream()
                    .filter(e -> e.getValue().test(project))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);
    }
}
