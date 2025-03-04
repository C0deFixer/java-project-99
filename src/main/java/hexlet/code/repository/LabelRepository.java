package hexlet.code.repository;

import hexlet.code.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
    public Optional<Label> findByName(String name);

    //public List<Label> findAllById(Iterable<Label>){};Set
    public Set<Label> findAllByIdIn(List<Long> ids);
/*    @Query("SELECT FROM labels WHERE id in :")
    default public List<Label> fingAllById(List<Long> list) {
        return new ArrayList<Label>() {
        };
    };*/
}
