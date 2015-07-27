package com.daedafusion.sf;

import com.daedafusion.sf.config.ManagedObjectDescription;
import com.daedafusion.sf.config.ServiceConfiguration;
import com.daedafusion.sf.impl.DefaultServiceRegistry;
import com.daedafusion.sf.Base64Encoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by mphilpot on 7/2/14.
 */
public class BasicTest
{
    private static final Logger log = Logger.getLogger(BasicTest.class);

    @Test
    public void main() throws ServiceFrameworkException
    {
        // Construct manually
        ServiceFramework sf = new ServiceFramework();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.setServiceConfiguration(buildConfig());
        sf.setServiceRegistry(registry);

        sf.start(); // Theoretically done by ServletContextListener in a service

        Base64Encoder encoder = sf.getService(Base64Encoder.class);

        assertThat(encoder, is(notNullValue()));

        String test = "This is a test of the emergency broadcast system";

        String encoded = encoder.encode(test.getBytes());

        assertThat(test, is(not(encoded)));

        String base = Base64.encodeBase64String(test.getBytes());

        assertThat(encoded, is(base));

        sf.stop();

        try
        {
            Base64Encoder tmp = sf.getService(Base64Encoder.class);
            fail("Exception should have been thrown");
        }
        catch(ServiceFrameworkException e)
        {
            // Pass
        }

        sf.teardown(); // Factory needs to be notified of this...

    }

    private ServiceConfiguration buildConfig()
    {
        ServiceConfiguration config = new ServiceConfiguration();

        ManagedObjectDescription sd = new ManagedObjectDescription();
        sd.setImplClass("com.daedafusion.sf.impl.Base64EncoderServiceImpl");
        sd.setInfClass("com.daedafusion.sf.Base64Encoder");

        config.getManagedObjectDescriptions().add(sd);

        ManagedObjectDescription pd = new ManagedObjectDescription();
        pd.setImplClass("com.daedafusion.sf.providers.impl.ApacheCommonsCodecProvider");
        pd.setInfClass("com.daedafusion.sf.providers.Base64EncoderProvider");

        config.getManagedObjectDescriptions().add(pd);

        config.compile();

        return config;
    }
}
