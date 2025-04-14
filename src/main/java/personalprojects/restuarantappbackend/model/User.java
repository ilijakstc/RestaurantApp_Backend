package personalprojects.restuarantappbackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Users")
@Data
public class User{

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int id;
    private String email;
    private String password;
    @Column(unique = true)
    private String username;
    private Boolean enabled = false;
}
