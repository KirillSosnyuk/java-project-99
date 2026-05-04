package hexlet.code.repository;

import hexlet.code.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
    Set<Label> findAllByIdIn(Collection<Long> ids);
    Label findByName(String name);
}
