package frontend.customfadeeffects;

import javafx.scene.effect.BoxBlur;

import java.util.Timer;
import java.util.TimerTask;


public class BlurFade extends BoxBlur {

    private int FADE_SIZE = 10;

    public BlurFade(){
        super();
        super.setHeight(0);
        super.setWidth(0);
        super.setIterations(5);
    }

    public void fadeIn(){
        Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                BlurFade.super.setHeight(BlurFade.super.getHeight()+0.1);
                BlurFade.super.setWidth(BlurFade.super.getHeight()+0.1);
                if (BlurFade.super.getHeight() > FADE_SIZE) {
                    timer.cancel();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask,0,1);
    }

    public void fadeOut(){
        Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                BlurFade.super.setHeight(BlurFade.super.getHeight()-0.1);
                BlurFade.super.setWidth(BlurFade.super.getHeight()-0.1);
                if (BlurFade.super.getHeight() < 0.1) {
                    timer.cancel();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask,0,1);
    }

}
