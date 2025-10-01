package src;
public class AdapterPatternDemo {
    public static void main(String[] args) {
        AudioPlayer audioPlayer = new AudioPlayer();

        audioPlayer.play("mp3", "song1.mp3");
        audioPlayer.play("mp4", "movie.mp4");
        audioPlayer.play("vlc", "clip.vlc");
        audioPlayer.play("avi", "video.avi");
    }
}
