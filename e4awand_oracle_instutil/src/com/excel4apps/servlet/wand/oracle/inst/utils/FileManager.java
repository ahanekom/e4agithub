package com.excel4apps.servlet.wand.oracle.inst.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.excel4apps.servlet.wand.oracle.inst.Installer;

/**
 * File management utility class
 * 
 * @author Andries Hanekom
 * 
 */
public class FileManager extends Installer
{

    /**
     * Replace target contents of file with replacement String
     * 
     * @param file
     * @param target
     * @param replacement
     * @throws IOException
     */
    public static void modifyFile(File file, String target, String replacement) throws IOException
    {

        if (file.exists())
        {
            String eol = System.getProperty("line.separator");

            StringBuffer buffer = new StringBuffer();
            String str;
            BufferedReader br = new BufferedReader(new FileReader(file.getAbsoluteFile()));

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

            br.close();

            if (buffer.indexOf(replacement) == -1)
            {
                String st1 = target;
                int c1 = buffer.indexOf(st1);

                if (c1 != -1)
                {
                    buffer.replace(c1, c1 + st1.length(), replacement);
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
                    bw.write(buffer.toString());
                    bw.close();
                }
                else
                {
                    throw new IOException(target + " String not found");
                }
            }
            else
            {
                logger.finer(replacement + " Exists, no modification performed");
            }
        }
        else
        {
            throw new IOException(file.getAbsolutePath() + " File does not exist");
        }
    }
}
