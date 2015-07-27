package com.daedafusion.sf;

import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 8/20/14.
 */
public abstract class ServiceFrameworkFactory
{
    private static final Logger log = Logger.getLogger(ServiceFrameworkFactory.class);

    private static ServiceFrameworkFactory ourInstance;

    static
    {
        String frameworkFactoryClass = System.getProperty("serviceFrameworkFactoryImpl", "com.df.argos.commons.sf.ServiceFrameworkFactoryYaml");

        try
        {
            ourInstance = (ServiceFrameworkFactory) Class.forName(frameworkFactoryClass, true, ServiceFramework.class.getClassLoader()).newInstance();
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException("Unable to bootstrap ServiceFrameworkFactory", e);
        }
    }

    public static ServiceFrameworkFactory getInstance()
    {
        return ourInstance;
    }

    protected ServiceFrameworkFactory()
    {
    }

    public abstract ServiceFramework getFramework();
    public abstract ServiceFramework getFramework(String name);
    public abstract void destroy();
}
