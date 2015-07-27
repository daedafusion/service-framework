package com.daedafusion.sf.loader;

import com.daedafusion.sf.ServiceFramework;
import com.daedafusion.sf.ServiceFrameworkException;
import com.daedafusion.sf.config.LoaderDescription;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mphilpot on 7/3/14.
 */
public class LoaderFactory
{
    private static LoaderFactory ourInstance = new LoaderFactory();

    public static LoaderFactory getInstance()
    {
        return ourInstance;
    }

    private Map<String, Loader> loaders;

    private LoaderFactory()
    {
        loaders = new HashMap<>();
    }

    public synchronized Loader getLoader(String uri) throws ServiceFrameworkException
    {
        if(loaders.containsKey(uri))
        {
            return loaders.get(uri);
        }

        throw new ServiceFrameworkException(String.format("Loader %s not found", uri));
    }

    public synchronized Loader buildLoader(LoaderDescription description) throws ServiceFrameworkException
    {
        if(loaders.containsKey(description.getUri()))
        {
            return loaders.get(description.getUri());
        }

        try
        {
            Loader loader = (Loader) Class.forName(description.getLoaderClass(), true, ServiceFramework.class.getClassLoader()).newInstance();
            loader.setLoaderDescription(description);

            loader.init();

            loaders.put(description.getUri(), loader);

            return loader;
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
        {
            throw new ServiceFrameworkException("Error building loader", e);
        }
    }
}
