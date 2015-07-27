package com.daedafusion.sf.loader;

import com.daedafusion.sf.ServiceFrameworkException;
import com.daedafusion.sf.config.LoaderDescription;

/**
 * Created by mphilpot on 7/2/14.
 */
public interface Loader
{
    void setLoaderDescription(LoaderDescription description);

    void init() throws ServiceFrameworkException;

    Object load(String name) throws ServiceFrameworkException;
}
