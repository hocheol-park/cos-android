package univ.ajou.cos.util;

import java.util.ArrayList;

public interface PermissionListener {

    void onPermissionGranted();

    void onPermissionDenied(ArrayList<String> deniedPermissions);

}
