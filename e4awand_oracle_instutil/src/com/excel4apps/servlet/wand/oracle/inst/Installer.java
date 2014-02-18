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
import com.excel4apps.servlet.wand.oracle.inst.exceptions.InstallValidationException;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.LoadAppsConfigException;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.LoadOAFPagesException;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.ServletConfigException;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.UpdateAdopSyncFileException;
import com.excel4apps.servlet.wand.oracle.inst.files.DeployAdSpliceFiles;
import com.excel4apps.servlet.wand.oracle.inst.files.DeployApplicationFiles;
import com.excel4apps.servlet.wand.oracle.inst.files.UpdateAdopSyncFile;
import com.excel4apps.servlet.wand.oracle.inst.oaf.LoadOAFPages;
import com.excel4apps.servlet.wand.oracle.inst.servlet.ServletConfigR11;
import com.excel4apps.servlet.wand.oracle.inst.servlet.ServletConfigR12;
import com.excel4apps.servlet.wand.oracle.inst.servlet.ServletConfigR122;
import com.excel4apps.servlet.wand.oracle.inst.utils.InstallValidation;

/**
 * Excel4apps Wands Installation Tool
 * 
 * @author Andries Hanekom
 * 
 */

public class Installer
{
    protected static final Logger logger = Logger.getLogger(Installer.class.getName());
    protected static String logFileName;
    private static Handler logFileHandler;
    private static Properties arguments;
    protected static InstContext ic;

    /**
     * Displays help information for Installer tool
     * 
     * Options: -d, --debug - Set the java logging framework LEVEL to ALL. <br>
     * Mode: <br>
     * MODE_DB_AND_APPS_TIER - Performs complete installation of database and
     * application tier artifacts. <br>
     * APPS_TIER - Only installs the application tier components. <br>
     * DEPLOY_ADSPLICE_FILES - Deploys ADSPLICE config files for R12.2 custom
     * application configuration.
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
                .println("install.sh -d APPS_TIER  # Installs only the APPS tier components for GL Wand in debug mode");
        System.out.println("");
        System.out.println("Options:");
        System.out.println("  -d, --debug   Debug mode on");
        System.out.println("");
        System.out.println("Mode: (Optional)");
        System.out.println("  APPS_TIER                 Installs only the APPS tier components for GL Wand");
        System.out
                .println("  DEPLOY_ADSPLICE_FILES     Deploy required config files for ADSPLICE utlity to $APPL_TOP/admin folder");
        System.out.println("                          Only Availble on R12.2.X");
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

        logC("Installer Mode: " + arguments.getProperty(InstConstants.INSTALLER_MODE_KEY) + "\n");
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
     *            Sets log level
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
     * Install Application and Database Components
     * 
     * @throws InstContextException
     * @throws ServletConfigException
     * @throws FileDeployException
     * @throws LoadOAFPagesException
     * @throws LoadAppsConfigException
     * @throws InstallValidationException
     * @throws UpdateAdopSyncFileException
     */
    private void appsAndDBTierInstall() throws InstContextException, ServletConfigException, FileDeployException,
            LoadOAFPagesException, LoadAppsConfigException, InstallValidationException, UpdateAdopSyncFileException
    {
        /* Setup Installation Context */
        SetupInstContext inst = new SetupInstContext();
        ic = inst.setup(true);

        /* Validate Installation Environment */
        InstallValidation.validate(true);

        /* Configure Servlet */
        setupServlet();
        logC("Servlet Configured Successfully\n");

        /* Deploy Artifacts */
        DeployApplicationFiles.deploy();
        logC("Files Deployed Successfully\n");

        /* Install OAF Pages */
        LoadOAFPages lp = new LoadOAFPages();
        lp.load();
        logC("OAF Pages Loaded Successfully\n");

        /* Load APPS Configuration */
        LoadAppsConfig.config();
        logC("APPS Components Created Successfully\n");

        if (ic.getAppsMayorVersion().equals(InstConstants.APPS_VERSION_12_2))
        {
            UpdateAdopSyncFile.update();
            logC("ADOP Custom Synchronization Driver File Updated Successfully\n");
        }
    }

