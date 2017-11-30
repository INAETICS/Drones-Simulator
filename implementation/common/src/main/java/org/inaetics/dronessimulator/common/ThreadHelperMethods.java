package org.inaetics.dronessimulator.common;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Set;

public class ThreadHelperMethods {
    public static ThreadInfo[] getAllThreads(int a) {
        ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
        long[] ids = mxBean.getAllThreadIds();
        return mxBean.getThreadInfo(ids);
    }

    public static ThreadGroup getAllThreads(boolean b) {
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup parentGroup;
        while ((parentGroup = rootGroup.getParent()) != null) {
            rootGroup = parentGroup;
        }
        return rootGroup;
    }

    public static Set<Thread> getAllThreads() {
        return Thread.getAllStackTraces().keySet();
    }
}
