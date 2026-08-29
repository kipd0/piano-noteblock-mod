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

    /*
     * Called when the player left-clicks
     * an assigned note block.
     *
     * The song only advances if the clicked
     * note is the currently highlighted note.
     */
    public void clickNote(int noteNumber) {

        if (!playing ||
                song == null ||
                currentIndex < 0 ||
                currentIndex >= song.notes.size()) {

            return;
        }

        /*
         * Wrong note clicked.
         * Do nothing.
         */
        if (noteNumber != highlightedNote) {

            return;
        }

        /*
         * Correct note was clicked.
         * Move to the next song entry.
         */
        currentIndex++;

        /*
         * End of song.
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

    /*
     * Repeated notes alternate:
     *
     * 6 = yellow
     * 6 = red
     * 6 = yellow
     * 6 = red
     */
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

    /*
     * Kept for compatibility because
     * PianoClient currently calls PLAYER.tick(client).
     *
     * Timing no longer happens here.
     */
    public void tick(Minecraft client) {

        // No automatic timing anymore.
        // The song advances only when
        // the correct note block is clicked.
    }
}
