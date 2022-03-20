package es.unizar.tmdad.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "rooms")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RoomEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "room_users",
            joinColumns = { @JoinColumn(name = "room_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    @ToString.Exclude
    Set<UserEntity> users = new HashSet<>();
}