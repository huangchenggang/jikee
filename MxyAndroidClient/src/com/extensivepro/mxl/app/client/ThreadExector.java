package com.extensivepro.mxl.app.client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.extensivepro.mxl.util.Logger;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 20, 2013 10:34:10 AM
 * @version V1.3.1
 */
public class ThreadExector
{
    private static final String TAG = ThreadExector.class.getSimpleName();

    private static ThreadPoolExecutor mExecutor = new ThreadPoolExecutor(1, 1,
            0, TimeUnit.SECONDS, new ArrayBlockingQueue(3),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    public static void execute(Command command, ClientThread thread)
    {
        Logger.d(TAG, "execute()");
        mExecutor.execute(thread);
    }
    
    public static void execute(Runnable runnable)
    {
        Logger.d(TAG, "execute()");
        mExecutor.execute(runnable);
    }

}
