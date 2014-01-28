package com.excel4apps.servlet.wand.oracle.inst.exceptions;

/**
 * Raised when an error occurs during the configuration of the Servlet Config
 * file.
 * 
 * @author Andries Hanekom
 * 
 */
public class ServletConfigException extends Exception
{

    String cause;

    // ----------------------------------------------
    // Default constructor - initializes instance variable to unknown
    public ServletConfigException()
    {
        super(); // call superclass constructor
        cause = "unknown";
    }

    // -----------------------------------------------
    // Constructor receives some kind of message that is saved in an instance
    // variable.
    public ServletConfigException(String err)
    {
        super(err); // call super class constructor
        cause = err; // save message
    }

    // ------------------------------------------------
    // public method, callable by exception catcher. It returns the error
    // message.
    public String getError()
    {
        return cause;
    }

}