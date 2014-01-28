package com.excel4apps.servlet.wand.oracle.inst.oaf;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.security.Permission;
import java.util.logging.Level;

import oracle.jrad.tools.xml.importer.XMLImporter;

import com.excel4apps.servlet.wand.oracle.inst.InstConstants;
import com.excel4apps.servlet.wand.oracle.inst.Installer;
import com.excel4apps.servlet.wand.oracle.inst.context.InstContext;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.LoadOAFPagesException;

/**
 * Loads OAF page configuration to MDS
 * 
 * @author Andries Hanekom
 * 
 */
public class LoadOAFPages extends Installer
{
    /*
     * ExitManager Class prevents call to XMLImporter.main(params) to end
     * running JVM instance when the XMLImporter.main call completes and it
     * calls System.exit
     */
    class ExitManager extends SecurityManager
    {
        SecurityManager original;

        ExitManager(SecurityManager original)
        {
            this.original = original;
        }

        /** Deny permission to exit the VM. */
        public void checkExit(int status)
        {
            throw (new SecurityException());
        }

        /**
         * Allow this security manager to be replaced, if fact, allow pretty
         * much everything.
         */
        public void checkPermission(Permission perm)
        {
        }

        public SecurityManager getOriginalSecurityManager()
        {
            return original;
        }
    }

    ExitManager sm;

    public LoadOAFPages()
    {
        super();
        sm = new ExitManager(System.getSecurityManager());
        System.setSecurityManager(sm);
    }

    /**
     * Prints input arguments for call to XMLImporter.main
     * 
     * @param s
     *            The arguments String Array
     */
    private static void printArgs(String[] s)
    {
        String arg = new String();
        for (int i = 0; i < s.length; i++)
        {
            arg = arg.concat(" " + s[i]);
        }
        logger.finer("String Argument:" + arg);
    }

    /**
     * Performs the OAF page load
     * 
     * @param ic
     *            Installation Context
     * 
     * @throws LoadOAFPagesException
     */
    public void load(InstContext ic) throws LoadOAFPagesException
    {
        try
        {
            String[] pages = new String[] { "WandHomePG.xml", "WandAppletPG.xml" };

            for (int i = 0; i < (pages.length); i++)
            {
                String page;
                String appsMds;
                logger.finer("OAF Page=" + pages[i]);

                if (ic.appsMayorVersion.equals(InstConstants.APPS_VERSION_11))
                    appsMds = InstConstants.APPS_11_MDS;
                else
                    appsMds = InstConstants.APPS_12_MDS;

                page = ic.getOac().getApplTop() + File.separator + appsMds + File.separator + "webui" + File.separator
                        + pages[i];

                logger.finer("OAF Page=" + page);

                try
                {
                    loadFile(ic, page);
                }
                catch (SecurityException s)
                {
                    logger.finer("XMLImporter.main catch System.exit");
                }
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new LoadOAFPagesException("Unable to load OAF page, unhandled exception");
        }
        finally
        {
            System.setSecurityManager(sm.getOriginalSecurityManager());
        }
    }

    
    /**
     * Performs call to {@link XMLImporter}
     * 
     * @param ic Installation Context
     * @param page Page to be loaded
     * @throws LoadOAFPagesException
     */
    private void loadFile(InstContext ic, String page) throws LoadOAFPagesException
    {

        String[] params = setupParams(ic, page);

        PrintStream orgOutStream = null;
        PrintStream orgErrStream = null;
        ByteArrayOutputStream f = new ByteArrayOutputStream();

        PrintStream outPS = null;
        try
        {
            // Saving the orginal stream
            orgOutStream = System.out;
            orgErrStream = System.err;

            logger.finer("Log File Name: " + ic.getLogFileName());

            outPS = new PrintStream(new BufferedOutputStream(f));

            if (outPS.checkError())
            {
                logger.finer("outPS.checkError true");
            }

            // Redirecting console output to file
            System.setOut(outPS);

            System.out.println("Printing from System.out to log");

            // Redirecting runtime exceptions to file
            System.setErr(outPS);

            logger.finer("Before XMLImporter.doImport");

            XMLImporter.main(params);

            logger.finer("After XMLImporter.doImport");

        }
        catch (SecurityException s)
        {
            logger.finer("XMLImporter.main catch System.exit");

        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new LoadOAFPagesException("Unable to load OAF page");

        }
        catch (Throwable t)
        {
            logger.log(Level.SEVERE, t.getMessage(), t);
            throw new LoadOAFPagesException("Unable to load OAF page");
        }
        finally
        {
            // Restoring back to console
            outPS.flush();
            logger.finer(f.toString());
            outPS.close();
            System.setOut(orgOutStream);
            System.setErr(orgErrStream);
        }
    }

    /**
     * Build input parameters for call to {@link XMLImporter}
     * 
     * @param ic
     *            Installation Context
     * @param page
     *            OAF page to be loaded
     * @return The argument string for call to {@link XMLImporter}
     */
    private String[] setupParams(InstContext ic, String page)
    {

        String userNameParam = "-username";
        String passwordParam = "-password ";
        String dbConParam = "-dbconnection";
        String dbConValue = ic.getOac().getDBHost() + ":" + ic.getOac().getDBPort() + ":" + ic.getOac().getDBSid();
        String rootDirParam = "-rootdir";
        String rootDirValue = null;
        String rootPackageParam = "-rootPackage";
        String rootPackageValue = "/com/excel4apps/oracle/apps/xxe4a/wands";

        if (ic.appsMayorVersion.equals(InstConstants.APPS_VERSION_11))
        {
            rootDirValue = ic.getOac().getApplTop() + File.separator + InstConstants.APPS_11_MDS;
        }
        else
        {
            rootDirValue = ic.getOac().getApplTop() + File.separator + InstConstants.APPS_12_MDS;
        }

        String[] param1 = { page, userNameParam, ic.getAppsusername(), passwordParam,
                String.valueOf(ic.getAppspassword()), dbConParam, dbConValue, rootDirParam, rootDirValue,
                rootPackageParam, rootPackageValue };

        printArgs(param1);

        return param1;
    }
}
