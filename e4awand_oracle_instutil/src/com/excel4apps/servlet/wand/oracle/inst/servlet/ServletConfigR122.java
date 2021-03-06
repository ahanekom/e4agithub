package com.excel4apps.servlet.wand.oracle.inst.servlet;

import java.io.IOException;
import java.util.logging.Level;

import com.excel4apps.servlet.wand.oracle.inst.exceptions.ServletConfigException;
import com.excel4apps.servlet.wand.oracle.inst.utils.ArchiveManager;

/**
 * Perform R12.2 Servlet Configuration
 * 
 * @author Andries Hanekom
 * 
 */
public class ServletConfigR122 extends ServletConfigR12
{
    /**
     * Oracle E-Business Suite R12.2.2: Bug 17594779 - CUSTOM AUTOCONFIG
     * TEMPLATE OACORE WEB.XML OVERWRITTEN AT STARTUP
     * 
     * @throws ServletConfigException
     */
    public void applyWorkaround() throws ServletConfigException
    {
        logger.fine("Applying R12.2.2: Bug 17594779 Workaround");
        try
        {
            ArchiveManager.copyFiles(templateFile.getAbsolutePath(), templateFile.getAbsolutePath() + ".xxe4a_backup");
            ArchiveManager.copyFiles(customFile.getAbsolutePath(), templateFile.getAbsolutePath());
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ServletConfigException("Unable to setup servlet file");
        }
    }
}
