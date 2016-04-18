package legacy.com.bluetoothle;

import java.util.List;

/**
 * Created by IIS on 4/17/2016.
 */
public class ProximityBeacon {
    private String deviceAddress;
    private int signal;

    public ProximityBeacon(String address, int rssi){
        deviceAddress = address;
        signal = rssi;

    }
}
