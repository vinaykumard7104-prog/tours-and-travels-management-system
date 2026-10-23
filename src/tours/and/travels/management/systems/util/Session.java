package tours.and.travels.management.systems.util;

import tours.and.travels.management.systems.model.Models.Account;

/** Holds the currently signed-in account. */
public final class Session {

    private static Account current;

    private Session() { }

    public static Account get() { return current; }
    public static void set(Account a) { current = a; }
    public static void clear() { current = null; }
    public static String username() { return current == null ? "" : current.username(); }
}
