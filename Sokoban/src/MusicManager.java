
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;



public class MusicManager {


    private static MusicManager instance;


    public static MusicManager getInstance(){
        if(instance == null){
            instance = new MusicManager();
        }
        return instance;
    }

    private MusicManager(){
        play();
    }

    public void play(){
        try {
            Clip clip = AudioSystem.getClip();
            // Source: https://www.youtube.com/watch?v=VijZQa6hT9U&list=PLobY7vO0pgVKn4FRDgwXk5FUSiGS8_jA8&index=5
            AudioInputStream ais = AudioSystem.getAudioInputStream(MusicManager.class.getResource("/sound/music.wav"));
            clip.open(ais);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
