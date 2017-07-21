package es.whoisalex.infonotes.ScreenSplash;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;

import es.whoisalex.infonotes.Activitys.MapsActivity;
import es.whoisalex.infonotes.R;

/**
 * Created by Alex on 20/07/2017.
 */

public class ScreenSplash extends Activity {

    private static final long TIME = 2500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_screensplash);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, TIME);
    }

    @Override
    public void onBackPressed() {}
}
