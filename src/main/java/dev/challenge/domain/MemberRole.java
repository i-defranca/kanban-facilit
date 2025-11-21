package dev.challenge.domain;

import lombok.Getter;

public enum MemberRole {
    LEAD("Responsável"),
    MANAGER("Gerente");

    @Getter
    private final String label;

    MemberRole(String label) {
        this.label = label;
    }
}
