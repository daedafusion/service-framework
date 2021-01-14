package com.daedafusion.sf.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Created by mphilpot on 7/2/14.
 */
public class ManagedObjectDescription
{
    private static final Logger log = LogManager.getLogger(ManagedObjectDescription.class);

    // Not specified in configuration!  Auto generated
    private String uuid;

    private String              infClass;
    private String              implClass;
    private Map<String, String> properties;
    private String              loaderUri;
    private List<String>        dependentServices;

    // Not specified in configuration!  Auto populated
    private List<String>        providers;

    public ManagedObjectDescription()
    {
        uuid = UUID.randomUUID().toString();
        properties = new HashMap<>();
        dependentServices = new ArrayList<>();
        providers = new ArrayList<>();
    }

    public String getUuid()
    {
        return uuid;
    }

    public String getInfClass()
    {
        return infClass;
    }

    public void setInfClass(String infClass)
    {
        this.infClass = infClass;
    }

    public String getImplClass()
    {
        if(implClass != null)
        {
            return implClass;
        }
        else
        {
            // Return default by convention
            int lastDot = getInfClass().lastIndexOf(".");
            return String.format("%s.impl%sImpl", getInfClass().substring(0, lastDot), getInfClass().substring(lastDot));
        }
    }

    public void setImplClass(String implClass)
    {
        this.implClass = implClass;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, String> properties)
    {
        this.properties = properties;
    }

    public String getLoaderUri()
    {
        return loaderUri;
    }

    public void setLoaderUri(String loaderUri)
    {
        this.loaderUri = loaderUri;
    }

    public List<String> getDependentServices()
    {
        return dependentServices;
    }

    public void setDependentServices(List<String> dependentServices)
    {
        this.dependentServices = dependentServices;
    }

    public List<String> getProviders()
    {
        return providers;
    }

    public void setProviders(List<String> providers)
    {
        this.providers = providers;
    }
}
