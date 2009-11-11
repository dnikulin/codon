package org.dnikulin.jcombinator.log;

/** A LineLogger that ignores its given lines. */
public class NullLogger implements LineLogger {
    /** Singleton instance. */
    public static final NullLogger INSTANCE = new NullLogger();

    @Override
    public void print(String line) {
        // Ignore
    }
}
