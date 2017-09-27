package nl.getthere.robo.aangeleverd.comm;

public class MainController {
    private final ArduinoReader rdr;
    private final ArduinoCommander cmd;
    private final QueueMgr mgr;
    
    private MainController() {
        rdr = new ArduinoReader();
        cmd = new ArduinoCommander();
        mgr = QueueMgr.getInstance();
    }
    
    
    
    // Implementation of singleton pattern with lazy loading.
    private static MainController controller = null;
    public static MainController getInstance() {
        if (controller == null) {
            synchronized(MainController.class) {
                if (controller == null) {
                    controller = new MainController();
                }
            }
        }
        return controller;
    }
     
    
    
    
    public void init() {
        Thread rdrThread = new Thread(rdr, "ArduinoReader");
        Thread cmdThread = new Thread(cmd, "ArduinoCommander");
        rdrThread.start();
        cmdThread.start();
    }
    
    
    
    
    public void addKnownResponse(int rsp) {
        rdr.addKnownResponse(rsp);
    }
    
    
    
    public boolean doCommand(int... command) {
        return mgr.emitCommand(command);
    }
    
    
    
    public int[] getResponse() {
        return mgr.getResponse();
    }
}
