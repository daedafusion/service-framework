package com.daedafusion.sf.impl;

import com.daedafusion.sf.AbstractService;
import com.daedafusion.sf.Base64Encoder;
import com.daedafusion.sf.providers.Base64EncoderProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by mphilpot on 7/2/14.
 */
public class Base64EncoderServiceImpl extends AbstractService<Base64EncoderProvider> implements Base64Encoder
{
    private static final Logger log = LogManager.getLogger(Base64EncoderServiceImpl.class);


    @Override
    public String encode(byte[] bytes)
    {
        return getSingleProvider().encode(bytes);
    }

    @Override
    public byte[] decode(String s)
    {
        return getSingleProvider().decode(s);
    }

    @Override
    public Class getProviderInterface()
    {
        return Base64EncoderProvider.class;
    }
}
