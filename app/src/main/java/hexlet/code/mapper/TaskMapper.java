package hexlet.code.mapper;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(uses = {JsonNullableMapper.class, ReferenceMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TaskMapper {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Mapping(source = "labels", target = "taskLabelIds", qualifiedByName = "mapLabelsToIds")
    @Mapping(source = "name", target = "title")
    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "description", target = "content")
    public abstract TaskDTO map(Task model);

    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "mapIdsToLabels")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToStatus")
    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "description", source = "content")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "mapIdsToLabels")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToStatus")
    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "description", source = "content")
    public abstract Task map(TaskDTO dto);

    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "mapIdsToLabels")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToStatus")
    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "description", source = "content")
    public abstract void update(TaskUpdateDTO update, @MappingTarget Task destination);

    @Named("slugToStatus")
    public TaskStatus slugToStatus(String slug) {
        return taskStatusRepository.findBySlug(slug);
    }

    @Named("mapIdsToLabels")
    public Set<Label> mapIdsToLabels(Set<Long> labelIds) {
        return labelRepository.findAllByIdIn(labelIds);
    }

    @Named("mapLabelsToIds")
    public Set<Long> mapLabelsToIds(Set<Label> labels) {
        return labels.stream()
            .map(Label::getId)
            .collect(Collectors.toSet());
    }
}
