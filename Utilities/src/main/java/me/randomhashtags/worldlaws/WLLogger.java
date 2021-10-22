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

    public static void log(Level level, String msg) {
        if(level == Level.ERROR) {
            WLUtilities.saveLoggedError(msg);
        } else {
            LOGGER.log(level, msg);
        }
    }
}
