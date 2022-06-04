package pt.ua.deti.icm.android.health_spike.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class ActivityRecognitionPermission extends AppPermission {

    public ActivityRecognitionPermission(Activity activity) {
        super(activity, Manifest.permission.ACTIVITY_RECOGNITION, 1);
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
