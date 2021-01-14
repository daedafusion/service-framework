package com.daedafusion.sf.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mphilpot on 7/2/14.
 */
public class ServiceConfiguration
{
    private static final Logger log = LogManager.getLogger(ServiceConfiguration.class);

    private List<ManagedObjectDescription> managedObjectDescriptions;
    private List<LoaderDescription>        loaderDescriptions;

    private Map<String, ManagedObjectDescription> descriptionMap;
    private Map<String, LoaderDescription>        loaderMap;

    public ServiceConfiguration()
    {
        managedObjectDescriptions = new ArrayList<>();
        loaderDescriptions = new ArrayList<>();
    }

    public void compile()
    {
        // Build maps, check for cyclic dependencies
        descriptionMap = new HashMap<>();
        loaderMap = new HashMap<>();

        // TODO check fo cycles
    }

    public LoaderDescription getManagedObjectLoader(String uri)
    {
        return loaderMap.get(uri);
    }

    public ManagedObjectDescription getManagedObjectDescription(String uri)
    {
        return descriptionMap.get(uri);
    }

    public List<ManagedObjectDescription> getManagedObjectDescriptions()
    {
        return managedObjectDescriptions;
    }

    public void setManagedObjectDescriptions(List<ManagedObjectDescription> managedObjectDescriptions)
    {
        this.managedObjectDescriptions = managedObjectDescriptions;
    }

    public List<LoaderDescription> getLoaderDescriptions()
    {
        return loaderDescriptions;
    }

    public void setLoaderDescriptions(List<LoaderDescription> loaderDescriptions)
    {
        this.loaderDescriptions = loaderDescriptions;
    }
}
