package net.jp.datagram;

import net.jp.nio.NioSelector;
import net.jp.proxy.Proxy;
import net.jp.proxy.TProxyOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;

import static java.nio.channels.SelectionKey.OP_READ;

public class DatagramProxy extends Proxy
{
    private static final Logger log = LoggerFactory.getLogger( DatagramProxy.class.getName() );
    private DatagramChannel serverChannel;
    private final ByteBuffer innerBuffer;
    private final ByteBuffer outerBuffer;
    private Map<SocketAddress, DatagramChannel> outers;

    public DatagramProxy( NioSelector selector, TProxyOptions options )
    {
        super( selector, options );
        innerBuffer = options.isDirectBuffer() ? ByteBuffer.allocateDirect( options.getBufferSize() )
            : ByteBuffer.allocate( options.getBufferSize() );
        outerBuffer = options.isDirectBuffer() ? ByteBuffer.allocateDirect( options.getBufferSize() )
            : ByteBuffer.allocate( options.getBufferSize() );
        outers = new HashMap<>();
    }

    @Override
    public void init() throws Exception
    {
        super.init();
        serverChannel = DatagramChannel.open();
        configureChannel( serverChannel );
        serverChannel.bind( options.getBindAddress() );
        log.info( "Listening on {}", serverChannel.getLocalAddress() );
    }

    @Override
    public void start()
    {
        super.start();
        selector.register( serverChannel, OP_READ, this::readFromServer );
    }

    private void readFromServer( SelectionKey selectionKey )
    {
        try
        {
            SocketAddress address = serverChannel.receive( innerBuffer );
            innerBuffer.flip();
            final int read = innerBuffer.remaining();
            log.info( "Read from inner {} - {} bytes", address, read );
            if( read > 0 )
            {
                DatagramChannel channel = requestOuter( address );
                int sent = channel.send( innerBuffer, options.getAddress() );
                log.info( "Sent to outer {} - {} bytes", options.getAddress(), sent );
            }

        }
        catch( IOException e )
        {
            log.error( e.getMessage(), e );
        }
        finally
        {
            innerBuffer.clear();
        }
    }

    private DatagramChannel requestOuter( SocketAddress address ) throws IOException
    {
        DatagramChannel channel = this.outers.get( address );
        if( channel == null )
        {
            channel = DatagramChannel.open();
            configureChannel( channel );
            selector.register( channel, OP_READ, this::readFromOuter );
            this.outers.put( address, channel );
        }
        return channel;
    }

    private void readFromOuter( SelectionKey selectionKey )
    {
        try
        {
            DatagramChannel channel = (DatagramChannel)selectionKey.channel();
            SocketAddress address = channel.receive( outerBuffer );
            outerBuffer.flip();
            final int read = outerBuffer.remaining();
            log.info( "Read from outer {} - {} bytes", address, read );
            if( read > 0 )
            {
                SocketAddress innerAddress = requestInner( channel );
                if( innerAddress != null )
                {
                    int sent = serverChannel.send( outerBuffer, innerAddress );
                    log.info( "Sent to inner {} - {} bytes", innerAddress, sent );
                }
                else
                {
                    log.error( "Inner address was not found for channel {}", channel.getLocalAddress() );
                }
            }

        }
        catch( IOException e )
        {
            log.error( e.getMessage(), e );
        }
        finally
        {
            innerBuffer.clear();
        }
    }

    private SocketAddress requestInner( DatagramChannel channel )
    {
        for( SocketAddress address : outers.keySet() )
        {
            if( outers.get( address ).equals( channel ) )
                return address;
        }
        return null;
    }

    private void configureChannel( DatagramChannel channel ) throws IOException
    {
        channel.configureBlocking( false );
        channel.setOption( StandardSocketOptions.SO_RCVBUF, options.getBufferSize() );
        channel.setOption( StandardSocketOptions.SO_SNDBUF, options.getBufferSize() );
    }
}
