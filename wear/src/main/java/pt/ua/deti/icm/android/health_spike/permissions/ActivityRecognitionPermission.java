package pt.ua.deti.icm.android.health_spike.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class ActivityRecognitionPermission extends AppPermission {

    public ActivityRecognitionPermission(Activity activity) {
        super(activity, Manifest.permission.ACTIVITY_RECOGNITION, 1);
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
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED;
    }

}
