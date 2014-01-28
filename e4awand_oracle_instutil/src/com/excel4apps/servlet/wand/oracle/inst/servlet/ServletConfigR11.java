package com.excel4apps.servlet.wand.oracle.inst.servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

import com.excel4apps.servlet.wand.oracle.inst.InstConstants;
import com.excel4apps.servlet.wand.oracle.inst.context.InstContext;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.ServletConfigException;

/**
 * Perform R11 Servlet Configuration
 * 
 * @author Andries Hanekom
 * 
 */
public class ServletConfigR11 extends ServletConfig
{
    public static void configure(InstContext ic) throws ServletConfigException
    {
        String fndTop = ic.getOac().getFndTop();
        String templateTop;

        templateTop = fndTop + File.separator + "admin" + File.separator + "template";

        String templateFileString = templateTop + File.separator + InstConstants.APPS_11_SERVLET_FILE;
        String customFileString = templateTop + File.separator + "custom" + File.separator
                + InstConstants.APPS_11_SERVLET_FILE;

        File customFile = new File(customFileString);
        File templateFile = new File(templateFileString);

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

    private static void modifyCustomFile(File customFile) throws ServletConfigException
    {
        /*
         * #------- Excel4apps Wands 5 Alias --------------------------------
         * servlet.excel4apps.code=com.excel4apps.servlet.wand.oracle.Servlet
         * #-----------------------------------------------------------------
         */

        String eol = System.getProperty("line.separator");

        String sl1 = "#------- Excel4apps Wands 5 Alias --------------------------------" + eol;
        String sl2 = "servlet.excel4apps.code=com.excel4apps.servlet.wand.oracle.Servlet" + eol;
        String sl3 = "#-----------------------------------------------------------------" + eol;

        try
        {

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
                str = str + eol;
                buffer.append(str);
            }

            if (buffer.indexOf(sl1) == -1)
            {
                FileWriter fileWritter;
                fileWritter = new FileWriter(customFile.getAbsoluteFile(), true);
                BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                bufferWritter.write(sl1);
                bufferWritter.write(sl2);
                bufferWritter.write(sl3);
                bufferWritter.close();
            }
            else
            {
                logger.finer(sl1 + " Exists, no modification performed");
                br.close();
            }

        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ServletConfigException("Unable to modify servlet file");
        }
    }
}
