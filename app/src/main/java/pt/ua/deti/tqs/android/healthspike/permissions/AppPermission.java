package pt.ua.deti.tqs.android.healthspike.permissions;

import android.app.Activity;

public abstract class AppPermission {

    protected final Activity activity;
    protected final String permission;
    protected final int requestCode;

    public AppPermission(Activity activity, String permission, int requestCode) {
        this.activity = activity;
        this.permission = permission;
        this.requestCode = requestCode;
    }

    public abstract void askPermission();

    public abstract boolean isGranted();

}
