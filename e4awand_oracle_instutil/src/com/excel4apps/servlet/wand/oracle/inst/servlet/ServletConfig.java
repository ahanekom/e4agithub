package com.excel4apps.servlet.wand.oracle.inst.servlet;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import com.excel4apps.servlet.wand.oracle.inst.Installer;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.ServletConfigException;
import com.excel4apps.servlet.wand.oracle.inst.utils.ArchiveManager;

/**
 * Performs manipulation of Servlet Template File.
 * 
 * @author Andries Hanekom
 * 
 */
abstract class ServletConfig extends Installer
{
    protected File templateFile;
    protected File customFile;

    public void configure(String servletFile) throws ServletConfigException
    {
        String fndTop = ic.getOac().getFndTop();
        String templateTop;

        templateTop = fndTop + File.separator + "admin" + File.separator + "template";

        String templateFileString = templateTop + File.separator + servletFile;
        String customFileString = templateTop + File.separator + "custom" + File.separator + servletFile;

        customFile = new File(customFileString);
        templateFile = new File(templateFileString);

        if (customFile.exists())
        {
            modifyCustomFile(customFile);
        }
        else
        {
            createCustomFile(customFile, templateFile);

            if (customFile.exists())
            {
                modifyCustomFile(customFile);
            }
        }
    }

    /**
     * Creates custom file from template
     * 
     * @param customFile
     * @param templateFile
     * @return
     * @throws ServletConfigException
     */
    public boolean createCustomFile(File customFile, File templateFile) throws ServletConfigException
    {
        logger.finer("Custom File: " + customFile.getPath());
        logger.finer("Template File: " + templateFile.getPath());

        if (templateFile.exists())
        {

            String absolutePath = customFile.getAbsolutePath();
            String filePath = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
            File customFolder = new File(filePath);

            if (!customFolder.exists())
            {
                customFolder.mkdir();
            }

            try
            {
                ArchiveManager.copyFiles(templateFile.getAbsolutePath(), customFile.getAbsolutePath());
            }
            catch (IOException e)
            {
                logger.log(Level.SEVERE, e.getMessage(), e);
                throw new ServletConfigException("Unable to setup servlet file");
            }
        }
        else
        {
            throw new ServletConfigException("Unable to setup servlet file: template file does not exist");
        }
        return true;
    }

    public boolean customFilExists(File customFile)
    {
        return (customFile.exists());
    }

    public boolean customFolderExists(File customFolder)
    {
        return (customFolder.exists());
    }

    protected abstract void modifyCustomFile(File customFile) throws ServletConfigException;
}
