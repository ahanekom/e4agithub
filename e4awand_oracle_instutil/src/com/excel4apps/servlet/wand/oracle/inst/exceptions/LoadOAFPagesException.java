package com.excel4apps.servlet.wand.oracle.inst.exceptions;

/**
 * Raised when an exception occurs during the loading of Wands OAF pages.
 * 
 * @author Andries Hanekom
 * 
 */
public class LoadOAFPagesException extends Exception
{

    String cause;

    /** Default constructor - initializes instance variable to unknown */
    public LoadOAFPagesException()
    {
        super(); // call superclass constructor
        cause = "unknown";
    }

    /** Constructor receives some kind of message that is saved in an instance */
    public LoadOAFPagesException(String err)
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
