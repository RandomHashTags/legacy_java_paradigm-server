package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public abstract class WLLogger {
    private static final Logger LOGGER = LogManager.getLogger("WLLogger");

    static {
        Configurator.setLevel("WLLogger", Level.DEBUG);
    }

    public static void logError(Object inputClass, String msg) {
        log(Level.ERROR, msg);
        final String name = inputClass instanceof String ? (String) inputClass : inputClass.getClass().getSimpleName();
        WLUtilities.saveLoggedError(name, msg);
    }
    public static void logWarning(String msg) {
        log(Level.WARN, msg);
    }
    public static void logInfo(String msg) {
        log(Level.INFO, msg);
    }
    public static void log(Level level, String msg) {
        LOGGER.log(level, msg);
    }
}
