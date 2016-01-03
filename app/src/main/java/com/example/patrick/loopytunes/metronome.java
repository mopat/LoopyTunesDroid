package com.example.patrick.loopytunes;

import java.util.TimerTask;
import java.util.Timer;
import android.util.Log;

/**
 * Created by Patrick on 02.01.2016.
 */

public class metronome extends TimerTask {
    Timer timer = new Timer();
    final int FPS = 40;
/*    TimerTask updateBall = new UpdateBallTask();
    timer.scheduleAtFixedRate(updateBall,0,1000/FPS);*/

    public void run() {
       Log.d("OVER", "OVER");
    }

}
