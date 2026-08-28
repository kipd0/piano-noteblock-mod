package name.modid;

public class SongNote {

    public int noteBlock;
    public long durationMs;

    public SongNote() {
    }

    public SongNote(int noteBlock, long durationMs) {
        this.noteBlock = noteBlock;
        this.durationMs = durationMs;
    }
}
