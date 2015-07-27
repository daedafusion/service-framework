package com.daedafusion.sf.config;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mphilpot on 7/3/14.
 */
public class LoaderDescription
{
    private static final Logger log = Logger.getLogger(LoaderDescription.class);

    private String              uri;
    private String              loaderClass;
    private String              resource;
    private Map<String, String> properties;

    public LoaderDescription()
    {
        properties = new HashMap<>();
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public String getLoaderClass()
    {
        return loaderClass;
    }

    public void setLoaderClass(String loaderClass)
    {
        this.loaderClass = loaderClass;
    }

    public String getResource()
    {
        return resource;
    }

    public void setResource(String resource)
    {
        this.resource = resource;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, String> properties)
    {
        this.properties = properties;
    }
}
