package com.daedafusion.sf.providers.impl;

import com.daedafusion.sf.AbstractProvider;
import com.daedafusion.sf.LifecycleListener;
import com.daedafusion.sf.providers.Base64EncoderProvider;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by mphilpot on 7/2/14.
 */
public class ApacheCommonsCodecProvider extends AbstractProvider implements Base64EncoderProvider
{
    private static final Logger log = LogManager.getLogger(ApacheCommonsCodecProvider.class);

    private String someProperty;
    private String anotherProperty;

    public ApacheCommonsCodecProvider()
    {
        addLifecycleListener(new LifecycleListener()
        {
            @Override
            public void init()
            {
                log.info("provider init");
                someProperty = getProperty("someKey", "someDefaultValue");
                anotherProperty = getProperty("someKey");
            }

            @Override
            public void start()
            {
                log.info("provider start");
            }

            @Override
            public void postStart()
            {
                log.info("provider postStart");
            }

            @Override
            public void stop()
            {
                log.info("provider stop");
            }

            @Override
            public void teardown()
            {
                log.info("provider teardown");
            }
        });
    }

    @Override
    public String encode(byte[] bytes)
    {
        return Base64.encodeBase64String(bytes);
    }

    @Override
    public byte[] decode(String s)
    {
        return Base64.decodeBase64(s);
    }
}
