package com.daedafusion.sf;

import java.util.List;

/**
 * Created by mphilpot on 7/2/14.
 */
public interface Service extends ManagedObject
{
    /**
     * This is required to set providers from a service from the Service Registry implementation
     * @param mos
     */
    void addProviders(List<ManagedObject> mos);

    Class getProviderInterface();
}
