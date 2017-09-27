package nl.getthere.robo.aangeleverd.comm;

import java.util.HashSet;
import java.util.Set;


class ArduinoReader implements Runnable {
    
    private final QueueMgr mgr = QueueMgr.getInstance();
    private final Set<Integer> knownResponses = new HashSet<>();
    
    
    @Override
    public void run() {
        while (true) {
            int rsp = ArduinoIO.readByte();
            if (isKnownResponse(rsp)) {
                int rsp2 = ArduinoIO.readByte();
                if (rsp2 >= 0) {
                    mgr.emitCommand(new int[] {rsp, rsp2});
                }
            }
        }
    }
    
    
    
    
    void addKnownResponse(int r) {
        knownResponses.add(r);
    }
    
    
    
    
    private boolean isKnownResponse(int r) {
        return knownResponses.contains(r);
    }
}
