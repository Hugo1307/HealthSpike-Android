package pt.ua.deti.icm.android.health_spike.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class FineLocationPermission extends AppPermission {

    public FineLocationPermission(Activity activity) {
        super(activity, Manifest.permission.ACCESS_FINE_LOCATION, 2);
    }

    @Override
    public void askPermission() {
        if (!isGranted()) {
            activity.requestPermissions(new String[]{permission}, 1);
        }
    }

    @Override
    public boolean isGranted() {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
