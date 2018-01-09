package com.example.knallan.medley;

import android.app.Activity;
import android.app.Dialog;

import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by knallan on 1/4/2018.
 */

public class Utils {

    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final String PREFS_NAME = "MedleySettings";
    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    static void showGooglePlayServicesAvailabilityErrorDialog(Activity activity,
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                activity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
}
