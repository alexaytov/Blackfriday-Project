package database;

import database.IO.JSONReader;
import database.IO.JSONWriter;
import exceptions.NotFoundException;
import user.interfaces.User;

import java.io.IOException;

public class UserDatabase<T extends User> extends BaseUserDatabase<T> {
    private final String filename;

    public UserDatabase(String fileName, String userType) throws IOException {
        super(JSONReader.readUsers(fileName, userType));
        this.filename = fileName;
    }

    public T getByName(String username) throws NotFoundException {
        T user = super.getData().get(username);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    @Override
    public synchronized void write(T user) {
        super.getData().put(user.getUsername(), user);
        this.saveAllChanges();

    }

    @Override
    public synchronized void delete(String username) throws NotFoundException {
        if (super.getData().remove(username) == null) {
            throw new NotFoundException();
        }
        this.saveAllChanges();
    }

    @Override
    public boolean contains(User user) {
        return super.getData().containsKey(user.getUsername());
    }

    @Override
    public void saveAllChanges() {
        JSONWriter.writeUsers(super.getData(), filename);
    }
}
