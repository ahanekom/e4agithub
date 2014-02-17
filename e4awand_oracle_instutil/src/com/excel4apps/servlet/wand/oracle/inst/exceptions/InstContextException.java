package com.excel4apps.servlet.wand.oracle.inst.exceptions;

/**
 * Raised when and error occurs during the initialisation of the Installation
 * Context object.
 * 
 * @author Andries Hanekom
 * 
 */
public class InstContextException extends Exception
{

    String cause;

    /** Default constructor - initializes instance variable to unknown */
    public InstContextException()
    {
        super(); // call superclass constructor
        cause = "unknown";
    }

    /** Constructor receives some kind of message that is saved in an instance */
    public InstContextException(String err)
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
