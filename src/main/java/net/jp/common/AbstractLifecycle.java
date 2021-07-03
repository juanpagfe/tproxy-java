package net.jp.common;

public abstract class AbstractLifecycle implements Lifecycle
{
    protected State currentState = State.UNINITIALIZED;

    public void init() throws Exception
    {
        currentState = State.INITIALIZED;
    }

    public void start()
    {
        currentState = State.STARTED;
    }

    public void stop()
    {
        currentState = State.STOPPED;
    }

    public void destroy()
    {
        currentState = State.DESTROYED;
    }

    public final State getState()
    {
        return currentState;
    }
}
