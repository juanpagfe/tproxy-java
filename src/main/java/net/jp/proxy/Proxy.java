package net.jp.proxy;

import net.jp.common.AbstractLifecycle;
import net.jp.nio.NioSelector;

public abstract class Proxy extends AbstractLifecycle
{
    protected final NioSelector selector;
    protected final TProxyOptions options;

    public Proxy( NioSelector selector, TProxyOptions options )
    {
        this.selector = selector;
        this.options = options;
    }
}
