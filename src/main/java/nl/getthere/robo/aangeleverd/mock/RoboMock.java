package nl.getthere.robo.aangeleverd.mock;


@SuppressWarnings("ALL")
public class RoboMock {
    public final static double DELTA_T = 0.02;  // 20 milliseconds
    public final static double BASE_LENGTH = 0.30;
    public final static double MOTOR_RATIO = 250;
    public final static double XMIN = -5;
    public final static double XMAX = 4;
    public final static double YMIN = -6;
    public final static double YMAX = 4;
    public final static double PING_DETECT_LIMIT = 0.2;
    public final static double ARENA_RADIUS = 2.5;
    
    public static final double X_OFFSET = 0.6;
    public static final double Y_OFFSET = 0.2;
    public static final double PHI_OFFSET = 0;
    
    public static final double RIGHT_DRAG = 10;
    public static final double LEFT_DRAG = 15;
    
    private double x;
    private double y;
    private double phi;

    private boolean inEmergency;    
    private double vl, vr;
    
    public RoboMock() {
        reset();
        RoboEngine engine = new RoboEngine(this);
        new Thread(engine).start();
    }
    
    
    private void reset() {
        x = 0;
        y = 0;
        phi = 0;
        vl = 0;
        vr = 0;
        inEmergency = false;
    }
    
    
    
    private void emergencyStop() {
        inEmergency = true;
        try {
            final int NSTEPS = 10;
            double dvl = vl/NSTEPS;
            double dvr = vr/NSTEPS;
            for (int i=0; i<NSTEPS; i++) {
                synchronized(this) {
                    vl -= dvl;
                    vr -= dvr;
                }
                Thread.sleep(40);
            }
            vl = vr = 0;
        }
        catch (InterruptedException ign) {
            
        }
        inEmergency = false;
    }
    
    
    
    private boolean movingInPosXDir() {
        return phi < Math.PI/4 && phi > -Math.PI/4;
    }
    
    
    
    private boolean movingInNegXDir() {
        return phi > 3*Math.PI/4 || phi < -3*Math.PI/4;
    }
    
    
    
    private boolean movingInPosYDir() {
        return phi > Math.PI/4 && phi < 3*Math.PI/4;
    }
    
    
    
    private boolean movingInNegYDir() {
        return phi < -Math.PI/4 && phi > -3*Math.PI/4;
    }
    
    
    synchronized void move() {
        double v = (vl + vr)/2;
        x += DELTA_T * v * Math.cos(phi);
        y += DELTA_T * v * Math.sin(phi);
        phi += DELTA_T * (vr - vl)/BASE_LENGTH;
        if (phi > Math.PI) phi -= 2*Math.PI;
        if (phi < -Math.PI) phi += 2*Math.PI;
        if (XMAX - x < PING_DETECT_LIMIT && movingInPosXDir()) {
            emergencyStop();
            return;
        }
        if (x - XMIN < PING_DETECT_LIMIT && movingInNegXDir()) {
            emergencyStop();
            return;
        }
        if (YMAX - y < PING_DETECT_LIMIT && movingInPosYDir()) {
            emergencyStop();
            return;
        }
        if (y - YMIN < PING_DETECT_LIMIT && movingInNegYDir()) {
            emergencyStop();
            return;
        }
    }
    
    
    public synchronized void setRightSpeed(int val) {
        if (!inEmergency) {
            if (val < -200) val = -200;
            if (val > 200) val = 200;
            if (val < 0) {
                val += RIGHT_DRAG;
                if (val > 0) val = 0;
            }
            if (val > 0) {
                val -= RIGHT_DRAG;
                if (val < 0) val = 0;
            }
            vr = val / MOTOR_RATIO;
        }
    }
    
    
    public synchronized void setLeftSpeed(int val) {
        if (!inEmergency) {
            if (val < -200) val = -200;
            if (val > 200) val = 200;
            if (val < 0) {
                val += LEFT_DRAG;
                if (val > 0) val = 0;
            }
            if (val > 0) {
                val -= LEFT_DRAG;
                if (val < 0) val = 0;
            }
            vl = val / MOTOR_RATIO;
        }
    }
    
    
    public synchronized int[] getPose() {
        int SCALE = 231;
        double xo = X_OFFSET * Math.cos(phi) - Y_OFFSET * Math.sin(phi);
        double yo = X_OFFSET * Math.sin(phi) + Y_OFFSET * Math.cos(phi);
        int[] out = new int[] {(int)((x-xo)*SCALE), (int)((y-yo)*SCALE), (int)(0.5 + phi*1000)};
        if (out[0]*out[0] + out[1]*out[1] > SCALE*SCALE*ARENA_RADIUS*ARENA_RADIUS) {
            out[0] = out[1] = out[2] = 0;
        }
        return out;
    }
    
    
    
    public static void main(String[] args) {
        System.out.println("start");
        RoboMock robo = new RoboMock();
        robo.setLeftSpeed(-200);
        robo.setRightSpeed(200);
        while (true) {
            try {Thread.sleep(500);} catch (InterruptedException ignore) {}
            int[] pose = robo.getPose();
            System.out.println(pose[0] + " " + pose[1] + " " + pose[2]);
        }
    }
}



class RoboEngine implements Runnable {
    RoboMock robo;
    
    RoboEngine(RoboMock robo) {
        this.robo = robo;
    }
    
    public void run() {
        try {
            while (true) {
                robo.move();
                Thread.sleep((int)(1000 * RoboMock.DELTA_T));
            }
        }
        catch (InterruptedException ign) {
            
        }
    }
}
