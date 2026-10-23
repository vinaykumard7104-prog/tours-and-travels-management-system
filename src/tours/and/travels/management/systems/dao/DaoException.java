package tours.and.travels.management.systems.dao;

/** Thrown for problems whose message is safe to show to the user. */
public class DaoException extends RuntimeException {
    public DaoException(String message) { super(message); }
    public DaoException(String message, Throwable cause) { super(message, cause); }
}
