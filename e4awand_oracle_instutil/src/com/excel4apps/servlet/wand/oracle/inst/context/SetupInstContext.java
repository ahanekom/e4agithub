package com.excel4apps.servlet.wand.oracle.inst.context;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.excel4apps.servlet.wand.oracle.inst.InstConstants;
import com.excel4apps.servlet.wand.oracle.inst.Installer;
import com.excel4apps.servlet.wand.oracle.inst.context.xml.OAContextParser;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.InstContextException;
import com.excel4apps.servlet.wand.oracle.inst.utils.PasswordField;

/**
 * This class performs the setup of the Installation Context Object.
 * 
 * @author Andries Hanekom
 * 
 */
public class SetupInstContext extends Installer
{

    public static final Logger logger = Logger.getLogger(Installer.class.getName());

    private InstContext ic = new InstContext();

    private static void logC(String msg)
    {

        if (msg != null)
        {
            System.out.println(msg);

            if (!msg.equalsIgnoreCase(""))
            {
                logger.info(msg.replaceAll("\n", ""));
            }
        }
    }

    // Utility function to read a line from standard input
    static String readEntry(String prompt)
    {
        try
        {
            StringBuffer buffer = new StringBuffer();

            System.out.print(prompt);
            System.out.flush();

            int c = System.in.read();

            while ((c != '\n') && (c != -1))
            {
                buffer.append((char) c);
                c = System.in.read();
            }

            return buffer.toString().trim();
        }
        catch (IOException e)
        {
            return "";
        }
    }

