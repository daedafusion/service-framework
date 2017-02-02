package com.daedafusion.sf;

/**
 * Created by mphilpot on 7/2/14.
 */
public interface LifecycleListener
{
    default void init(){}
    default void start(){}
    default void postStart(){}
    default void stop(){}
    default void teardown(){}
}
