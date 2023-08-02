package hello.springtx.propagation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Member {
    @GeneratedValue
    @Id
    private Long id;

    private String username;

    //jpa needs NoArgsConstructor
    public Member(){
    }

    public Member(String username) {
        this.username = username;
    }
}
