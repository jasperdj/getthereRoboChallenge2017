package nl.getthere.robo.aangeleverd.comm;


class ArduinoIO {
    static int readByte() {
        // Hier code invullen om byte van Arduino te lezen.
        // Als lezen lukt, dan waarde retourneren als 0 ... 255.
        // Bij fout de integer waarde -1 retourneren.
        
        // Conversie van byte naar int:
        byte b = ....
        int val = b & 0xff;  // Belangrijk om mask 0xff te gebruiken!
        return val;
    }
    
    
    
    static boolean writeByte(int bt) {
        byte b = (byte)bt;
        System.out.println("Write byte:" + b);
        
        return false;
    }
}
