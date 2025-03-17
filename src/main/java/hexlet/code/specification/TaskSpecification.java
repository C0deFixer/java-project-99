package hexlet.code.specification;

import hexlet.code.dto.TaskParamsDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {

    public Specification<Task> build(TaskParamsDto params) {
        return withTitleCont(params.getTitleCont())
                .and(withAssigneeId(params.getAssigneeId()))
                .and(withStatus(params.getStatus()))
                .and(withLabelid(params.getLabelId()));

    }

    private Specification<Task> withTitleCont(String titleCont) {
        return (root, query, cb) -> titleCont == null ? cb.conjunction() : cb.like(root.get("name"), "%" + titleCont + "%");
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) -> assigneeId == null ? cb.conjunction() : cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    private Specification<Task> withStatus(String statusSlug) {
        return (root, query, cb) -> statusSlug == null ? cb.conjunction() : cb.equal(root.get("taskStatus").get("name"), statusSlug);
    }

    private Specification<Task> withLabelid(Long labelid) {
        if (labelid == null) {
            return (root, query, cb) -> cb.conjunction();
        } else {
            return (root, query, cb) -> {
                Join<Label, Task> labelTaskJoin = root.join("labels");
                return cb.equal(labelTaskJoin.get("id"), labelid);
            };
        }
    }

}
