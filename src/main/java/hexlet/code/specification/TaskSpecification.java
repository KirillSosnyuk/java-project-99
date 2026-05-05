package hexlet.code.specification;

import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {
    public Specification<Task> build(TaskParamsDTO params) {
        return withTitleCont(params.getTitleCont())
            .and(withAssigneeId(params.getAssigneeId()))
            .and(withStatus(params.getStatus()))
            .and(withLabelId(params.getLabelId()));
    }

    private Specification<Task> withAssigneeId(Long id) {
        return (root, query, cb) -> id == null ? cb.conjunction() : cb.equal(root.get("assignee").get("id"), id);
    }

    private Specification<Task> withTitleCont(String titleCont) {
        return (root, query, cb) -> titleCont == null
            ? cb.conjunction()
            : cb.like(cb.lower(root.get("name")), "%" + titleCont.toLowerCase() + "%");
    }

    private Specification<Task> withStatus(String status) {
        return (root, query, cb) -> status == null ? cb.conjunction()
            : cb.equal(root.get("taskStatus").get("slug"), status);
    }

    private Specification<Task> withLabelId(Long id) {
        return (root, query, cb) -> id == null ? cb.conjunction() : cb.equal(root.get("labels").get("id"), id);
    }
}
