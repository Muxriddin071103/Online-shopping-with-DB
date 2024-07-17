package uz.app.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Boolean enabled;
    private String confirmationCode;
    private boolean confirmed = false;
}
