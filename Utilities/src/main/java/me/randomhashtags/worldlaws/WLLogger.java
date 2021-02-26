package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public abstract class WLLogger {
    private static final Logger LOGGER = LogManager.getLogger("WLLogger");
    public static void log(Level level, String msg) {
        Configurator.setLevel("WLLogger", Level.DEBUG);
        LOGGER.log(level, msg);
    }
}
