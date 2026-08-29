package name.modid;

import net.minecraft.client.Minecraft;

public class SongPlayer {

    private Song song;

    private int currentIndex;

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

    public int getCurrentNote() {

        if (!playing ||
                song == null ||
                currentIndex < 0 ||
                currentIndex >= song.notes.size()) {

            return -1;
        }

        return song.notes
                .get(currentIndex)
                .noteBlock;
    }

    public int getNextNote() {

        if (!playing ||
                song == null) {

            return -1;
        }

        int index =
                currentIndex + 1;

        if (index >= song.notes.size()) {
            return -1;
        }

        return song.notes
                .get(index)
                .noteBlock;
    }

    public int getNextNextNote() {

        if (!playing ||
                song == null) {

            return -1;
        }

        int index =
                currentIndex + 2;

        if (index >= song.notes.size()) {
            return -1;
        }

        return song.notes
                .get(index)
                .noteBlock;
    }

    public void clickNote(int noteNumber) {

        if (!playing ||
                song == null ||
                currentIndex < 0 ||
                currentIndex >= song.notes.size()) {

            return;
        }

        if (noteNumber != highlightedNote) {

            return;
        }

        currentIndex++;

        if (currentIndex >= song.notes.size()) {

            stop();

            return;
        }

        highlightedNote =
                song.notes
                        .get(currentIndex)
                        .noteBlock;
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

        // No automatic timing.
    }
}
