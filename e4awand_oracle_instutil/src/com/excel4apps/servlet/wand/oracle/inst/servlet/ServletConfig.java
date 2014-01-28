package com.excel4apps.servlet.wand.oracle.inst.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import com.excel4apps.servlet.wand.oracle.inst.InstConstants;
import com.excel4apps.servlet.wand.oracle.inst.Installer;
import com.excel4apps.servlet.wand.oracle.inst.context.InstContext;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.ServletConfigException;

/**
 * Performs manipulation of Servlet Template File.
 * 
 * @author Andries Hanekom
 * 
 */
public class ServletConfig extends Installer
{
    public static void copyFiles(String source, String destination) throws IOException
    {
        logger.finer("Copy file " + source + " to " + destination);
        InputStream in = new FileInputStream(source);
        OutputStream out = new FileOutputStream(destination);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
        {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static boolean createCustomFile(File customFile, File templateFile) throws ServletConfigException
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
                copyFiles(templateFile.getAbsolutePath(), customFile.getAbsolutePath());
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

    public static boolean customFilExists(File customFile)
    {
        return (customFile.exists());
    }

    public static boolean customFolderExists(File customFolder)
    {
        return (customFolder.exists());
    }

    /**
     * Perform servlet config based on Application Version
     * 
     * @param ic
     * @throws ServletConfigException
     */
    public static void setup(InstContext ic) throws ServletConfigException
    {
        if (ic.appsMayorVersion.equals(InstConstants.APPS_VERSION_11))
        {
            ServletConfigR11.configure(ic);
        }
        else
        {
            ServletConfigR12.configure(ic);
        }
    }
}
