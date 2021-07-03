package net.jp.proxy;

import java.net.InetSocketAddress;

public class TProxyOptions
{
    public static final InetSocketAddress DEFAULT_BIND_ADDRESS = new InetSocketAddress( "0.0.0.0", 0 );
    public static final int BUFFER_SIZE = 512 * 1024;

    public enum Protocol
    {
        TCP, UDP
    }

    private InetSocketAddress bindAddress = DEFAULT_BIND_ADDRESS;
    private InetSocketAddress address = null;
    private Protocol protocol = Protocol.TCP;
    private int bufferSize = BUFFER_SIZE;
    private boolean directBuffer = false;

    public InetSocketAddress getBindAddress()
    {
        return bindAddress;
    }

    public void setBindAddress( InetSocketAddress bindAddress )
    {
        this.bindAddress = bindAddress;
    }

    public InetSocketAddress getAddress()
    {
        return address;
    }

    public void setAddress( InetSocketAddress address )
    {
        this.address = address;
    }

    public Protocol getProtocol()
    {
        return protocol;
    }

    public void setProtocol( Protocol protocol )
    {
        this.protocol = protocol;
    }

    public void setBindAddress( String bindAddress ) throws Exception
    {
        this.bindAddress = parseAddress( bindAddress );
    }

    private InetSocketAddress parseAddress( String address ) throws Exception
    {
        String[] split = address.split( ":" );
        return new InetSocketAddress( split[0], Integer.parseInt( split[1] ) );
    }

    public void setAddress( String address ) throws Exception
    {
        this.address = parseAddress( address );
    }

    public int getBufferSize()
    {
        return bufferSize;
    }

    public void setBufferSize( int bufferSize )
    {
        this.bufferSize = bufferSize;
    }

    public boolean isDirectBuffer()
    {
        return directBuffer;
    }

    public void setDirectBuffer( boolean directBuffer )
    {
        this.directBuffer = directBuffer;
    }
}
