package com.excel4apps.servlet.wand.oracle.inst.utils;

import java.sql.SQLException;
import java.util.logging.Level;

import com.excel4apps.servlet.wand.oracle.inst.Installer;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.InstallValidationException;

public class InstallValidation extends Installer
{

    private static void database() throws InstallValidationException
    {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        try
        {
            databaseHelper.databaseConnectionTest();

            if (!databaseHelper.isXXE4AAppConfigured())
            {
                throw new InstallValidationException("Unable to connect to database");
            }

        }
        catch (SQLException ex)
        {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new InstallValidationException("Unable to connect to database");
        }
    }

    public static void validate(boolean database) throws InstallValidationException
    {
        if (database)
        {
            database();
        }
    }
}
