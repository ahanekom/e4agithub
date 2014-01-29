package com.excel4apps.servlet.wand.oracle.inst.files;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import com.excel4apps.servlet.wand.oracle.inst.InstConstants;
import com.excel4apps.servlet.wand.oracle.inst.Installer;
import com.excel4apps.servlet.wand.oracle.inst.context.InstContext;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.FileDeployException;
import com.excel4apps.servlet.wand.oracle.inst.utils.ArchiveManager;

/**
 * Manages deployment of Wands file artifacts.
 * 
 * @author Andries Hanekom
 * 
 */
public class DeployFiles extends Installer
{
    private static boolean cleanUp(File directory)
    {
        if (directory == null)
        {
            logger.finer("directory == null");
            return false;
        }
        else if (!directory.exists())
        {
            logger.finer("directory does not exist: " + directory.getPath());
            return true;
        }
        else if (!directory.isDirectory())
        {
            logger.finer("not a directory: " + directory.getPath());
            return false;
        }

        String[] list = directory.list();

        // Some JVMs return null for File.list() when the
        // directory is empty.
        if (list != null)
        {
            for (int i = 0; i < list.length; i++)
            {
                File entry = new File(directory, list[i]);

                logger.fine("removing entry " + entry);

                if (entry.isDirectory())
                {
                    if (!cleanUp(entry))
                    {
                        return false;
                    }
                }
                else
                {
                    if (!entry.delete())
                    {
                        return false;
                    }
                }
            }
        }

        return directory.delete();
    }

    public static void deploy(InstContext ic) throws FileDeployException
    {
        try
        {
            File mayorReleaseZipFile;
            File extractToFolder;

            // Unzip Release specific file
            if (ic.getAppsMayorVersion().equals(InstConstants.APPS_VERSION_12))
            {
                mayorReleaseZipFile = new File(InstConstants.APPS_12_ZIP_FILE);
                extractToFolder = new File("." + File.separator + InstConstants.APPS_VERSION_12);
            }
            else
            {
                mayorReleaseZipFile = new File(InstConstants.APPS_11_ZIP_FILE);
                extractToFolder = new File("." + File.separator + InstConstants.APPS_VERSION_12);
            }

            ArchiveManager.unzip(mayorReleaseZipFile, extractToFolder);

            deployJavaTop(ic, extractToFolder);

            deployApplTop(ic, extractToFolder);

            if (cleanUp(extractToFolder))
            {
                logger.finer("Cleanup Done");
            }
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new FileDeployException("Unable to deploy files");
        }
    }

    private static void deployApplTop(InstContext ic, File extractFromFolder) throws IOException
    {
        File applTopZipFile;
        File applTopFolder;

        applTopZipFile = new File(extractFromFolder + File.separator + InstConstants.APPL_TOP_ZIP_FILE);

        applTopFolder = new File(ic.getOac().getApplTop());

        ArchiveManager.unzip(applTopZipFile, applTopFolder);
    }

    private static void deployJavaTop(InstContext ic, File extractFromFolder) throws IOException
    {
        File javaTopZipFile;
        File javaTopFolder;

        javaTopZipFile = new File(extractFromFolder + File.separator + InstConstants.JAVA_TOP_ZIP_FILE);

        javaTopFolder = new File(ic.getOac().getJavaTop());

        ArchiveManager.unzip(javaTopZipFile, javaTopFolder);
    }
}