    /**
     * Installs Only Application Tier components
     * 
     * @throws InstContextException
     * @throws ServletConfigException
     * @throws FileDeployException
     * @throws InstallValidationException
     * @throws UpdateAdopSyncFileException
     */
    private void appsTierInstall() throws InstContextException, ServletConfigException, FileDeployException,
            InstallValidationException, UpdateAdopSyncFileException
    {
        /* Setup Installation Context */
        SetupInstContext inst = new SetupInstContext();
        ic = inst.setup(false);

        /* Validate Installation Environment */
        InstallValidation.validate(false);

        /* Configure Servlet */
        setupServlet();
        logC("Servlet Configured Successfully\n");

        /* Deploy Artifacts */
        DeployApplicationFiles.deploy();
        logC("Files Deployed Successfully\n");

        if (ic.getAppsMayorVersion().equals(InstConstants.APPS_VERSION_12_2))
        {
            UpdateAdopSyncFile.update();
            logC("ADOP Custom Synchronization Driver File Updated Successfully\n");
        }
    }

    /**
     * Deploy's ADSPLICE config files required to create custom application
     * XXE4A on Oracle EBS R12.2.X
     * 
     * @throws InstContextException
     * @throws FileDeployException
     */
    private void deployAdSpliceFiles() throws InstContextException, FileDeployException
    {
        /* Setup Installation Context */
        SetupInstContext inst = new SetupInstContext();
        ic = inst.setup(true);

        if (ic.getAppsMayorVersion().equals(InstConstants.APPS_VERSION_12_2))
        {
            DeployAdSpliceFiles.deploy();
            logC("ADSPLICE Files Deployed Successfully\n");
        }
        else
        {
            throw new FileDeployException(
                    "Option only available on a Oracle EBS 12.2 environment, not action performed");
        }
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
            if (arguments.getProperty(InstConstants.INSTALLER_MODE_KEY).equals(
                    InstConstants.INSTALLER_MODE_DB_AND_APPS_TIER))
            {
                appsAndDBTierInstall();
            }
            else if (arguments.getProperty(InstConstants.INSTALLER_MODE_KEY).equals(
                    InstConstants.INSTALLER_MODE_APPS_TIER))
            {
                appsTierInstall();
            }

            else if (arguments.getProperty(InstConstants.INSTALLER_MODE_KEY).equals(
                    InstConstants.INSTALLER_MODE_DEPLOY_ADSPLICE_FILES))
            {
                deployAdSpliceFiles();
            }

        }
        catch (Exception ex)
        {
            logC("ERROR: " + ex.getMessage() + "\n");
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
        arguments.setProperty(InstConstants.DEBUG_KEY, "N");
        arguments.setProperty(InstConstants.INSTALLER_MODE_KEY, InstConstants.INSTALLER_MODE_DB_AND_APPS_TIER);

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
                    arguments.setProperty(InstConstants.DEBUG_KEY, "Y");
                }
                else
                {
                    throw new ArgumentsException();
                }
            }
            /* Parse Installation Mode */
            else if ((arg.toUpperCase().equals(InstConstants.INSTALLER_MODE_APPS_TIER))
                    || (arg.toUpperCase().equals(InstConstants.INSTALLER_MODE_DEPLOY_ADSPLICE_FILES)))
            {
                arguments.setProperty(InstConstants.INSTALLER_MODE_KEY, arg.toUpperCase());
            }
            else
            {
                throw new ArgumentsException();
            }
        }
    }

    /**
     * Perform servlet config based on Application Version
     * 
     * @throws ServletConfigException
     */
    private void setupServlet() throws ServletConfigException
    {

        if (ic.getAppsMayorVersion().equals(InstConstants.APPS_VERSION_11))
        {
            ServletConfigR11 servletConfigR11 = new ServletConfigR11();
            servletConfigR11.configure(InstConstants.APPS_11_SERVLET_FILE);
        }
        else if (ic.getAppsMayorVersion().equals(InstConstants.APPS_VERSION_12))
        {
            ServletConfigR12 servletConfigR12 = new ServletConfigR12();
            servletConfigR12.configure(InstConstants.APPS_12_SERVLET_FILE);
        }
        else if (ic.getAppsMayorVersion().equals(InstConstants.APPS_VERSION_12_2))
        {
            ServletConfigR122 servletConfigR122 = new ServletConfigR122();
            servletConfigR122.configure(InstConstants.APPS_122_SERVLET_FILE);
            servletConfigR122.applyWorkaround();
        }
        else
        {
            throw new ServletConfigException(
                    "Unable to determine Oracle EBS Application version for web.xml Servlet  Configuration");
        }
    }
}
