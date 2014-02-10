package com.excel4apps.servlet.wand.oracle.inst.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.excel4apps.servlet.wand.oracle.inst.Installer;

/**
 * Utility class used for ZIP and UNZIP routines.
 * 
 * @author Andries Hanekom
 * 
 */
public class ArchiveManager extends Installer
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

    public static final void unzip(File zip, File extractTo) throws IOException
    {

        logger.finer("zip: " + zip.getAbsolutePath());
        logger.finer("extractTo: " + extractTo.getAbsolutePath());

        ZipFile archive = new ZipFile(zip);
        Enumeration e = archive.entries();

        while (e.hasMoreElements())
        {
            ZipEntry entry = (ZipEntry) e.nextElement();
            File file = new File(extractTo, entry.getName());
            if (entry.isDirectory() && !file.exists())
            {
                file.mkdirs();
            }
            else
            {
                if (!file.getParentFile().exists())
                {
                    file.getParentFile().mkdirs();
                }

                if (!entry.isDirectory())
                {

                    InputStream in = archive.getInputStream(entry);
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));

                    byte[] buffer = new byte[8192];
                    int read;

                    while (-1 != (read = in.read(buffer)))
                    {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    out.close();
                }
            }
        }
        archive.close();
    }

    private static final void zip(File directory, File base, ZipOutputStream zos) throws IOException
    {
        File[] files = directory.listFiles();

        byte[] buffer = new byte[8192];
        int read = 0;

        for (int i = 0, n = files.length; i < n; i++)
        {
            if (files[i].isDirectory())
            {
                zip(files[i], base, zos);
            }
            else
            {
                FileInputStream in = new FileInputStream(files[i]);
                ZipEntry entry = new ZipEntry(files[i].getPath().substring(base.getPath().length() + 1));
                zos.putNextEntry(entry);
                while (-1 != (read = in.read(buffer)))
                {
                    zos.write(buffer, 0, read);
                }
                in.close();
            }
        }
    }

    public static final void zipDirectory(File directory, File zip) throws IOException
    {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));
        zip(directory, directory, zos);
        zos.close();
    }
}
