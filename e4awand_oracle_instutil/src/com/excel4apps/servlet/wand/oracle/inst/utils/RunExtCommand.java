package com.excel4apps.servlet.wand.oracle.inst.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

import com.excel4apps.servlet.wand.oracle.inst.Installer;

/**
 * Run an external Linux/UNIX Operating System command.
 * 
 * @author Andries Hanekom
 * 
 */
public class RunExtCommand extends Installer
{

    /**
     * Run an external command on a Linux/UNIX Operation System
     * 
     * @param command
     * @return
     */
    public static int run(String command)
    {
        int exitVal = 0;
        String[] cmd = { "/bin/sh", "-c", command };

        try
        {
            logger.finer("External Command: " + cmd);

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd);

            /* Any Error Message */
            try
            {
                InputStreamReader esr = new InputStreamReader(proc.getErrorStream());

                BufferedReader br = new BufferedReader(esr);
                String line = null;

                while ((line = br.readLine()) != null)
                {
                    logger.finer("E_OUTPUT>" + line);
                }

                esr.close();
                br.close();
            }
            catch (IOException ioe)
            {
                logger.log(Level.SEVERE, ioe.getMessage(), ioe);
                exitVal = 1;
            }

            /* Any Output */
            try
            {
                InputStreamReader isr = new InputStreamReader(proc.getInputStream());

                BufferedReader br = new BufferedReader(isr);
                String line = null;

                while ((line = br.readLine()) != null)
                {
                    logger.finer("I_OUTPUT>" + line);
                }

                isr.close();
                br.close();
            }
            catch (IOException ioe)
            {
                logger.log(Level.SEVERE, ioe.getMessage(), ioe);
                exitVal = 1;
            }

            exitVal = proc.waitFor();
            logger.finer("Command ExitValue: " + proc.exitValue());
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, e.getMessage(), e);
            exitVal = 1;
        }
        return exitVal;
    }
}
