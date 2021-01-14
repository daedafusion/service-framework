package com.daedafusion.sf.impl;

import com.daedafusion.sf.AbstractService;
import com.daedafusion.sf.BackgroundService;
import com.daedafusion.sf.LifecycleListener;
import com.daedafusion.sf.providers.BackgroundServiceProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by mphilpot on 9/29/16.
 */
public class BackgroundServiceImpl extends AbstractService<BackgroundServiceProvider> implements BackgroundService
{
    private static final Logger log = LogManager.getLogger(BackgroundServiceImpl.class);

    public BackgroundServiceImpl()
    {
        addLifecycleListener(new LifecycleListener()
        {
            @Override
            public void start()
            {
                getProviders().forEach(p -> {
                    log.info(String.format("Starting background service %s", p.getName()));
                });
            }
        });
    }

    @Override
    public Class getProviderInterface()
    {
        return BackgroundServiceProvider.class;
    }
}
