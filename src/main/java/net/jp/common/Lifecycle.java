package net.jp.common;

import java.util.LinkedList;
import java.util.List;

public interface Lifecycle
{
    enum State
    {
        UNINITIALIZED, INITIALIZED, STARTED, STOPPED, DESTROYED
    }

    void init() throws Exception;

    void start();

    void stop();

    void destroy();

    State getState();

    static State setState(Lifecycle obj, State newState) throws Exception
    {
        State oldState = obj.getState();

        if( newState.ordinal() < oldState.ordinal() )
        {
            throw new IllegalStateException( "Cannot move into " + newState.name() + " state from " + oldState.name()
                + " " + obj.getClass().getName() );
        }

        if( newState.ordinal() >= State.INITIALIZED.ordinal() )
        {
            if( oldState.ordinal() < State.INITIALIZED.ordinal() )
            {
                obj.init();
            }
        }

        if( newState.ordinal() >= State.STARTED.ordinal() )
        {
            if( oldState.ordinal() < State.STARTED.ordinal() )
            {
                obj.start();
            }
        }

        if( newState.ordinal() >= State.STOPPED.ordinal() )
        {
            if( oldState.ordinal() < State.STOPPED.ordinal() )
            {
                obj.stop();
            }
        }

        if( newState.ordinal() >= State.DESTROYED.ordinal() )
        {
            if( oldState.ordinal() < State.DESTROYED.ordinal() )
            {
                obj.destroy();
            }
        }

        return obj.getState();
    }

    static List<State> getTransitionSequence(State oldState, State newState)
    {
        List<State> sequence = new LinkedList<>();
        State[] states = State.values();

        if( oldState.ordinal() > newState.ordinal() )
        {
            return sequence;
        }

        for( State state : states )
        {
            if( oldState.compareTo( state ) < 0 && newState.compareTo( state ) >= 0 )
            {
                sequence.add( state );
            }
        }

        return sequence;
    }

    default boolean isStateAtLeast(State state)
    {
        return isAtLeast( getState(), state );
    }

    static boolean isAtLeast(State currentState, State state)
    {
        return ( currentState.ordinal() >= state.ordinal() );
    }
}
