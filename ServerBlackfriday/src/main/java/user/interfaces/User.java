package user.interfaces;

import java.time.ZonedDateTime;

public interface User extends Human {

    String getUsername();

    String getPassword();

    ZonedDateTime getDateOfCreation();

    void setUsername(String newUsername);

    void setPassword(String newPassword);


}
