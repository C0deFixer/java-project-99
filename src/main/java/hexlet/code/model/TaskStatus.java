package hexlet.code.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "task_statuses")
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString(onlyExplicitlyIncluded = true)
public class TaskStatus implements BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @Column(unique = true)
    @Size(min = 1)
    private String name;

    @ToString.Include
    @Column(unique = true)
    @Size(min = 1)
    private String slug;

    @CreatedDate
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "taskStatus", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

}
