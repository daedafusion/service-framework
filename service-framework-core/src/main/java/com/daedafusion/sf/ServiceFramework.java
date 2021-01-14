package com.daedafusion.sf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by mphilpot on 7/2/14.
 */
public final class ServiceFramework
{
    private static final Logger log = LogManager.getLogger(ServiceFramework.class);

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

        serviceRegistry.getManagedObjects().forEach(LifecycleListener::start);

        isStarted = true;

        serviceRegistry.getManagedObjects().forEach(LifecycleListener::postStart);
    }

    public void stop()
    {
        serviceRegistry.getManagedObjects().forEach(LifecycleListener::stop);

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

        serviceRegistry.getManagedObjects().forEach(LifecycleListener::teardown);
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
