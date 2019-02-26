package com.rumaruka.powercraft.api;



import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.logging.*;

public class PCLogger {

    private static final Logger logger = Logger.getLogger("PowerCraft");

    private static boolean loggingEnabled = true;

    private static boolean inited;

    private static boolean printToStdout = false;


    static void init(File f){
        if(inited){
            return;
        }
        inited=true;
        try {
            FileHandler handler=new FileHandler(f.getPath());
            //handler.setFormatter(new);
            logger.addHandler(handler);
            loggingEnabled=true;
        }catch (Exception e){
            e.printStackTrace();
        }
        logger.setLevel(Level.ALL);
        logger.info("PowerCraft logger info initialized");
        logger.info((new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
    }

    public static void setLoggingEnabled(boolean loggingEnabled) {
        PCLogger.loggingEnabled = loggingEnabled;
    }

    public static void setPrintToStdout(boolean printToStdout) {
        PCLogger.printToStdout = printToStdout;
    }
    public static void log(Level lvl,String msg,Object... param){
        log(Level.INFO,msg,param);
    }

    public static void info(String msg, Object... param) {

        log(Level.INFO, msg, param);
    }
    public static void fine(String msg, Object... param) {

        log(Level.FINE, msg, param);
    }
    public static void finest(String msg, Object... param) {

        log(Level.FINEST, msg, param);
    }
    public static void warning(String msg, Object... param) {

        log(Level.WARNING, msg, param);
    }
    public static void throwing(String sourceClass, String sourceMethod, Throwable thrown) {

        if (!loggingEnabled) {
            return;
        }

        logger.throwing(sourceClass, sourceMethod, thrown);
    }

    public static void severe(String msg, Object... param) {

        log(Level.SEVERE, msg, param);
    }
    public static void finer(String msg,Object... param){
        log(Level.FINER,msg,param);
    }


    private PCLogger(){

    }


    private static class LogFormatter extends Formatter{
        private static final String nl = System.getProperty("line.separator");
        public LogFormatter() {}

        @Override
        public String format(LogRecord record) {
          StringBuffer buffer = new StringBuffer(180);
          if(record.getMessage().equals("\n")){
              return nl;
          }
            if (record.getMessage().charAt(0) == '\n') {
                buffer.append(nl);
                record.setMessage(record.getMessage().substring(1));
            }
            Level lvl = record.getLevel();
            String trail = "";
            if(lvl==Level.CONFIG){
                trail="CONFIG:";
            }
            if (lvl == Level.FINE) {
                trail = "";
            }

            if (lvl == Level.FINER) {
                trail = "\t";
            }

            if (lvl == Level.FINEST) {
                trail = "\t\t";
            }

            if (lvl == Level.INFO) {
                trail = "INFO: ";
            }

            if (lvl == Level.SEVERE) {
                trail = "SEVERE: ";
            }

            if (lvl == Level.WARNING) {
                trail = "WARNING: ";
            }
            buffer.append(trail);
            buffer.append(formatMessage(record));
            buffer.append(nl);
            Throwable throwable = record.getThrown();
            if(throwable!=null){
                buffer.append("at ");
                buffer.append(record.getSourceClassName());
                buffer.append(".");
                buffer.append(record.getSourceMethodName());
                buffer.append(nl);
            }
            if(PCLogger.printToStdout){
                System.out.print(buffer.toString());
            }
            return buffer.toString();
        }
    }



}
