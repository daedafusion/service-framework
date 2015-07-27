package com.daedafusion.sf.impl;

import com.daedafusion.sf.*;
import com.daedafusion.sf.config.LoaderDescription;
import com.daedafusion.sf.config.ManagedObjectDescription;
import com.daedafusion.sf.config.ServiceConfiguration;
import com.daedafusion.sf.loader.Loader;
import com.daedafusion.sf.loader.LoaderFactory;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mphilpot on 7/2/14.
 */
public class DefaultServiceRegistry implements ServiceRegistry
{
    private static final Logger log = Logger.getLogger(DefaultServiceRegistry.class);

    private ServiceConfiguration serviceConfiguration;

    private Map<String, List<ManagedObject>> registeredManagedObjects;

    private List<ManagedObject> managedObjects;

    private Map<String, Object> externalResources;

    private List<ServiceRegistryListener> listeners;

    public DefaultServiceRegistry()
    {
        listeners = new ArrayList<>();
        registeredManagedObjects = new HashMap<>();
        managedObjects = new ArrayList<>();
        externalResources = new ConcurrentHashMap<>();
    }

    public DefaultServiceRegistry(ServiceConfiguration config)
    {
        this();
        this.serviceConfiguration = config;
    }

    @Override
    public ServiceConfiguration getServiceConfiguration()
    {
        return serviceConfiguration;
    }

    @Override
    public void setServiceConfiguration(ServiceConfiguration config)
    {
        serviceConfiguration = config;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> inf)
    {
        if(registeredManagedObjects.get(inf.getName()) == null)
            throw new ServiceFrameworkException(String.format("Unknown Service %s", inf.getName()));

        if(registeredManagedObjects.get(inf.getName()).size() > 1)
            throw new ServiceFrameworkException("Illegal Service Request");

        return (T) registeredManagedObjects.get(inf.getName()).iterator().next();
    }

    @Override
    public List<ManagedObject> getManagedObjects()
    {
        return managedObjects;
    }

    @Override
    public Map<String, Object> getExternalResources()
    {
        return externalResources;
    }

    @Override
    public void addExternalResource(String key, Object object)
    {
        externalResources.put(key, object);
    }

    @Override
    public void construct()
    {
        for(LoaderDescription ld : serviceConfiguration.getLoaderDescriptions())
        {
            LoaderFactory.getInstance().buildLoader(ld);
        }

        for (ManagedObjectDescription mod : serviceConfiguration.getManagedObjectDescriptions())
        {
            ManagedObject mo = null;

            if(mod.getLoaderUri() == null)
            {
                try
                {
                    mo = (ManagedObject) Class.forName(mod.getImplClass(), true, ServiceFramework.class.getClassLoader()).newInstance();

                }
                catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
                {
                    throw new RuntimeException(String.format("Failed to classload %s", mod.getImplClass()), e); // TODO
                }
            }
            else
            {
                Loader mol = LoaderFactory.getInstance().getLoader(mod.getLoaderUri());

                mo = (ManagedObject) mol.load(mod.getImplClass());
            }

            mo.setServiceRegistry(this);
            mo.setUuid(mod.getUuid());

            try
            {
                mo.setInterface(mod.getInfClass());
            }
            catch (ClassNotFoundException e)
            {
                log.error("", e);
                throw new ServiceFrameworkException(String.format("Interface %s Not Found", mod.getInfClass()));
            }

            if(mo instanceof Service)
            {
                Service s = (Service) mo;

                List<String> uuids = new ArrayList<>();

                for(ManagedObjectDescription mod2 : serviceConfiguration.getManagedObjectDescriptions())
                {
                    if(mod2.getInfClass().equals(s.getProviderInterface().getName()))
                    {
                        uuids.add(mod2.getUuid());
                    }
                }

                mod.setProviders(uuids);
            }

            // Set Dependent Services, but first need to translate interface classes into uuids
            // TODO this is a bit of a hack... would be better to use a hashmap
            List<String> uuids = new ArrayList<>();
            for(String depServiceClass : mod.getDependentServices())
            {
                for(ManagedObjectDescription mod2 : serviceConfiguration.getManagedObjectDescriptions())
                {
                    if(mod2.getInfClass().equals(depServiceClass))
                    {
                        uuids.add(mod2.getUuid());
                    }
                }
            }

            // Overwrite
            mod.setDependentServices(uuids);

            mo.setDescription(mod);

            mo.init(); // This could be delayed

            managedObjects.add(mo);
        }
    }

    @Override
    public boolean isInitialized()
    {
        boolean isInitialized = true;

        // At this point all the callbacks should have propagated
        for(ManagedObject mo : managedObjects)
        {
            if(!mo.isInitialized())
            {
                log.warn(String.format("Service %s %s (%s) failed to initialize", mo.getUuid(), mo.getInterface(), mo.getDescription().getImplClass()));
                log.warn(String.format("   Providers :: %s", mo.getDescription().getProviders()));
                log.warn(String.format("   Dependent :: %s", mo.getDescription().getDependentServices()));
                isInitialized = false;
            }
        }

        return isInitialized;
    }

    @Override
    public void register(ManagedObject mo)
    {
        log.info(String.format("Service %s registered", mo.getInterface().getName()));

        if(!registeredManagedObjects.containsKey(mo.getInterface().getName()))
        {
            registeredManagedObjects.put(mo.getInterface().getName(), new ArrayList<ManagedObject>());
        }

        registeredManagedObjects.get(mo.getInterface().getName()).add(mo);

        // FIXME There has got to be a better way than this
        if(mo instanceof Service)
        {
            Service as = (Service) mo;

            List<ManagedObject> list = registeredManagedObjects.get(as.getProviderInterface().getName());

            // Some services might have no providers (no-op services)
            if(list != null && !list.isEmpty())
            {
                as.addProviders(list);
            }
        }

        for(ServiceRegistryListener listener : listeners)
        {
            listener.registered(mo.getUuid());
        }
    }

    @Override
    public void deregister(ManagedObject mo)
    {
        log.info(String.format("Service %s deregistered", mo.getInterface().getName()));

        registeredManagedObjects.remove(mo.getInterface().getName());

        for(ServiceRegistryListener listener : listeners)
        {
            listener.deregistered(mo.getUuid());
        }
    }

    @Override
    public void addServiceRegistryListener(ServiceRegistryListener listener)
    {
        listeners.add(listener);
    }
}
