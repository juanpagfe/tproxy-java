package net.jp.common;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;

public abstract class CommandLineApplication
{
    private static final Logger log = LoggerFactory
        .getLogger( "net.jp.common.CommandLineApplication" );
    protected static final String ARG_HELP = "help";
    protected final String[] args;
    protected final PrintStream out;
    protected String headerText = "\nwhere options include:";
    private String cmdLineSyntax = "java -jar tproxy-java <OPTIONS>";
    protected CommandLine commandLine;

    /**
     * Launches main process of the application
     * 
     * @param cmd
     *            command line arguments
     */
    protected abstract void launch( CommandLine cmd ) throws Exception;

    /**
     * Returns the command line options of the application
     * 
     * @return options
     */
    protected abstract Options getOptions();

    /**
     * Receives the main string arguments and a PrintStream
     * 
     * @param args
     *            arguments
     * @param out
     *            print stream
     */
    protected CommandLineApplication( String[] args, PrintStream out )
    {
        this.args = args;
        this.out = out;
    }

    /**
     * Returns basic helper option to explain how to use the application
     * 
     * @return helper option
     */
    protected Option getHelperOption()
    {
        return Option.builder( "h" ).desc( "Help" ).numberOfArgs( 0 ).longOpt( "help" ).build();
    }

    /**
     * Returns the footer of the help blurb, which generally describes usage
     * examples.
     *
     * @param options
     *            The Command line options, to be incorporated into the footer
     *            message.
     */
    protected CharSequence getHelpFooter( Options options )
    {
        return null;
    }

    /**
     * Formats and prints help with given options
     * 
     * @param options
     *            application options
     */
    protected void printHelp( Options options )
    {
        HelpFormatter formatter = new HelpFormatter();

        String footer;
        CharSequence footerSequence = getHelpFooter( options );
        if( footerSequence == null )
            footer = "";
        else
            footer = footerSequence.toString();

        formatter.printHelp( getCommandLineSyntax(), headerText, options, footer, false );
    }

    protected String getCommandLineSyntax()
    {
        return cmdLineSyntax;
    }

    /**
     * Run the command-line parser and launch the application
     */
    public void start()
    {
        CommandLineParser parser = new DefaultParser();
        Options options = getOptions();
        try
        {
            options.addOption( getHelperOption() );
            CommandLine commandLine = parser.parse( options, args );
            if( commandLine.hasOption( ARG_HELP ) )
            {
                printHelp( options );
            }
            else
            {
                this.commandLine = commandLine;
                launch( commandLine );
            }
        }
        catch( Exception exception )
        {
            if( exception instanceof ParseException )
                log.error( exception.getMessage() );
            else
                log.error( exception.getMessage(), exception );
            printHelp( options );
            // out.println( exception.getLocalizedMessage() );
            // exception.printStackTrace();
        }
    }
}