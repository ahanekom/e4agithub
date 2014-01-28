package com.excel4apps.servlet.wand.oracle.inst;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.excel4apps.servlet.wand.oracle.inst.apps.LoadAppsConfig;
import com.excel4apps.servlet.wand.oracle.inst.context.InstContext;
import com.excel4apps.servlet.wand.oracle.inst.context.SetupInstContext;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.ArgumentsException;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.FileDeployException;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.InstContextException;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.LoadAppsConfigException;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.LoadOAFPagesException;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.ServletConfigException;
import com.excel4apps.servlet.wand.oracle.inst.files.DeployFiles;
import com.excel4apps.servlet.wand.oracle.inst.oaf.LoadOAFPages;
import com.excel4apps.servlet.wand.oracle.inst.servlet.ServletConfig;

/**
 * Excel4apps Wands Installation Tool
 * 
 * @author Andries Hanekom
 * 
 */

public class Installer
{
    public static final Logger logger = Logger.getLogger(Installer.class.getName());
    public static String logFileName;
    private static Handler logFileHandler;
    private InstContext ic;
    protected static Properties arguments;

    /**
     * Displays help information for Installer tool
     * 
     * Options: -d, --debug - Set the java logging framework LEVEL to ALL. <br>
     * Mode: <br>
     * MODE_DB_AND_APPS_TIER - Performs complete installation of database and
     * application tier artifacts. <br>
     * APPS_TIER - Only installs the application tier components.
     */
    private static void argsHelp()
    {
        System.out.println("");
        System.out.println("Usage: Installer [-OPTIONS] MODE");
        System.out.println("");
        System.out.println("Example:");
        System.out.println("install.sh               # Installs GL Wand Database and Application Tier components");
        System.out.println("install.sh -d            # Installs GL Wand in debug mode");
        System.out
                .println("install.sh -d APPS_TIER  # Installs only the APPS tier components for GL Wand in debug mode ");
        System.out.println("");
        System.out.println("Options:");
        System.out.println("  -d, --debug   Debug mode on");
        System.out.println("");
        System.out.println("Mode: (Optional)");
        System.out.println("  APPS_TIER   Installs only the APPS tier components for GL Wand");
        System.out.println("");
    }

    /**
     * Removes .lck file, JAVA 1.4 Bug related to logger and FileHandles Does
     * not occur on > 1.4
     */
    private static void cleanLogLckFile()
    {
        File lckFile = new File(logFileName + ".lck");

        if (lckFile.exists())
        {
            lckFile.delete();
        }
    }

    /**
     * Prints message to System.out and logs at LEVEL.INFO
     * 
     * @param msg
     */
    private static void logC(String msg)
    {
        if (msg != null)
        {
            System.out.println(msg);

            if ((!msg.equalsIgnoreCase("") && (!msg.equalsIgnoreCase("\n"))))
            {
                logger.info(msg.replaceAll("\n", ""));
            }
        }
    }

    public static void main(String[] args)
    {
        logger.setLevel(Level.INFO);

        int returnInt = 0;
        setupFileHandler();
        Installer inst = new Installer();

        /* Parse and set program arguments */
        try
        {
            inst.setInstallerOptions(args);
        }
        catch (ArgumentsException e)
        {
            argsHelp();
            System.exit(1);
        }

        logC("Installer Mode: " + arguments.getProperty(InstConstants.KEY_INSTALLER_MODE) + "\n");
        logC("E4A GL Wand Installer Started\n");

        /* Perform Installation */
        returnInt = inst.install();

        logger.finer("returnInt:" + returnInt);

        if (returnInt == 0)
        {
            logC("Installation completed successfully\n");
        }
        else
        {
            logC("Installation completed uncuccessfully, please review log " + logFileName + " for details.\n");
        }

        logC("Program Exit Status=" + String.valueOf(returnInt) + "\n");

        /* Flush Logs and Close File Handler */
        logFileHandler.flush();
        logFileHandler.close();

        /*
         * JAVA 1.4 Bug related to logger and FileHandles Does not occur on >
         * 1.4
         */
        cleanLogLckFile();

        /* Stop JVM */
        System.exit(returnInt);
    }

    private static void PrintLoggerInfo()
    {
        logger.fine("This logger's level is " + logger.getLevel());
        logger.fine("This logger's filter is " + logger.getFilter());
        logger.fine("Parent class is " + logger.getParent());
        logger.fine("Parent classname is " + logger.getParent().getName()); // ""

        Logger root = Logger.getLogger("");
        logger.fine("Root logger's level is " + root.getLevel());
        logger.fine("Root logger's filter is " + root.getFilter());
        Handler[] handlers = root.getHandlers();
        for (int i = 0; i < handlers.length; i++)
        {
            logger.fine("Handler is " + handlers[i]);
            logger.fine("Handler's level is " + handlers[i].getLevel());
            logger.fine("Handler's filter is " + handlers[i].getFilter());
            logger.fine("Handler's formatter is " + handlers[i].getFormatter());
        }
    }

