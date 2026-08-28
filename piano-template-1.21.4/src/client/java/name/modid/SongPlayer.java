package name.modid;

import net.minecraft.client.Minecraft;

public class SongPlayer {

    private Song song;

    private int currentIndex;

    private long noteStartTime;

    private boolean playing;

    private int highlightedNote = -1;

    public void play(Song song) {

        if (song == null ||
                song.notes == null ||
                song.notes.isEmpty()) {

            return;
        }

        this.song = song;

        this.currentIndex = 0;

        this.noteStartTime =
                System.currentTimeMillis();

        this.playing = true;

        this.highlightedNote =
                song.notes.get(0).noteBlock;
    }

    public void stop() {

        playing = false;

        highlightedNote = -1;

        song = null;

        currentIndex = 0;
    }

    public boolean isPlaying() {

        return playing;
    }

    public int getHighlightedNote() {

        return highlightedNote;
    }

    public int getCurrentIndex() {

        return currentIndex;
    }

    public boolean useAlternateColor() {

        if (!playing ||
                song == null ||
                currentIndex < 0 ||
                currentIndex >= song.notes.size()) {

            return false;
        }

        int currentNote =
                song.notes
                        .get(currentIndex)
                        .noteBlock;

        int repeatPosition = 0;

        for (int i = currentIndex - 1;
             i >= 0;
             i--) {

            if (song.notes
                    .get(i)
                    .noteBlock != currentNote) {

                break;
            }

            repeatPosition++;
        }

        return repeatPosition % 2 == 1;
    }

    public void tick(Minecraft client) {

        if (!playing || song == null) {
            return;
        }

        if (currentIndex >= song.notes.size()) {

            stop();

            return;
        }

        SongNote current =
                song.notes.get(currentIndex);

        long elapsed =
                System.currentTimeMillis()
                        - noteStartTime;

        if (elapsed >= current.durationMs) {

            currentIndex++;

            if (currentIndex >= song.notes.size()) {

                stop();

                return;
            }

            SongNote next =
                    song.notes.get(currentIndex);

            highlightedNote =
                    next.noteBlock;

            noteStartTime =
                    System.currentTimeMillis();
        }
    }
}
