package src;

import java.util.List;

public class SongIterator implements IteratorCustom {
    private List<String> songs;
    private int position = 0;

    public SongIterator(List<String> songs) {
        this.songs = songs;
    }

    @Override
    public boolean hasNext() {
        return position < songs.size();
    }

    @Override
    public Object next() {
        return hasNext() ? songs.get(position++) : null;
    }
}
