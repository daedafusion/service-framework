package com.daedafusion.sf;

/**
 * Created by mphilpot on 7/2/14.
 */
public interface LifecycleListener
{
    void init();
    void start();
    void stop();
    void teardown();
}
