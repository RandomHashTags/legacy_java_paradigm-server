package me.randomhashtags.worldlaws;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public abstract class WLLogger {
    private static Logger LOGGER;
    public static void log(Level level, String msg) {
        if(LOGGER == null) {
            LogManager.getLogManager().reset();
            LOGGER = Logger.getLogger(WLLogger.class.getName());

            final Handler handler = new Handler() {
                @Override
                public void publish(LogRecord logRecord) {
                    final Date date = new Date(logRecord.getMillis());
                    final SimpleDateFormat format = new SimpleDateFormat("yyyy.MMM.dd@HH:mm:ss");
                    final String now = format.format(date), msg = logRecord.getMessage();
                    final String string = "[" + now + "] [" + logRecord.getLevel().getName() + "] " + msg;
                    System.out.println(string);
                }

                @Override
                public void flush() {
                }

                @Override
                public void close() throws SecurityException {
                }
            };
            LOGGER.addHandler(handler);
        }
        LOGGER.log(level, msg);
    }
}
