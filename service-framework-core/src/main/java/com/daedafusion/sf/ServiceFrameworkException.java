package com.daedafusion.sf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by mphilpot on 7/2/14.
 */
public class ServiceFrameworkException extends RuntimeException
{
    private static final Logger log = LogManager.getLogger(ServiceFrameworkException.class);

    public ServiceFrameworkException(String message)
    {
        super(message);
    }

    public ServiceFrameworkException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ServiceFrameworkException(Throwable cause)
    {
        super(cause);
    }
}
