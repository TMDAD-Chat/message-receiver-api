package es.unizar.tmdad.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity(name = "users")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToMany(mappedBy = "users")
    @ToString.Exclude
    private Set<RoomEntity> rooms = new HashSet<>();
}
