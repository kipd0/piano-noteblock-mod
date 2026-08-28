package name.modid;

import java.util.ArrayList;
import java.util.List;

public class Song {

    public String name;

    public List<SongNote> notes = new ArrayList<>();

    public Song() {
    }

    public Song(String name) {
        this.name = name;
    }
}
