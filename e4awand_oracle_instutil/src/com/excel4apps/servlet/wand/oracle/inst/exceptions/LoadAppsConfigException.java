package com.excel4apps.servlet.wand.oracle.inst.exceptions;

/**
 * Raised when the environments Application Configuration Context file could not
 * be loaded.
 * 
 * @author Andries Hanekom
 * 
 */
public class LoadAppsConfigException extends Exception
{

    String cause;

    /** Default constructor - initializes instance variable to unknown */
    public LoadAppsConfigException()
    {
        super(); // call superclass constructor
        cause = "unknown";
    }

    /** Constructor receives some kind of message that is saved in an instance */
    public LoadAppsConfigException(String err)
    {
        super(err); // call super class constructor
        cause = err; // save message
    }

    /** public method, callable by exception catcher. It returns the error */
    public String getError()
    {
        return cause;
    }

}
