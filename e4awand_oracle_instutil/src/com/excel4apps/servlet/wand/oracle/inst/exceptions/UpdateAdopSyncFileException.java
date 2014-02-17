package com.excel4apps.servlet.wand.oracle.inst.exceptions;

/**
 * Raised when an error occurs during the update of the ADOP Custom Sync File
 * file.
 * 
 * @author Andries Hanekom
 * 
 */
public class UpdateAdopSyncFileException extends Exception
{

    String cause;

    /** Default constructor - initializes instance variable to unknown */
    public UpdateAdopSyncFileException()
    {
        super(); // call superclass constructor
        cause = "unknown";
    }

    /** Constructor receives some kind of message that is saved in an instance */
    public UpdateAdopSyncFileException(String err)
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
