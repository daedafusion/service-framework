package com.daedafusion.sf;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mphilpot on 7/2/14.
 */
public abstract class AbstractService<T extends Provider> extends AbstractManagedObject implements Service
{
    private static final Logger log = Logger.getLogger(AbstractService.class);

    private List<T> providers;

    public AbstractService()
    {
        providers = new ArrayList<>();
    }

    public List<T> getProviders()
    {
        return providers;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addProviders(List<ManagedObject> mos)
    {
        for(ManagedObject mo : mos)
        {
            providers.add((T) mo);
        }
    }

    protected T getSingleProvider()
    {
        if(providers.isEmpty())
        {
            throw new RuntimeException("No providers found for service requiring one mandatory provider"); // TODO FIXME
        }

        if(providers.size() > 1)
        {
            log.warn("Multiple providers configured but only one asked for");
        }

        return providers.get(0);
    }
}
