package user;

import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.Assert.assertEquals;


public class ClientTest {

    private static final String DEFAULT_USERNAME = "defaultUsername";
    private static final String DEFAULT_PASSWORD = "defaultPassword";
    private static final String DEFAULT_FIRST_NAME = "defaultFirstName";
    private static final String DEFAULT_LAST_NAME = "defaultLastName";
    private static final int DEFAULT_AGE = 20;

    private Client client;

    @Before
    public void setup(){
        this.client = new Client(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_AGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenDateIsNull(){
        client = new Client(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_AGE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenUsernameIsNull(){
        this.client = new Client(null, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_AGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenUsernameIsEmpty(){
        this.client = new Client("  ", DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_AGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenPasswordIsNull(){
        this.client = new Client(DEFAULT_USERNAME, null, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_AGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenPasswordIsEmpty(){
        this.client = new Client(DEFAULT_USERNAME, "  ", DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_AGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenFirstNameIsEmpty(){
        this.client = new Client(DEFAULT_USERNAME, DEFAULT_PASSWORD, null, DEFAULT_LAST_NAME, DEFAULT_AGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenFirstNameIsNull(){
        this.client = new Client(DEFAULT_USERNAME, DEFAULT_PASSWORD, " ", DEFAULT_LAST_NAME, DEFAULT_AGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenLastNameIsEmpty(){
        this.client = new Client(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, null, DEFAULT_AGE);

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenLastNameIsNull(){
        this.client = new Client(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, "  ", DEFAULT_AGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenAgeIsNegative(){
        this.client = new Client(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenAgeIsZero(){
        this.client = new Client(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, 0);
    }

    @Test
    public void getterForDateOfCreationShouldReturnRightValue(){
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        assertEquals(zonedDateTime.toString().substring(0, 19), this.client.getDateOfCreation().toString().substring(0, 19));
    }


    @Test
    public void getUsernameShouldReturnCorrectValue(){
        assertEquals(DEFAULT_USERNAME, this.client.getUsername());
    }

    @Test
    public void getFirstNameShouldReturnCorrectValue(){
        assertEquals(DEFAULT_FIRST_NAME, this.client.getFirstName());
    }

    @Test
    public void getLastNameShouldReturnCorrectValue(){
        assertEquals(DEFAULT_LAST_NAME, this.client.getLastName());
    }

    @Test
    public void getAgeShouldReturnCorrectValue(){
        assertEquals(DEFAULT_AGE, this.client.getAge());
    }

    @Test
    public void getPasswordShouldReturnCorrectValue(){
        assertEquals(DEFAULT_PASSWORD, this.client.getPassword());
    }

    @Test
    public void hashShouldBeGeneratedOnlyByName(){
        Client newClient = new Client(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_AGE);
        assertEquals(newClient.hashCode(), this.client.hashCode());
    }

    @Test
    public void equalsShouldCompareOnlyByName(){
        Client newClient = new Client(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_AGE);
        assertEquals(this.client, newClient);
    }




}
