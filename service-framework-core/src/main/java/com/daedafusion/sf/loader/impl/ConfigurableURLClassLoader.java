package com.daedafusion.sf.loader.impl;

import com.daedafusion.sf.config.LoaderDescription;
import org.apache.log4j.Logger;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by mphilpot on 4/1/15.
 */
public class ConfigurableURLClassLoader extends URLClassLoader
{
    private static final Logger log = Logger.getLogger(ConfigurableURLClassLoader.class);
    private final LoaderDescription description;

    public ConfigurableURLClassLoader(URL[] urls, ClassLoader parent, LoaderDescription description)
    {
        super(urls, parent);
        this.description = description;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
    {
        if(description.getProperties().containsKey("mask") && name.startsWith(description.getProperties().get("mask")))
        {
            Class c = findClass(name);

            if(c != null)
                return c;
        }

        return super.loadClass(name, resolve);
    }
}
