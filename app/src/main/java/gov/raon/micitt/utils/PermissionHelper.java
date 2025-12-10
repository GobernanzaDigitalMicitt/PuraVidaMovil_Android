package gov.raon.micitt.utils;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class PermissionHelper {
    private final int PERMISSION_REQUEST_CODE = 0x08;

    public interface PermissionResultListener {
        void onPermissionResult(boolean isGranted);
    }

    private PermissionResultListener permissionResultListener;

    private boolean hasPermissions(@NonNull Context context, String[] permissions) {

        boolean hasPermissions = true;
        if (permissions != null && permissions.length > 0) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PERMISSION_GRANTED) {
                    hasPermissions = false;
                    break;
                }
            }
        }

        return hasPermissions;
    }

    public void requestPermissions(final @NonNull Activity activity,
                                   final @NonNull String[] permissions,
                                   @NonNull PermissionResultListener permissionResultListener) {

        this.permissionResultListener = permissionResultListener;

        if (hasPermissions(activity, permissions)) {
            this.permissionResultListener.onPermissionResult(true);
        } else {
            ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {

            boolean isGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PERMISSION_GRANTED) {
                    isGranted = false;
                    break;
                }
            }

            if (permissionResultListener != null) {
                permissionResultListener.onPermissionResult(isGranted);
            }
        }
    }
}
