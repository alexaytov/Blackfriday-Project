package user;

import java.time.ZonedDateTime;

public class Client extends BaseUser {

    public Client(String username, String password, String firstName, String lastName, int age, ZonedDateTime dateOfCreation) {
        super(username, password, firstName, lastName, age, dateOfCreation);
    }

    public Client(String username, String password, String firstName, String lastName, int age) {
        super(username, password, firstName, lastName, age);
    }
}
