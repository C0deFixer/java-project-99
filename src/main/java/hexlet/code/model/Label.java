package hexlet.code.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lables")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(onlyExplicitlyIncluded = true)
public class Label implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @ToString.Include
    @Column(unique = true)
    @Size(min = 1, max = 1000)
    private String name;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToMany(mappedBy = "labels", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<Task> tasks = new HashSet<>();

}
