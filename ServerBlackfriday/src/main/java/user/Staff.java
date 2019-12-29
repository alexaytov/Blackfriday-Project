package user;

import java.time.ZonedDateTime;

public class Staff extends BaseUser {

    public Staff(String username, String password, String firstName, String lastName, int age, ZonedDateTime dateOfCreation) {
        super(username, password, firstName, lastName, age, dateOfCreation);
    }

    public Staff(String username, String password, String firstName, String lastName, int age) {
        super(username, password, firstName, lastName, age);
    }
}
