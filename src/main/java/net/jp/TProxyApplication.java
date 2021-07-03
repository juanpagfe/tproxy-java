package net.jp;

import net.jp.common.CommandLineApplication;
import net.jp.proxy.TProxy;
import net.jp.proxy.TProxyOptions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TProxyApplication extends CommandLineApplication
{
    private static final String PROTOCOL_ARG = "protocol";
    private static final String BIND_ARG = "bind";
    private static final String ADDRESS_ARG = "address";
    private static final String BUFFER_SIZE_ARG = "buffer-size";
    private static final String DIRECT_BUFFER_ARG = "direct-buffer";
    private static final Map<String, Object[]> ARGUMENTS = new HashMap<String, Object[]>() {
        {
            put( PROTOCOL_ARG,
                new Object[] { "p", "Transmission protocol (udp, tcp)", false, 1, TProxyOptions.Protocol.TCP } );
            put( BIND_ARG, new Object[] { "b", "Bind address (Example: 0.0.0.0:2323)", false, 1,
                TProxyOptions.DEFAULT_BIND_ADDRESS } );
            put( ADDRESS_ARG, new Object[] { "a", "Destination address (Example: 172.24.23.22:80", true, 1, null } );
            put( BUFFER_SIZE_ARG,
                new Object[] { "s", "Default buffer size for sockets", false, 1, TProxyOptions.BUFFER_SIZE } );
            put( DIRECT_BUFFER_ARG,
                    new Object[] { "d", "Uses java direct buffer", false, 0, false } );
        }
    };

    /**
     * Receives the main string arguments and a PrintStream
     *
     * @param args
     *            arguments
     * @param out
     */
    protected TProxyApplication( String[] args, PrintStream out )
    {
        super( args, out );
    }

    @Override
    protected void launch( CommandLine cmd ) throws Exception
    {
        TProxyOptions options = parseOptions( cmd );
        TProxy tProxy = new TProxy( options );
        tProxy.init();
        tProxy.start();
    }

    private TProxyOptions parseOptions( CommandLine cmd ) throws Exception
    {
        TProxyOptions options = new TProxyOptions();

        if( cmd.hasOption( PROTOCOL_ARG ) )
            options.setProtocol(
                TProxyOptions.Protocol.valueOf( cmd.getOptionValue( PROTOCOL_ARG ).toUpperCase( Locale.ROOT ) ) );

        if( cmd.hasOption( BIND_ARG ) )
            options.setBindAddress( cmd.getOptionValue( BIND_ARG ) );

        if( cmd.hasOption( BUFFER_SIZE_ARG ) )
            options.setBufferSize( Integer.parseInt(cmd.getOptionValue( BUFFER_SIZE_ARG )) );

        if( cmd.hasOption( DIRECT_BUFFER_ARG ) )
            options.setDirectBuffer( true );

        options.setAddress( cmd.getOptionValue( ADDRESS_ARG ) );

        return options;
    }

    @Override
    protected Options getOptions()
    {
        Options options = new Options();
        for( Map.Entry<String, Object[]> entry : ARGUMENTS.entrySet() )
        {
            Object[] arg = entry.getValue();
            String description = (String)arg[1];
            if( arg[4] != null )
                description += ".  Default value is " + arg[4];

            options.addOption( Option.builder( (String)arg[0] ).longOpt( entry.getKey() ).desc( description )
                .required( (boolean)arg[2] ).numberOfArgs( (int)arg[3] ).build() );
        }
        return options;
    }

    public static void main( String[] args )
    {
        PrintStream out = System.out;
        TProxyApplication application = new TProxyApplication( args, out );
        application.start();
    }
}
