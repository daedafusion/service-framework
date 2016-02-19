package com.daedafusion.sf;

import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 3/30/15.
 */
public class ServiceFrameworkTestFactory extends ServiceFrameworkFactory
{
    private static final Logger log = Logger.getLogger(ServiceFrameworkTestFactory.class);
    private final ServiceFramework framework;

    public ServiceRegistry getRegistry()
    {
        return framework.getServiceRegistry();
    }

    public void setRegistry(ServiceRegistry registry)
    {
        framework.setServiceRegistry(registry);
    }

    public ServiceFrameworkTestFactory()
    {
        this.framework = new ServiceFramework();
    }

    @Override
    public ServiceFramework getFramework()
    {
        if(!framework.isStarted)
        {
            framework.start();
        }

        return framework;
    }

    @Override
    public ServiceFramework getFramework(String name)
    {
        return framework;
    }

    @Override
    public void destroy()
    {

    }
}
