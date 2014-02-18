package com.excel4apps.servlet.wand.oracle.inst.utils;

import java.sql.SQLException;
import java.util.logging.Level;

import com.excel4apps.servlet.wand.oracle.inst.Installer;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.InstallValidationException;

/**
 * Installation validation helper class
 * 
 * @author Andries Hanekom
 * 
 */
public class InstallValidation extends Installer
{

    /**
     * Performs database related validation checks
     * 
     * @throws InstallValidationException
     */
    private static void database() throws InstallValidationException
    {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        try
        {
            databaseHelper.databaseConnectionTest();

            if (!databaseHelper.isXXE4AAppConfigured())
            {
                throw new InstallValidationException("Application XXE4A Not Registered");
            }

        }
        catch (SQLException ex)
        {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new InstallValidationException("Unable to connect to database");
        }
    }

    /**
     * Entry method for validation routine.
     * 
     * @param database
     * @throws InstallValidationException
     */
    public static void validate(boolean database) throws InstallValidationException
    {
        if (database)
        {
            database();
        }
    }
}
