package com.daedafusion.sf;

import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 7/2/14.
 */
public final class ServiceFramework
{
    private static final Logger log = Logger.getLogger(ServiceFramework.class);

    private ServiceRegistry serviceRegistry;

    boolean isStarted;

    protected ServiceFramework()
    {
        isStarted = false;
    }

    public void start()
    {
        serviceRegistry.construct();

        if(!serviceRegistry.isInitialized())
        {
            throw new ServiceFrameworkException("Framework did not initialize");
        }

        for(ManagedObject mo : serviceRegistry.getManagedObjects())
        {
            mo.start();
        }

        isStarted = true;
    }

    public void stop()
    {
        for(ManagedObject mo : serviceRegistry.getManagedObjects())
        {
            mo.stop();
        }

        isStarted = false;
    }

    /**
     * Package protection
     */
    protected void teardown()
    {
        if(isStarted)
        {
            stop();
        }

        for(ManagedObject mo : serviceRegistry.getManagedObjects())
        {
            mo.teardown();
        }
    }

    public <T> T getService(Class<T> inf)
    {
        if(!isStarted)
        {
            throw new ServiceFrameworkException("Service Framework is stopped");
        }

        return serviceRegistry.getService(inf);
    }

    public ServiceRegistry getServiceRegistry()
    {
        return serviceRegistry;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }
}
