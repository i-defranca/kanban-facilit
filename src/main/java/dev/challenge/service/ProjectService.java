package dev.challenge.service;

import com.fasterxml.jackson.databind.JsonNode;
import dev.challenge.api.v1.dto.request.ProjectStatusUpdateRequest;
import dev.challenge.api.v1.dto.request.ProjectUpdateRequest;
import dev.challenge.domain.ProjectStatus;
import dev.challenge.entity.Project;
import dev.challenge.exception.NotFoundException;
import dev.challenge.exception.RequestValidationException;
import dev.challenge.repository.ProjectRepository;
import dev.challenge.sluggable.SlugService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static dev.challenge.util.Util.*;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository repository;
    private final SlugService<Project> slugify;

    public Project create(Project p) {
        p.setSlug(slugify.generate(p));

        p.setStatus(this.processStatus(p));
        p.setDelayDays(this.processDelay(p));
        p.setRemainingTimePercentage(this.processRemainingTime(p));

        return repository.save(p);
    }

    public Project find(String slug) {
        return repository.findBySlug(slug).orElseThrow(() -> new NotFoundException(Project.class, slug));
    }

    public List<Project> list(String status) {
        if (filled(status)) {
            return repository.findAllByStatus(ProjectStatus.valueOf(status.toUpperCase()));
        }
        return list();
    }

    public List<Project> list() {
        return repository.findAll();
    }

    public long count() {
        return repository.count();
    }

    @Transactional
    public ProjectStatus updateStatus(String slug, ProjectStatusUpdateRequest data) {
        Project p = find(slug);
        ProjectStatus update;
        try {
            update = ProjectStatus.valueOf(data.status().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RequestValidationException(Map.of("status", "InvalidStatus"), data.status() + " is not a valid status.");
        }

        if (p.getStatus().equals(update)) {
            return update;
        }

        switch (update) {
            case TODO -> {
                if (p.getStatus().equals(ProjectStatus.ACTIVE)) {
                    p.setActualStart(null);
                    break;
                }
                invalidStatus(update, String.format("Clear the %s date or update one of the expected dates for automatic status update.", p.getStatus()
                                                                                                                                           .equals(ProjectStatus.LATE) ? "start" : "end"));
            }
            case ACTIVE -> {
                switch (p.getStatus()) {
                    case TODO -> p.setActualStart(LocalDate.now());
                    case LATE -> invalidStatus(update, "Update one of the expected dates for automatic status update.");
                    case DONE -> {
                        p.setActualEnd(null);
                        p.setStatus(processStatus(p));
                        if (p.getStatus().equals(ProjectStatus.LATE)) {
                            invalidStatus(update, "Update the dates for automatic status update.");
                        }
                    }
                }
            }
            case DONE -> p.setActualEnd(LocalDate.now());
            case LATE -> {
                switch (p.getStatus()) {
                    case TODO -> {
                        if (! past(p.getExpectedStart())) {
                            invalidStatus(update, "Operation is invalid before the expected start date.");
                        }
                    }
                    case ACTIVE ->
                            invalidStatus(update, "Set it back to 'todo' or update the expected end date for automatic status update.");
                    case DONE -> {
                        p.setActualEnd(null);
                        p.setStatus(processStatus(p));
                        if (!p.getStatus().equals(ProjectStatus.LATE)) {
                            invalidStatus(update, "Update the dates for automatic status update.");
                        }
                    }
                }
            }
        }

        p.setStatus(update);
        repository.save(p);
        return p.getStatus();
    }

    private void invalidStatus(ProjectStatus status, String message) {
        throw new RequestValidationException(Map.of("status", "InvalidStatus"), String.format("Project cannot be set to %s. %s", status.name(), message));
    }

    @Transactional
    public Project update(String slug, JsonNode patch, ProjectUpdateRequest dto) {
        Project p = find(slug);
        if (patch.has("name")) {
            if (patch.get("name").asText().isBlank()) {
                throw new RequestValidationException(Map.of("name", "NotBlank"));
            }
            p.setName(dto.name());
            p.setSlug(slugify.generate(p));
        }
        if (patch.has("expectedStart")) {
            p.setExpectedStart(dto.expectedStart());
        }
        if (patch.has("expectedEnd")) {
            p.setExpectedEnd(dto.expectedEnd());
        }
        if (patch.has("actualStart")) {
            p.setActualStart(dto.actualStart());
        }
        if (patch.has("actualEnd")) {
            p.setActualEnd(dto.actualEnd());
        }
        ProjectStatus status = this.processStatus(p);
        if (status != p.getStatus()) {
            p.setStatus(status);
        }
        p.setDelayDays(this.processDelay(p));
        p.setRemainingTimePercentage(this.processRemainingTime(p));

        return repository.save(p);
    }

    private ProjectStatus processStatus(Project p) {
        ProjectStatus status = ProjectStatus.forProject(p);
        if (status == null) {
            throw new RequestValidationException(Map.of("status", "InvalidDates"));
        }
        return status;
    }

    private Integer processDelay(Project p) {
        if (empty(p.getActualEnd()) && past(p.getExpectedEnd()) && filled(p.getActualStart())) {
            return (int) p.getExpectedEnd().until(LocalDate.now(), ChronoUnit.DAYS);
        }
        return 0;
    }

    private BigDecimal processRemainingTime(Project p) {
        boolean todo = empty(p.getActualStart()) && empty(p.getActualEnd());
        boolean done = filled(p.getActualEnd());
        boolean late = past(p.getExpectedEnd()) && !done;

        if (todo || done || late) {
            return BigDecimal.ZERO;
        }

        if (filled(p.getExpectedStart()) && filled(p.getExpectedEnd())) {
            long total = p.getExpectedStart().until(p.getExpectedEnd(), ChronoUnit.DAYS);
            if (total <= 0) {
                return BigDecimal.ZERO;
            }

            long used = p.getExpectedStart().until(LocalDate.now(), ChronoUnit.DAYS);
            long remaining = total - used;

            return BigDecimal.valueOf(remaining * 100 / total).setScale(1, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    @Transactional
    public void delete(String slug) {
        repository.deleteBySlug(slug);
    }
}
