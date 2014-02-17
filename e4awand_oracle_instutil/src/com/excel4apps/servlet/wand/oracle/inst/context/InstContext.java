package com.excel4apps.servlet.wand.oracle.inst.context;

import com.excel4apps.servlet.wand.oracle.inst.context.xml.OAContextParser.OAContext;

/**
 * Installation Context POJO; the installation Context holds information used
 * during the installation.
 * 
 * @author Andries Hanekom
 * 
 */
public class InstContext
{
    private String logFileName;
    private String appsMayorVersion;
    private OAContext oac;
    private String appsusername;
    private String contextFile;

    private char[] appspassword = null;

    public String getAppsMayorVersion()
    {
        return appsMayorVersion;
    }

    public char[] getAppspassword()
    {
        return appspassword;
    }

    public String getAppsusername()
    {
        return appsusername;
    }

    public String getContextFile()
    {
        return contextFile;
    }

    public String getLogFileName()
    {
        return logFileName;
    }

    public OAContext getOac()
    {
        return oac;
    }

    public void setAppsMayorVersion(String appsMayorVersion)
    {
        this.appsMayorVersion = appsMayorVersion;
    }

    public void setAppspassword(char[] appspassword)
    {
        this.appspassword = appspassword;
    }

    public void setAppsusername(String appsusername)
    {
        this.appsusername = appsusername;
    }

    public void setContextFile(String contextFile)
    {
        this.contextFile = contextFile;
    }

    public void setLogFileName(String logFileName)
    {
        this.logFileName = logFileName;
    }

    public void setOac(OAContext oac)
    {
        this.oac = oac;
    }
}
