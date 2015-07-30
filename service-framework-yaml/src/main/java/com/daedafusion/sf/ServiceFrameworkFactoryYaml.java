package com.daedafusion.sf;

import com.daedafusion.sf.ServiceFramework;
import com.daedafusion.sf.ServiceFrameworkFactory;
import com.daedafusion.sf.config.ServiceConfiguration;
import com.daedafusion.sf.impl.DefaultServiceRegistry;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mphilpot on 7/2/14.
 */
public class ServiceFrameworkFactoryYaml extends ServiceFrameworkFactory
{
    private Map<String, ServiceFramework> frameworks;
    private String defaultFramework = "defaultFramework";
    private String defaultFrameworkResource = "framework.yaml";

    protected ServiceFrameworkFactoryYaml()
    {
        frameworks = new HashMap<>();
    }

    @Override
    public synchronized ServiceFramework getFramework()
    {
        return getFramework(defaultFramework);
    }

    @Override
    public synchronized ServiceFramework getFramework(String name)
    {
        if(!frameworks.containsKey(name))
        {
            // Attempt default config
            setConfigResource(ServiceFrameworkFactoryYaml.class.getClassLoader().getResourceAsStream(defaultFrameworkResource));
        }

        return frameworks.get(name);
    }

    @Override
    public synchronized void destroy()
    {
        for (ServiceFramework sf : frameworks.values())
        {
            sf.stop();
            sf.teardown();
        }

        frameworks.clear();
    }

    public void setConfigResource(InputStream resource)
    {
        setConfigResource(defaultFramework, resource);
    }

    public void setConfigResource(String name, InputStream resource)
    {
        Yaml yaml = new Yaml(new Constructor(ServiceConfiguration.class));

        ServiceConfiguration config = (ServiceConfiguration) yaml.load(resource);
        config.compile();

        ServiceFramework sf = new ServiceFramework();
        sf.setServiceRegistry(new DefaultServiceRegistry(config));

        frameworks.put(name, sf);
    }
}
