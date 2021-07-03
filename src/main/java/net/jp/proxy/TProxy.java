package net.jp.proxy;

import net.jp.common.AbstractLifecycle;
import net.jp.datagram.DatagramProxy;
import net.jp.nio.NioSelector;
import net.jp.stream.StreamProxy;

public class TProxy extends AbstractLifecycle
{
    private final TProxyOptions options;
    private NioSelector selector;
    private Proxy proxy;

    public TProxy( TProxyOptions options )
    {
        this.options = options;
    }

    @Override
    public void init() throws Exception
    {
        super.init();
        selector = new NioSelector();
        selector.init();
        proxy = ( options.getProtocol().equals( TProxyOptions.Protocol.TCP ) ) ? new StreamProxy( selector, options )
            : new DatagramProxy( selector, options );
        proxy.init();
    }

    @Override
    public void start()
    {
        super.start();
        selector.start();
        proxy.start();
    }

    @Override
    public void stop()
    {
        super.stop();
        proxy.stop();
        selector.stop();
    }
}
