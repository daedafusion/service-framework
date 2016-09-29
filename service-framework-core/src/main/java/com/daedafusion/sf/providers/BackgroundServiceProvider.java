package com.daedafusion.sf.providers;

import com.daedafusion.sf.BackgroundService;
import com.daedafusion.sf.Provider;

/**
 * Created by mphilpot on 9/29/16.
 */
public interface BackgroundServiceProvider extends Provider, BackgroundService
{
    String getName();
}
