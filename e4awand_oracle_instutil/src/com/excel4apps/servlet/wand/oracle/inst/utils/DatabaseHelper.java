package com.excel4apps.servlet.wand.oracle.inst.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.excel4apps.servlet.wand.oracle.inst.Installer;

public class DatabaseHelper extends Installer
{

    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rset = null;

    protected void close() throws SQLException
    {
        if (rset != null)
        {
            rset.close();
        }
        if (stmt != null)
        {
            stmt.close();
        }
        if (conn != null)
        {
            conn.close();
        }
    }

    /**
     * Performs a database connection test using information from the
     * Installation Context, A simple select from dual is performed.
     * 
     * @param testXXE4AApplication
     * 
     * @throws SQLException
     */
    public void databaseConnectionTest() throws SQLException
    {
        ResultSet rset;

        // Do the SQL "Hello World" Test
        rset = executeQuery("select 'Hello World' from dual");

        while (rset.next())
        {
            logger.finer(rset.getString(1));
        }

        logger.finer("Login Successfull");
    }

    public ResultSet executeQuery(String sql) throws SQLException
    {

        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

        conn = DriverManager.getConnection("jdbc:oracle:thin:@" + ic.getOac().getDBHost() + ":"
                + ic.getOac().getDBPort() + ":" + ic.getOac().getDBSid(), ic.getAppsusername(),
                String.valueOf(ic.getAppspassword()));

        logger.finer("Connected.");

        // Create a statement
        stmt = conn.createStatement();

        // Execute the SQL
        rset = stmt.executeQuery(sql);

        return rset;
    }

    public boolean isXXE4AAppConfigured() throws SQLException
    {
        /* Test for Existence of XXE4A (Excel4apps) application */

        try
        {
            logger.finer("Application XXE4A Test");

            rset = executeQuery("select COUNT(*) from FND_APPLICATION where APPLICATION_SHORT_NAME = 'XXE4A'");

            while (rset.next())
            {
                logger.finer(String.valueOf(rset.getInt(1)));

                if (rset.getInt(1) == 1)
                {
                    logger.finer("Application XXE4A Registered");
                    return true;
                }
                else
                {
                    logger.finer("Application XXE4A Not Registered");
                    return false;
                }
            }
        }

        finally
        {
            close();
        }

        return false;
    }
}
