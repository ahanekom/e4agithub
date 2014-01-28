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
 * Perform R12 Servlet Configuration
 * 
 * @author Andries Hanekom
 * 
 */
public class ServletConfigR122 extends ServletConfig
{
    public static void configure(InstContext ic) throws ServletConfigException
    {
        String fndTop = ic.getOac().getFndTop();
        String templateTop;

        templateTop = fndTop + File.separator + "admin" + File.separator + "template";

        String templateFileString = templateTop + File.separator + InstConstants.APPS_12_SERVLET_FILE;
        String customFileString = templateTop + File.separator + "custom" + File.separator
                + InstConstants.APPS_12_SERVLET_FILE;

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
         * <!-- Excel4apps Wands 5 Alias --> <servlet>
         * <servlet-name>excel4apps</servlet-name>
         * <servlet-class>com.excel4apps.
         * servlet.wand.oracle.Servlet</servlet-class> </servlet>
         * <servlet-mapping> <servlet-name>excel4apps</servlet-name>
         * <url-pattern>/excel4apps</url-pattern> </servlet-mapping>
         */

        String eol = System.getProperty("line.separator");

        String sl1 = "<!-- Excel4apps Wands 5 Alias -->" + eol;
        String sl2 = "<servlet>" + eol;
        String sl3 = "<servlet-name>excel4apps</servlet-name>" + eol;
        ;

        String sl4 = "<servlet-class>com.excel4apps.servlet.wand.oracle.Servlet</servlet-class>" + eol;
        ;

        String sl5 = "</servlet>" + eol;
        ;
        String sl6 = "<servlet-mapping>" + eol;
        ;
        String sl7 = "<servlet-name>excel4apps</servlet-name>" + eol;
        ;
        String sl8 = "<url-pattern>/excel4apps</url-pattern>" + eol;
        ;
        String sl9 = "</servlet-mapping>" + eol;
        ;
        String sl10 = "</web-app>";
        String replacementString = sl1 + sl2 + sl3 + sl4 + sl5 + sl6 + sl7 + sl8 + sl9 + eol + sl10;

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
                String st1 = "</web-app>";
                int c1 = buffer.indexOf(st1);
                buffer.replace(c1, c1 + st1.length(), replacementString);

                br.close();
                BufferedWriter bw = new BufferedWriter(new FileWriter(customFile.getAbsoluteFile()));
                bw.write(buffer.toString());
                bw.close();
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
