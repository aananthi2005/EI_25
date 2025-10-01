package src;

import java.util.ArrayList;
import java.util.List;

public class SongPlaylist implements Playlist {
    private List<String> songs = new ArrayList<>();

    public void addSong(String song) {
        songs.add(song);
    }

    @Override
    public IteratorCustom createIterator() {
        return new SongIterator(songs);
    }
}
