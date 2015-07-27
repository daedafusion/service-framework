package com.daedafusion.sf;

import com.daedafusion.sf.config.ServiceConfiguration;

import java.util.List;
import java.util.Map;

/**
 * Created by mphilpot on 7/2/14.
 */
public interface ServiceRegistry
{
    ServiceConfiguration getServiceConfiguration();
    void setServiceConfiguration(ServiceConfiguration config);

    <T> T getService(Class<T> inf);

    List<ManagedObject> getManagedObjects();

    Map<String, Object> getExternalResources();
    void addExternalResource(String key, Object object);

    void construct() throws ServiceFrameworkException;
    boolean isInitialized();

    void register(ManagedObject mo);
    void deregister(ManagedObject mo);

    void addServiceRegistryListener(ServiceRegistryListener listener);
}
