package io.github.finefuture.dynamic.bean;

/**
 * @author longqiang
 * @version 1.0
 */
public class DependentInfo {

    private String beanName;

    private volatile boolean refreshed = false;

    private volatile int count;

    private int originalCount = 0;

    DependentInfo(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }

    boolean getRefreshed() {
        return refreshed;
    }

    void setRefreshed(boolean refreshed) {
        this.refreshed = refreshed;
    }

    public int getCount() {
        return count;
    }

    public int getOriginalCount() {
        return originalCount;
    }

    private void resetCount() {
        count = originalCount;
    }

    void decrementCount() {
        if (--count == 0) {
            setRefreshed(false);
            resetCount();
        }
    }

    void registerCount() {
        originalCount++;
        count++;
    }

    @Override
    public String toString() {
        return "DependentInfo{" +
                "beanName='" + beanName + '\'' +
                ", refreshed=" + refreshed +
                ", count=" + count +
                ", originalCount=" + originalCount +
                '}';
    }
}