    /**
     * Set all root console handles logging level
     * 
     * @param l
     */
    private static void setRootConsoleHandlerLevel(Level l)
    {
        Logger root = Logger.getLogger("");
        Handler[] handlers = root.getHandlers();
        for (int i = 0; i < handlers.length; i++)
        {
            handlers[i].setLevel(l);
        }
    }

    /**
     * Setup the File Handler used to write logging to file
     */
    private static void setupFileHandler()
    {
        logFileName = setupLog();
        logger.finer("Log File Name" + logFileName);
        try
        {
            logger.setUseParentHandlers(false);
            logFileHandler = new FileHandler(logFileName, false);
            logFileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(logFileHandler);
        }
        catch (SecurityException e)
        {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Generates a unique log file name for the installer
     * 
     * @return Installer log file name
     */
    private static String setupLog()
    {
        Date dateNow = new Date();
        SimpleDateFormat dateFormatFileName = new SimpleDateFormat("yyyyMMddhhmmss");
        String fileName = dateFormatFileName.format(dateNow);
        return InstConstants.LOG_FILE + "_" + fileName + ".log";
    }

    /**
     * Main installation method
     * 
     * @return a return code:
     * 
     *         0 = Success 1 = Error
     */
    private int install()
    {
        try
        {
            /* Setup Installation Context */
            SetupInstContext inst = new SetupInstContext();
            ic = inst.setup();

            /* Deploy Artifacts */
            DeployFiles.deploy(ic);
            logC("Files Deployed Successfully\n");

            /* Configure Servlet */
            ServletConfig.setup(ic);
            logC("Servlet Configured Successfully\n");

            /*
             * Only load OAF pages and APPS Configuration if performing DB and
             * APPS tier installation.
             */
            if (arguments.getProperty(InstConstants.KEY_INSTALLER_MODE).equals(InstConstants.MODE_DB_AND_APPS_TIER))
            {
                /* Install OAF Pages */
                LoadOAFPages lp = new LoadOAFPages();
                lp.load(ic);
                logC("OAF Pages Loaded Successfully\n");

                /* Load APPS Configuration */
                LoadAppsConfig.config(ic);
                logC("APPS Components Created Successfully\n");
            }
        }
        catch (InstContextException ex)
        {
            logC(ex.getMessage() + "\n");
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return 1;
        }
        catch (FileDeployException ex)
        {
            logC(ex.getMessage() + "\n");
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return 1;
        }
        catch (ServletConfigException ex)
        {
            logC(ex.getMessage() + "\n");
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return 1;
        }
        catch (LoadOAFPagesException ex)
        {
            logC(ex.getMessage() + "\n");
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return 1;
        }
        catch (LoadAppsConfigException ex)
        {
            logC(ex.getMessage() + "\n");
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return 1;
        }
        catch (Exception ex)
        {
            logC(ex.getMessage() + "\n");
            logger.log(Level.SEVERE, "Unhandled Exception");
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return 1;
        }

        return 0;
    }

    /**
     * Parse and stores arguments as installer options.
     * 
     * @param args
     *            Input arguments String[] passed to class via install.sh script
     * @throws ArgumentsException
     */
    private void setInstallerOptions(String[] args) throws ArgumentsException
    {
        int i = 0;
        /* Set Default Arguments */
        arguments = new Properties();
        arguments.setProperty(InstConstants.KEY_DEBUG, "N");
        arguments.setProperty(InstConstants.KEY_INSTALLER_MODE, InstConstants.MODE_DB_AND_APPS_TIER);

        while (i < args.length)
        {

            String arg = args[i++];

            /* Removes end-of-string anchoring */
            arg.replaceAll("\\r$", "");

            logger.info("Argument " + i + ": " + arg);

            /* Parse OPTIONS */
            if (arg.startsWith("-") || arg.startsWith("--"))
            {
                if (arg.equals("-d") || arg.equals("--debug"))
                {
                    logger.setLevel(Level.ALL);
                    logC("WARNING: Debug mode ON, log level=ALL");
                    setRootConsoleHandlerLevel(Level.ALL);
                    PrintLoggerInfo();
                    arguments.setProperty(InstConstants.KEY_DEBUG, "Y");
                }
                else
                {
                    throw new ArgumentsException();
                }
            }
            /* Parse Installation Mode */
            else if (arg.toUpperCase().equals(InstConstants.MODE_APPS_TIER))
            {
                arguments.setProperty(InstConstants.KEY_INSTALLER_MODE, arg.toUpperCase());
            }
            else
            {
                throw new ArgumentsException();
            }
        }
    }
}
