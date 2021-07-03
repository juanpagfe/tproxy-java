package net.jp.nio;

public class NioException extends RuntimeException
{

    public NioException( String message )
    {
        super( message );
    }

    public NioException( String message, Throwable cause )
    {
        super( message, cause );
    }

}

