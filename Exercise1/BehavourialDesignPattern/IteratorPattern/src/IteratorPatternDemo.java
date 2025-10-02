package src;

public class IteratorPatternDemo {
    public static void main(String[] args) {
        SongPlaylist playlist = new SongPlaylist();
        playlist.addSong("Shape of You");
        playlist.addSong("Perfect");
        playlist.addSong("Believer");
        playlist.addSong("Senorita");

        IteratorCustom iterator = playlist.createIterator();

        System.out.println("Playing songs from playlist:");
        while (iterator.hasNext()) {
            System.out.println("Now Playing: " + iterator.next());
        }
    }
}
