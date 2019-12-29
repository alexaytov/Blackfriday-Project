package database.interfaces;

import exceptions.NotFoundException;

public interface Database<T> {

    /**
     * Adds user to the database
     *
     * @param data the data to be added
     */
    void write(T data);

    /**
     * Deletes user from database
     *
     * @param name the name of the data being deleted
     * @throws NotFoundException if the user is not found
     */
    void delete(String name) throws NotFoundException;

    /**
     * Checks if the (@code data) is in the database
     *
     * @param data the data to be checked
     * @return if the user is contained
     */
    boolean contains(T data);

    /**
     * Writes the data to the database file
     */
    void saveAllChanges();


}
