package com.excel4apps.servlet.wand.oracle.inst.apps;

import java.io.File;

import com.excel4apps.servlet.wand.oracle.inst.InstConstants;
import com.excel4apps.servlet.wand.oracle.inst.Installer;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.LoadAppsConfigException;
import com.excel4apps.servlet.wand.oracle.inst.utils.RunExtCommand;

/**
 * Loads EBS Application Artifacts: Menu, Responsibility and Executable
 * 
 * @author Andries Hanekom
 * 
 */
public class LoadAppsConfig extends Installer
{

    /**
     * Generates the FNDLOAD command for the Executable
     * 
     * @param userCreds
     *            Username and Password
     * @param installDir
     *            Installation Directory
     * @return Concurrent Program FNDLOAD command
     */
    private static String buildCPCmd(String userCreds, String installDir)
    {
        String lctFile = InstConstants.FND_LOAD_CP_LDT;
        String fileName = installDir + File.separator + InstConstants.XXE4A_XXE4A_CP_LDT;
        String respCmd = buildFndLoadCmd(userCreds, lctFile, fileName);
        return respCmd;
    }

    /**
     * Create FNDLOAD command string
     * 
     * @param userCreds
     *            Username and Password
     * @param lctFile
     *            FNDLOAD LCT control filename
     * @param filename
     *            FNDLOAD LDT filename
     * @return
     */
    private static String buildFndLoadCmd(String userCreds, String lctFile, String filename)
    {
        String cmd = "$FND_TOP/bin/FNDLOAD " + userCreds + " 0 Y UPLOAD " + lctFile + " " + filename;
        return cmd;
    }

    /**
     * Generates the FNDLOAD command for the Menu
     * 
     * @param userCreds
     *            Username and Password
     * @param installDir
     *            Installation Directory
     * @return Menu FNDLOAD command
     */
    private static String buildMenuCmd(String userCreds, String installDir)
    {
        String lctFile = InstConstants.FND_LOAD_MENU_LDT;
        String fileName = installDir + File.separator + "XXE4A_WANDS_MENU.ldt";
        String menuCmd = buildFndLoadCmd(userCreds, lctFile, fileName);
        return menuCmd;
    }

    /**
     * Generates the FNDLOAD command for the Responsibility
     * 
     * @param userCreds
     *            Username and Password
     * @param installDir
     *            Installation Directory
     * @return Responsibility FNDLOAD command
     */
    private static String buildRespCmd(String userCreds, String installDir)
    {
        String lctFile = InstConstants.FND_LOAD_RESP_LDT;
        String fileName = installDir + File.separator + "XXE4A_WANDS_RESP.ldt";
        String respCmd = buildFndLoadCmd(userCreds, lctFile, fileName);
        return respCmd;
    }

    /**
     * Load the Wands Menu, Executable and Responsibility configuration
     * 
     * @throws LoadAppsConfigException
     */
    public static void config() throws LoadAppsConfigException
    {

        String userCreds = ic.getAppsusername() + "/" + String.valueOf(ic.getAppspassword());
        String installDir = ic.getOac().getJavaTop() + File.separator + "com" + File.separator + "excel4apps"
                + File.separator + "install";
        int returnCode = RunExtCommand.run(buildMenuCmd(userCreds, installDir));

        if (returnCode != 0)
        {
            throw new LoadAppsConfigException("Unable to load menu using FNDLOAD");
        }
        else
        {
            logger.info("Menu loaded successfully");
            returnCode = RunExtCommand.run(buildRespCmd(userCreds, installDir));
            if (returnCode != 0)
            {
                throw new LoadAppsConfigException("Unable to load responsibility using FNDLOAD");
            }
            else
            {
                logger.info("Responsibility loaded successfully");
                returnCode = RunExtCommand.run(buildCPCmd(userCreds, installDir));
                if (returnCode != 0)
                {
                    throw new LoadAppsConfigException("Unable to load concurrent program using FNDLOAD");
                }
                else
                {
                    logger.info("Concurrent program  loaded successfully");
                }
            }
        }
    }
}
