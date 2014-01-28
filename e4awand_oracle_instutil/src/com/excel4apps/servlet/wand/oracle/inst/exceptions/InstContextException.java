package com.excel4apps.servlet.wand.oracle.inst.exceptions;

/**
 * Raised when and error occurs during the initialization of the the
 * Installation Context object.
 * 
 * @author Andries Hanekom
 * 
 */
public class InstContextException extends Exception
{

    String cause;

    // ----------------------------------------------
    // Default constructor - initializes instance variable to unknown
    public InstContextException()
    {
        super(); // call superclass constructor
        cause = "unknown";
    }

    // -----------------------------------------------
    // Constructor receives some kind of message that is saved in an instance
    // variable.
    public InstContextException(String err)
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