    /**
     * Performs a database connection test using information from the
     * Installation Context. A simple select from dual is performed and once
     * Successful the method confirms that the XXE4A application has been
     * Registered, a prerequisite for the running of this tool as described in
     * the Installation documentation.
     * 
     * @throws InstContextException
     */
    private void databaseTest() throws InstContextException
    {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;

        try
        {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

            conn = DriverManager.getConnection("jdbc:oracle:thin:@" + ic.getOac().getDBHost() + ":"
                    + ic.getOac().getDBPort() + ":" + ic.getOac().getDBSid(), ic.getAppsusername(),
                    String.valueOf(ic.getAppspassword()));

            logger.finer("Connected.");

            // Create a statement
            stmt = conn.createStatement();

            // Do the SQL "Hello World" thing
            rset = stmt.executeQuery("select 'Hello World' from dual");

            while (rset.next())
            {
                logger.finer(rset.getString(1));
            }

            logger.finer("Login Successfull");

            try
            {
                logger.finer("Application XXE4A Test");

                stmt = conn.createStatement();
                rset.close();
                rset = stmt.executeQuery("select COUNT(*) from FND_APPLICATION where APPLICATION_SHORT_NAME = 'XXE4A'");

                while (rset.next())
                {
                    logger.finer(String.valueOf(rset.getInt(1)));

                    if (rset.getInt(1) == 1)
                    {
                        logger.finer("Application XXE4A Registered");
                    }
                    else
                    {
                        throw new InstContextException(
                                "Application XXE4A Not Registered, please register the application and rerun the installer.");
                    }
                }

            }
            catch (SQLException ex)
            {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
                throw new InstContextException("Excel4apps EBS application: XXE4A not registered");
            }

        }
        catch (SQLException ex)
        {

            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new InstContextException("Database login test unsuccessfull");
        }
        finally
        {
            try
            {
                if (rset != null)
                {
                    rset.close();
                }
                if (stmt != null)
                {
                    stmt.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
                throw new InstContextException(e.getMessage());
            }
        }
    }

    private void getInstCredentials() throws InstContextException
    {
        String user = null;

        user = readEntry("Enter the APPS username [APPS]: ");

        if ((((user != null) && (user.equalsIgnoreCase("APPS"))) || (user.equals(""))))
        {
            ic.setAppsusername("apps");
        }
        else
        {
            ic.setAppsusername(user);
        }

        logger.finer("Apps User=" + ic.getAppsusername());
        logC("\n");

        try
        {
            char[] pass;
            pass = PasswordField.getPassword(System.in, "Enter the " + ic.getAppsusername().toUpperCase()
                    + " password: ");

            logC("\n");

            if (pass == null)
            {
                throw new InstContextException("Password is NULL");
            }

            ic.setAppspassword(pass);
        }
        catch (IOException ioe)
        {
            logger.log(Level.SEVERE, ioe.getMessage(), ioe);
            throw new InstContextException("Unable to read password");
        }

        catch (InstContextException i)
        {
            logger.log(Level.SEVERE, i.getMessage(), i);
            throw new InstContextException(i.getMessage());
        }
    }

    private void getOAContext() throws InstContextException
    {
        OAContextParser oa = new OAContextParser();
        String contextFile = System.getProperty("CONTEXT_FILE");

        logger.finer("Context File:" + contextFile);

        try
        {
            oa.parse(contextFile);
            ic.setOac(oa.oaContext);
            ic.setContextFile(contextFile);
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new InstContextException("Unable to setup OAContext");
        }
    }

    /**
     * Read the database connection details from the console input if user
     * selects not to use database connection details from CONTEXT_FILE.
     * 
     */
    private void readDbConnect()
    {
        logC("");
        logger.finer("User entered database connection details.");
        ic.getOac().setDBHost(readEntry("Enter Database server host name : (eg:- server.domain.com): "));
        logger.finer("DBHost: " + ic.getOac().getDBHost());
        ic.getOac().setDBPort(readEntry("Enter Database Port Number : (eg:- 1521): "));
        logger.finer("DBPort: " + ic.getOac().getDBPort());
        ic.getOac().setDBSid(readEntry("Enter Database SID : (eg:- DEV): "));
        logger.finer("DBSid: " + ic.getOac().getDBSid());
    }

    /**
     * Collects required information, perform validations and set's up the
     * Installation Context
     * 
     * @return Installation Context
     * @throws InstContextException
     */
    public InstContext setup() throws InstContextException
    {
        ic.setLogFileName(Installer.logFileName);
        getOAContext();
        setupAppsMajorVersion();

        validateR122FileEdition();

        if (arguments.getProperty(InstConstants.KEY_INSTALLER_MODE).equals(InstConstants.MODE_DB_AND_APPS_TIER))
        {
            getInstCredentials();
            setupDbConnectDetails();
            databaseTest();
        }

        return ic;
    }

    private void setupAppsMajorVersion()
    {
        String appsMayorVersion = ic.getOac().getAppsVersion().substring(0, 2);

        if (appsMayorVersion.equals("12"))
        {
            logger.finer(ic.getOac().getAppsVersion().substring(3, 4));
            if (ic.getOac().getAppsVersion().substring(3, 4).equals("2"))
            {
                appsMayorVersion = "12.2";
            }
        }

        ic.setAppsMayorVersion(appsMayorVersion);
        logger.finer("appsMayorVersion=" + appsMayorVersion);
    }

    private void setupDbConnectDetails()
    {
        if ((ic.getOac().getDBHost() == null) || (ic.getOac().getDBPort() == null) || (ic.getOac().getDBSid() == null))
        {
            readDbConnect();
        }
        else
        {
            logC("");
            logC("Database Connection Details from Environment Context File:");
            logC(ic.getContextFile());
            logC("==========================================================");
            logC("Server: " + ic.getOac().getDBHost());
            logC("Port: " + ic.getOac().getDBPort());
            logC("Sid: " + ic.getOac().getDBSid());
            logC("");
            String confirmation;

            confirmation = readEntry("Enter Y to use the following connection settings [Y]: ");

            logger.finer("confirmation=" + confirmation);

            if ((confirmation != null) && (confirmation.equalsIgnoreCase("N")))
            {
                readDbConnect();
            }

        }
        logC("");
        logC("Database Connection Details used for this installation: " + ic.getOac().getDBHost() + ":"
                + ic.getOac().getDBPort() + ":" + ic.getOac().getDBSid());
        logC("");
    }

    private void validateR122FileEdition() throws InstContextException
    {
        if (!ic.getOac().getFileEditionType().equals(InstConstants.FILE_EDITION_TYPE_PATCH))
        {
            throw new InstContextException("You must be connected to the Patch Edition File System.");
        }
    }
}
