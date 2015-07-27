package com.daedafusion.sf;

import com.daedafusion.sf.config.ManagedObjectDescription;

/**
 * Created by mphilpot on 7/2/14.
 */
public interface ManagedObject extends LifecycleListener
{
    String getUuid();
    void setUuid(String uuid);

    void setInterface(String inf) throws ClassNotFoundException;
    Class getInterface();

    void setServiceRegistry(ServiceRegistry registry);

    void setDescription(ManagedObjectDescription description);
    ManagedObjectDescription getDescription();

    boolean isInitialized();
}
