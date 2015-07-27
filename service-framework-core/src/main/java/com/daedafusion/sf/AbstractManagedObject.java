package com.daedafusion.sf;

import com.daedafusion.sf.config.ManagedObjectDescription;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by mphilpot on 7/2/14.
 */
public abstract class AbstractManagedObject implements ManagedObject
{
    private static final Logger log = Logger.getLogger(AbstractManagedObject.class);

    private String uuid;

    private Class infClass;
    private List<LifecycleListener>  listeners;
    private ServiceRegistry          serviceRegistry;
    private ManagedObjectDescription description;

    private Set<String> remainingDependents;

    private boolean initialized;

    protected AbstractManagedObject()
    {
        listeners = new ArrayList<>();
        initialized = false;
    }

    protected void addLifecycleListener(LifecycleListener listener)
    {
        listeners.add(listener);
    }

    protected ServiceRegistry getServiceRegistry()
    {
        return serviceRegistry;
    }

    @Override
    public String getUuid()
    {
        return uuid;
    }

    @Override
    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    @Override
    public Class getInterface()
    {
        return infClass;
    }

    @Override
    public void setInterface(String inf) throws ClassNotFoundException
    {
        infClass = Class.forName(inf);
    }

    @Override
    public void setServiceRegistry(ServiceRegistry registry)
    {
        this.serviceRegistry = registry;
    }

    @Override
    public void setDescription(ManagedObjectDescription description)
    {
        this.description = description;
    }

    @Override
    public ManagedObjectDescription getDescription()
    {
        return description;
    }

    @Override
    public boolean isInitialized()
    {
        if(initialized)
        {
            return true;
        }

        if(remainingDependents == null || !remainingDependents.isEmpty())
        {
            return false;
        }

        serviceRegistry.register(this);

        for(LifecycleListener listener :listeners)
        {
            listener.init();
        }

        initialized = true;

        return true;
    }

    @Override
    public void init()
    {
        remainingDependents = new HashSet<>();
        remainingDependents.addAll(description.getDependentServices());

        // Add providers as dependents
        remainingDependents.addAll(description.getProviders());

        Iterator<String> iter = remainingDependents.iterator();

        while(iter.hasNext())
        {
            String dep = iter.next();

            // TODO bad hack -- need hashmap
            boolean depInit = false;

            for(ManagedObject mo : serviceRegistry.getManagedObjects())
            {
                if(mo.getUuid().equals(dep) && mo.isInitialized())
                {
                    depInit = true;
                }
            }

            if(depInit)
            {
                iter.remove();
            }
            else
            {
                serviceRegistry.addServiceRegistryListener(new ServiceRegistryListener()
                {
                    @Override
                    public void registered(String uuid)
                    {
                        if(remainingDependents.contains(uuid))
                        {
                            remainingDependents.remove(uuid);
                            isInitialized();
                        }
                    }

                    @Override
                    public void deregistered(String uuid)
                    {
                        // Empty
                    }
                });
            }
        }

        isInitialized();
    }

    @Override
    public void start()
    {
        for(LifecycleListener listener : listeners)
        {
            listener.start();
        }
    }

    @Override
    public void stop()
    {
        for(LifecycleListener listener : listeners)
        {
            listener.stop();
        }
    }

    @Override
    public void teardown()
    {
        for(LifecycleListener listener : listeners)
        {
            listener.teardown();
        }
    }

    public List<LifecycleListener> getListeners()
    {
        return listeners;
    }

    protected String getProperty(String key)
    {
        return description.getProperties().get(key);
    }

    protected String getProperty(String key, String defaultValue)
    {
        String value = getProperty(key);

        if(value == null)
        {
            return defaultValue;
        }

        return value;
    }
}
