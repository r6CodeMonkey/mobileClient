package oddymobstar.activity.helper;

import android.location.LocationManager;

import oddymobstar.activity.listener.LocationListener;
import oddymobstar.util.Configuration;

/**
 * Created by timmytime on 03/12/15.
 */
public class LocationHelper {

    private android.os.Handler handler = new android.os.Handler();
    private Thread locationUpdates;
    private Configuration configuration;

    public LocationHelper(Configuration configuration) {
        this.configuration = configuration;
    }

    public void initLocationUpdates(final LocationManager locationManager, final LocationListener locationListener) {
        locationUpdates = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Long.parseLong(configuration.getConfig(Configuration.GPS_UPDATE_INTERVAL).getValue()), 0, locationListener);
                    }
                });

            }
        });
        locationUpdates.start();
    }

    public Thread getLocationUpdates() {
        return locationUpdates;
    }

    public void killLocationUpdates() {
        locationUpdates = null;
    }

}
