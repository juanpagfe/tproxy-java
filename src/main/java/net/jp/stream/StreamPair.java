package net.jp.stream;

import net.jp.proxy.TProxyOptions;
import net.jp.common.AbstractLifecycle;
import net.jp.nio.NioSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static java.nio.channels.SelectionKey.OP_READ;

public class StreamPair extends AbstractLifecycle
{
    private static final Logger log = LoggerFactory.getLogger( StreamPair.class.getName() );
    private final NioSelector selector;
    private final SocketChannel inner;
    private final SocketChannel outer;
    private final ByteBuffer innerBuffer;
    private final ByteBuffer outerBuffer;

    public StreamPair( NioSelector selector, SocketChannel inner, SocketChannel outer, TProxyOptions options )
    {
        this.selector = selector;
        this.inner = inner;
        this.outer = outer;
        this.innerBuffer = options.isDirectBuffer() ? ByteBuffer.allocateDirect( options.getBufferSize() )
            : ByteBuffer.allocate( options.getBufferSize() );
        this.outerBuffer = options.isDirectBuffer() ? ByteBuffer.allocateDirect( options.getBufferSize() )
            : ByteBuffer.allocate( options.getBufferSize() );
    }

    @Override
    public void start()
    {
        super.start();
        this.selector.register( inner, OP_READ, key -> read( innerBuffer, inner, outer ) );
        this.selector.register( outer, OP_READ, key -> read( outerBuffer, outer, inner ) );
    }

    @Override
    public void stop()
    {
        super.stop();
        try
        {
            inner.close();
            outer.close();
        }
        catch( IOException e )
        {
            log.error( e.getMessage(), e );
        }
        innerBuffer.clear();
        outerBuffer.clear();
    }

    private void read( ByteBuffer readBuffer, SocketChannel inner, SocketChannel outer )
    {
        try
        {
            long read = inner.read( readBuffer );
            log.info( "Read from outer {} bytes", read );
            if( read < 0 )
                stop();
            if( read > 0 )
            {
                readBuffer.flip();
                long wrote = outer.write( readBuffer );
                if( wrote > 0 )
                    log.info( "Wrote {} bytes to outer", wrote );
            }
        }
        catch( IOException e )
        {
            log.error( e.getMessage(), e );
        }
        finally
        {
            readBuffer.clear();
        }
    }
}
