package pt.ua.deti.icm.android.health_spike.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class BodySensorsPermission extends AppPermission{

    public BodySensorsPermission(Activity activity) {
        super(activity, Manifest.permission.BODY_SENSORS, 2);
    }

    @Override
    public void askPermission() {
        if (!isGranted()) {
            Log.i("HealthSpike", "Permission not granted");
            activity.requestPermissions(new String[]{permission}, 1);
        } else {
            Log.i("HealthSpike", "Permission granted");
        }
    }

    @Override
    public boolean isGranted() {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }


}
