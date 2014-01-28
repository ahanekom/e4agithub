package com.excel4apps.servlet.wand.oracle.inst.exceptions;

/**
 * Raised when an error occurs with deployment of Wands file artifacts.
 * 
 * @author Andries Hanekom
 * 
 */
public class FileDeployException extends Exception
{

    String cause;

    // ----------------------------------------------
    // Default constructor - initializes instance variable to unknown
    public FileDeployException()
    {
        super(); // call superclass constructor
        cause = "unknown";
    }

    // -----------------------------------------------
    // Constructor receives some kind of message that is saved in an instance
    // variable.
    public FileDeployException(String err)
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
