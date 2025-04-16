package personalprojects.restuarantappbackend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;
    @NotBlank
    @Size(min = 8, max = 20)
    private String email;
    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    public @NotBlank @Size(min = 3, max = 20) String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank @Size(min = 3, max = 20) String username) {
        this.username = username;
    }

    public @NotBlank @Size(min = 8, max = 20) String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank @Size(min = 8, max = 20) String email) {
        this.email = email;
    }

    public @NotBlank @Size(min = 8, max = 20) String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank @Size(min = 8, max = 20) String password) {
        this.password = password;
    }
}
