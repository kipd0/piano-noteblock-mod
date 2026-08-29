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

    /*
     * Counts how many clicks on THIS SAME
     * note are still remaining.
     *
     * 6,6,6,6,4
     *
     * start = 4
     * click = 3
     * click = 2
     * click = 1
     *
     * Renderer hides 1.
     */
    public int getRemainingRepeatCount() {

        if (!playing ||
                song == null ||
                currentIndex < 0 ||
                currentIndex >= song.notes.size()) {
            return 0;
        }

        int currentNote =
                song.notes
                        .get(currentIndex)
                        .noteBlock;

        int remaining = 1;

        for (int i = currentIndex + 1;
             i < song.notes.size();
             i++) {

            if (song.notes
                    .get(i)
                    .noteBlock != currentNote) {
                break;
            }

            remaining++;
        }

        return remaining;
    }

    public void clickNote(int noteNumber) {

        if (!playing ||
                song == null ||
                currentIndex < 0 ||
                currentIndex >= song.notes.size()) {
            return;
        }

        /*
         * Wrong note = do nothing.
         */
        if (noteNumber != highlightedNote) {
            return;
        }

        /*
         * Correct note.
         */
        currentIndex++;

        /*
         * Song finished.
         */
        if (currentIndex >= song.notes.size()) {
            stop();
            return;
        }

        highlightedNote =
                song.notes
                        .get(currentIndex)
                        .noteBlock;
    }

    public void tick(Minecraft client) {
        // No automatic timing.
    }
}
