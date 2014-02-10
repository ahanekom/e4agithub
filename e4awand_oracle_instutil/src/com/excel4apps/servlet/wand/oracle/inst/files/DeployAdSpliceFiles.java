package com.excel4apps.servlet.wand.oracle.inst.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import com.excel4apps.servlet.wand.oracle.inst.InstConstants;
import com.excel4apps.servlet.wand.oracle.inst.Installer;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.FileDeployException;
import com.excel4apps.servlet.wand.oracle.inst.utils.ArchiveManager;
import com.excel4apps.servlet.wand.oracle.inst.utils.DatabaseHelper;

/**
 * Handles deployment of ADSPLICE utility configuration files
 * 
 * @author Andries Hanekom
 * 
 */
public class DeployAdSpliceFiles extends Installer
{

    private static final String XXE4APROD_TXT_FILE_NAME = "xxe4aprod.txt";
    private static String[] adspliceFileNames = new String[] { "newprods.txt", XXE4APROD_TXT_FILE_NAME, "xxe4aterr.txt" };

    /**
     * Copy ADSPLICE config files from installer source folder to ADSPLICE
     * Destination Folder
     * 
     * @throws FileDeployException
     */
    private static void copyFiles() throws FileDeployException
    {
        try
        {
            String adspliceDestFolder = ic.getOac().getApplTop() + File.separator + "admin" + File.separator;

            for (int i = 0; i < adspliceFileNames.length; i++)
            {
                ArchiveManager.copyFiles(InstConstants.ADSPLICE_SOURCE_FOLDER + File.separator + adspliceFileNames[i],
                        adspliceDestFolder + adspliceFileNames[i]);
            }
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new FileDeployException("Unable to deploy AdSplice Configuration Files");
        }
    }

    /**
     * Entry method for file deployment. Checks availability of default XXE4A
     * application id {@link InstConstants.XXE4A_APP_ID_DEFAULT}, if not
     * available get's next available id and updates config files. Files are
     * then deployed to default ADSPLICE processing area $APPL_TOP/admin.
     * 
     * @throws FileDeployException
     */
    public static void deploy() throws FileDeployException
    {
        DatabaseHelper databaseHelper = new DatabaseHelper();

        try
        {
            databaseHelper.databaseConnectionTest();

            if (!databaseHelper.isXXE4AAppConfigured())
            {

                int appId = InstConstants.XXE4A_APP_ID_DEFAULT;
                int i = -1;
                while (i == -1)
                {
                    logger.finest("appId=" + appId);
                    i = verifyAppIdAvailibility(appId);
                    appId++;
                }
                logger.finest("Availble Application id=" + i);

                if (appId == InstConstants.XXE4A_APP_ID_DEFAULT)
                {
                    copyFiles();
                }
                else
                {
                    updateAdSpliceFile(i);

                    copyFiles();
                }
            }
            else
            {
                throw new FileDeployException("Excel4apps XXE4A Application Already Configured");
            }
        }

        catch (SQLException ex)
        {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new FileDeployException("Unable to validate XXE4A Application Configuration");
        }
    }

    /**
     * Modify the ADSPLICE config files of the DEFAULT XXE4A Application ID has
     * been used.
     * 
     * @param appId
     * @throws FileDeployException
     */
    private static void updateAdSpliceFile(int appId) throws FileDeployException
    {
        try
        {
            String applicationId = new String(Integer.toString(appId));
            logger.finest("Application id used for update=" + applicationId);

            File customFile = new File(InstConstants.ADSPLICE_SOURCE_FOLDER + File.separator + XXE4APROD_TXT_FILE_NAME);
            String eol = System.getProperty("line.separator");

            StringBuffer buffer = new StringBuffer();
            String str;
            BufferedReader br = new BufferedReader(new FileReader(customFile.getAbsoluteFile()));

            while (true)
            {
                str = br.readLine();

                if (str == null)
                {
                    break;
                }

                if (str.indexOf(Integer.toString(InstConstants.XXE4A_APP_ID_DEFAULT)) != -1)
                {
                    logger.finest("Before: " + str);

                    str = str.replace(Integer.toString(InstConstants.XXE4A_APP_ID_DEFAULT), applicationId);

                    logger.finest("After: " + str);

                }
                str = str + eol;
                buffer.append(str);
            }
            br.close();
            BufferedWriter bw = new BufferedWriter(new FileWriter(customFile.getAbsoluteFile()));
            bw.write(buffer.toString());
            bw.close();

        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new FileDeployException("Unable to update AdSplice Configuration Files");
        }
    }

    /**
     * Verify availability of Application ID parameter
     * 
     * @param applicationID
     * @return
     * @throws FileDeployException
     */
    private static int verifyAppIdAvailibility(int applicationID) throws FileDeployException
    {

        DatabaseHelper databaseHelper = new DatabaseHelper();

        ResultSet rset;

        try
        {
            logger.finer("Application XXE4A Test");

            StringBuffer sql = new StringBuffer();
            sql.append("SELECT COUNT ");
            sql.append("FROM ");
            sql.append("  (SELECT COUNT(*) AS COUNT ");
            sql.append("  FROM ");
            sql.append("    ( SELECT 'x' FROM fnd_oracle_userid WHERE oracle_id = " + applicationID);
            sql.append("    UNION ");
            sql.append("    SELECT 'x' FROM fnd_application WHERE application_id = " + applicationID);
            sql.append("    ) ");
            sql.append("  )");
            rset = databaseHelper.executeQuery(sql.toString());

            while (rset.next())
            {
                logger.finer(String.valueOf(rset.getInt(1)));

                if (rset.getInt(1) == 0)
                {
                    /* Exists */
                    return applicationID;
                }
                else if (rset.getInt(1) > 0)
                {
                    /* Does not Exist */
                    return -1;
                }
            }

        }
        catch (SQLException ex)
        {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new FileDeployException("Exception raised during Application ID availibility check");
        }

        return applicationID;
    }
}
