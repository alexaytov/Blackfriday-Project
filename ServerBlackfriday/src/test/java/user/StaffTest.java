package user;

import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.Assert.assertEquals;


public class StaffTest {

    private static final String DEFAULT_USERNAME = "defaultUsername";
    private static final String DEFAULT_PASSWORD = "defaultPassword";
    private static final String DEFAULT_FIRST_NAME = "defaultFirstName";
    private static final String DEFAULT_LAST_NAME = "defaultLastName";
    private static final int DEFAULT_AGE = 20;

    private Staff staff;

    @Before
    public void setup(){
        this.staff = new Staff(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_AGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenDateIsNull(){
        staff = new Staff(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_AGE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenUsernameIsNull(){
        this.staff = new Staff(null, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_AGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenUsernameIsEmpty(){
        this.staff = new Staff("  ", DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_AGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenPasswordIsNull(){
        this.staff = new Staff(DEFAULT_USERNAME, null, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_AGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenPasswordIsEmpty(){
        this.staff = new Staff(DEFAULT_USERNAME, "  ", DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_AGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenFirstNameIsEmpty(){
        this.staff = new Staff(DEFAULT_USERNAME, DEFAULT_PASSWORD, null, DEFAULT_LAST_NAME, DEFAULT_AGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenFirstNameIsNull(){
        this.staff = new Staff(DEFAULT_USERNAME, DEFAULT_PASSWORD, " ", DEFAULT_LAST_NAME, DEFAULT_AGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenLastNameIsEmpty(){
        this.staff = new Staff(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, null, DEFAULT_AGE);

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenLastNameIsNull(){
        this.staff = new Staff(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, "  ", DEFAULT_AGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenAgeIsNegative(){
        this.staff = new Staff(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenAgeIsZero(){
        this.staff = new Staff(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, 0);
    }

    @Test
    public void getterForDateOfCreationShouldReturnRightValue(){
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        assertEquals(zonedDateTime.toString().substring(0, 19), this.staff.getDateOfCreation().toString().substring(0, 19));
    }


    @Test
    public void getUsernameShouldReturnCorrectValue(){
        assertEquals(DEFAULT_USERNAME, this.staff.getUsername());
    }

    @Test
    public void getFirstNameShouldReturnCorrectValue(){
        assertEquals(DEFAULT_FIRST_NAME, this.staff.getFirstName());
    }

    @Test
    public void getLastNameShouldReturnCorrectValue(){
        assertEquals(DEFAULT_LAST_NAME, this.staff.getLastName());
    }

    @Test
    public void getAgeShouldReturnCorrectValue(){
        assertEquals(DEFAULT_AGE, this.staff.getAge());
    }

    @Test
    public void getPasswordShouldReturnCorrectValue(){
        assertEquals(DEFAULT_PASSWORD, this.staff.getPassword());
    }

    @Test
    public void hashShouldBeGeneratedOnlyByName(){
        Staff newStaff = new Staff(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_AGE);
        assertEquals(newStaff.hashCode(), this.staff.hashCode());
    }

    @Test
    public void equalsShouldCompareOnlyByName(){
        Staff newStaff = new Staff(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_AGE);
        assertEquals(this.staff, newStaff);
    }




}
