package uz.app.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.app.role.UsersRole;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Boolean enabled= true;
    private String confirmationCode;
    private boolean confirmed = false;
    private Integer balance = 0;
    private UsersRole role = UsersRole.USER;
}
