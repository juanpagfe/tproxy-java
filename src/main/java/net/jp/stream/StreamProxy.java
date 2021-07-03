package net.jp.stream;

import net.jp.proxy.Proxy;
import net.jp.proxy.TProxyOptions;
import net.jp.nio.NioSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class StreamProxy extends Proxy
{
    private static final Logger log = LoggerFactory.getLogger( StreamProxy.class.getName() );
    private ServerSocketChannel serverSocketChannel;

    public StreamProxy( NioSelector selector, TProxyOptions options )
    {
        super( selector, options );
    }

    @Override
    public void init() throws Exception
    {
        super.init();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking( false );
        serverSocketChannel.bind( options.getBindAddress() );
        log.info( "Listening on {}", serverSocketChannel.getLocalAddress() );
    }

    @Override
    public void start()
    {
        super.start();
        selector.register( serverSocketChannel, SelectionKey.OP_ACCEPT, this::accept );
    }

    private void accept( SelectionKey selectionKey )
    {
        try
        {
            SocketChannel channel = serverSocketChannel.accept();
            configureSocket( channel );
            log.info( "Connection accepted: {}", channel.getLocalAddress() );
            SocketChannel channel1 = SocketChannel.open();
            configureSocket( channel1 );
            channel1.connect( options.getAddress() );
            selector.register( channel1, SelectionKey.OP_CONNECT, selectionKey1 -> {
                if( channel1.finishConnect() )
                {
                    createPair( channel, channel1 );
                }
                else
                {
                    log.error( "Connection with {} could not be finished", options.getAddress() );
                }
            } );
        }
        catch( IOException e )
        {
            log.error( e.getMessage(), e );
        }
    }

    private void createPair( SocketChannel inner, SocketChannel outer )
    {
        try
        {
            StreamPair pair = new StreamPair( selector, inner, outer, options );
            pair.init();
            pair.start();
        }
        catch( Exception e )
        {
            log.error( e.getMessage(), e );
        }
    }

    private void configureSocket( SocketChannel channel ) throws IOException
    {
        channel.configureBlocking( false );
        channel.setOption( StandardSocketOptions.SO_KEEPALIVE, true );
        channel.setOption( StandardSocketOptions.TCP_NODELAY, true );
        channel.setOption( StandardSocketOptions.SO_LINGER, -1 );
        channel.setOption( StandardSocketOptions.SO_RCVBUF, options.getBufferSize() );
        channel.setOption( StandardSocketOptions.SO_SNDBUF, options.getBufferSize() );
    }
}
