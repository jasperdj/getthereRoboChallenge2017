package nl.getthere.robo.aangeleverd.comm;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;


class QueueMgr {
    static final int MAX_PENDING_COMMANDS = 100;
    static final int MAX_UNREAD_RESPONSES = 500;
    
    // Implement singleton pattern with greedy instantiation.
    // We only want one instance of the queue manager to be 
    // active at any time.
    private final static QueueMgr MGR = new QueueMgr();
    static QueueMgr getInstance() {
        return MGR;
    }
    
    
    // The command queue and response queue.
    private final BlockingDeque<int[]> commands;
    private final BlockingDeque<int[]> responses;
    
    
    // Make constructor private, so that no other thread or
    // piece of code can create a second instance.
    private QueueMgr() {
        commands = new LinkedBlockingDeque<>(MAX_PENDING_COMMANDS);
        responses = new LinkedBlockingDeque<>(MAX_UNREAD_RESPONSES);
    }
    
    
    
    
    // Emit a variable length command. This normally should be immediate,
    // but we wait for at most 10 seconds.
    boolean emitCommand(int... cmd) {
        try {
            commands.offer(cmd, 10, TimeUnit.SECONDS);
            return true;
        }
        catch (InterruptedException ex) {
            return false;
        }
    }
    
    
    
    // Obtain a command from the queue. We wait for at most one hour.
    int[] getCommand() {
        try {
            return commands.poll(3600, TimeUnit.SECONDS);
        }
        catch (InterruptedException ex) {
            return null;
        }
    }
    
    
    
    
    
    // Emit a response from the Arduino into the queue. If this does
    // not succeed, this method does NOT block. We never want to
    // hang up the Arduino in its writing data to the serial line,
    // so if the response cannot be written then it is lost. The
    // return value tells whether writing the response succeeds.
    // We can log something if an error occurs.
    boolean emitResponse(int... rsp) {
        return responses.offer(rsp);
    }
    
    
    
    // Obtain a response as an array of ints. We wait at most 10 seconds.
    // The Arduino normally should produce some data every few tens of ms.
    // If it does not produce any data for 10 seconds, then consider it dead.
    int[] getResponse() {
        try {
            return responses.poll(10, TimeUnit.SECONDS);
        }
        catch (InterruptedException ex) {
            return null;
        }
    }
}
