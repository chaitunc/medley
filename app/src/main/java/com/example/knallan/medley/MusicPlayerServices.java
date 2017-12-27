package com.example.knallan.medley;

import android.content.Context;

/**
 * Created by knallan on 12/26/2017.
 */

public interface MusicPlayerServices {
    void play(Context context) throws Exception;
    void stop();
}
