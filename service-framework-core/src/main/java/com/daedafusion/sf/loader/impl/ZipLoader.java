package com.daedafusion.sf.loader.impl;

import com.daedafusion.sf.ServiceFramework;
import com.daedafusion.sf.ServiceFrameworkException;
import com.daedafusion.sf.config.LoaderDescription;
import com.daedafusion.sf.loader.Loader;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Properties
 *
 * cacheDir :: Directory to expand zip files (default ~/.sfcache)
 * pluginName :: Name of plugin, will create a directory within $cacheDir (defaults to uuid)
 */
public class ZipLoader implements Loader
{
    private static final Logger log = Logger.getLogger(ZipLoader.class);

    private LoaderDescription description;
    private URLClassLoader classLoader;

    private String resource;

    // Properties
    private String cacheDir;
    private String pluginName;

    public ZipLoader()
    {
        // Establish defaults
        cacheDir = String.format("%s%s%s", System.getProperty("user.home"), File.separator, ".sfcache");
        pluginName = UUID.randomUUID().toString();
    }


    @Override
    public void setLoaderDescription(LoaderDescription description)
    {
        this.description = description;

        this.resource = description.getResource();

        // Override properties
        if(description.getProperties().containsKey("cacheDir"))
        {
            cacheDir = description.getProperties().get("cacheDir");
        }
        if(description.getProperties().containsKey("pluginName"))
        {
            pluginName = description.getProperties().get("pluginName");
        }
    }

    @Override
    public void init() throws ServiceFrameworkException
    {
        String resourceHash;
        try
        {
            resourceHash = computeResourceHash();
        }
        catch (IOException e)
        {
            throw new ServiceFrameworkException("Error computing resource hash", e);
        }

        // Create cacheDir if needed
        File cacheDirectory = null;
        try
        {
            cacheDirectory = createCacheDirectory();
        }
        catch (IOException e)
        {
            throw new ServiceFrameworkException("Error creating cache directory", e);
        }

        // Create plugin dir if needed
        File pluginDirectory;
        try
        {
            pluginDirectory = createPluginDirectory(cacheDirectory);
        }
        catch (IOException e)
        {
            throw new ServiceFrameworkException("Error creating plugin directory", e);
        }

        if (expandRequired(resourceHash, pluginDirectory))
        {
            try
            {
                expandResource(resourceHash, pluginDirectory);
            }
            catch (IOException e)
            {
                throw new ServiceFrameworkException("Error expanding resource", e);
            }
        }

        URL[] urls;
        try
        {
            urls = computeURLClosure(pluginDirectory);
        }
        catch (MalformedURLException e)
        {
            throw new ServiceFrameworkException("Error computing resource closure", e);
        }

//        classLoader = new ConfigurableURLClassLoader(urls, ServiceFramework.class.getClassLoader(), description);
        classLoader = new URLClassLoader(urls, ServiceFramework.class.getClassLoader());
    }

    private URL[] computeURLClosure(File pluginDirectory) throws MalformedURLException
    {
        List<URL> urls = new ArrayList<>();
        urls.add(pluginDirectory.toURI().toURL());

        Collection<File> jarFiles = FileUtils.listFiles(pluginDirectory, new String[]{"jar"}, true);

        for(File jf : jarFiles)
        {
            urls.add(jf.toURI().toURL());
        }

        return urls.toArray(new URL[0]);
    }

    private void expandResource(String resourceHash, File pluginDirectory) throws IOException
    {
        // Clean out whatever might have been there
        FileUtils.forceDelete(pluginDirectory);
        FileUtils.forceMkdir(pluginDirectory);

        // Write checksum.txt
        FileUtils.writeStringToFile(new File(pluginDirectory, "checksum.txt"), resourceHash);

        // Copy resource to plugin directory
        IOUtils.copy(getResourceStream(), new FileOutputStream(new File(pluginDirectory, "resource.zip")));

        try
        {
            ZipFile zipFile = new ZipFile(new File(pluginDirectory, "resource.zip"));

            zipFile.extractAll(pluginDirectory.getPath());
        }
        catch (ZipException e)
        {
            throw new IOException(e);
        }
    }

    private boolean expandRequired(String resourceHash, File pluginDirectory)
    {
        // See if checksum file exists
        File checksumFile = new File(pluginDirectory, "checksum.txt");

        if(checksumFile.exists())
        {
            try
            {
                String existingHash = FileUtils.readFileToString(checksumFile);

                return !existingHash.equals(resourceHash);
            }
            catch (IOException e)
            {
                log.info("Error reading checksum.txt :: assume expansion required", e);
                return true;
            }
        }
        else
        {
            // Empty directory
            return true;
        }
    }

    private File createPluginDirectory(File cacheDirectory) throws IOException
    {
        File pluginDirectory = new File(String.format("%s%s%s", cacheDir, File.separator, pluginName));

        if(!pluginDirectory.exists())
        {
            FileUtils.forceMkdir(pluginDirectory);
        }

        return pluginDirectory;
    }

    private File createCacheDirectory() throws IOException
    {
        File cacheDirectory = new File(cacheDir);

        if(!cacheDirectory.exists())
        {
            FileUtils.forceMkdir(cacheDirectory);
        }

        return cacheDirectory;
    }

    private String computeResourceHash() throws IOException
    {
        InputStream resourceStream = null;
        try
        {
            resourceStream = getResourceStream();

            return Hex.encodeHexString(DigestUtils.sha1(resourceStream));
        }
        finally
        {
            IOUtils.closeQuietly(resourceStream);
        }
    }

    private InputStream getResourceStream() throws FileNotFoundException
    {
        if(resource.startsWith("classpath:"))
        {
            InputStream in = ServiceFramework.class.getClassLoader().getResourceAsStream(resource.substring("classpath:".length()));

            if(in == null)
            {
                throw new FileNotFoundException("Could not load resource from classpath");
            }

            return in;
        }
        else if(resource.startsWith("file://"))
        {
            return new FileInputStream(new File(resource.substring("file://".length())));
        }
        else
        {
            return new FileInputStream(new File(resource));
        }
    }

    @Override
    public Object load(String name) throws ServiceFrameworkException
    {
        try
        {
            return Class.forName(name, true, classLoader).newInstance();
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
        {
            throw new ServiceFrameworkException("Error instantiating impl class", e);
        }
    }
}
