package com.daedafusion.sf;

/**
 * Created by mphilpot on 7/2/14.
 */
public interface ServiceRegistryListener
{
    void registered(String uuid);
    void deregistered(String uuid);
}
