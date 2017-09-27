package nl.getthere.robo.aangeleverd.comm;

class ArduinoCommander implements Runnable {
    private final QueueMgr mgr = QueueMgr.getInstance();
    
    @Override
    public void run() {
        while (true) {
            int[] command = mgr.getCommand();
            for (int cmdByte : command) {
                ArduinoIO.writeByte(cmdByte);
            }
        }
    }
}
