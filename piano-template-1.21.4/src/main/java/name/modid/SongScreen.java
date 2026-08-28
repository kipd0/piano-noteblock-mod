package name.modid;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;

public class SongScreen extends Screen {

    private EditBox noteBox;
    private EditBox timeBox;

    private String message = "";

    public SongScreen() {

        super(
                net.minecraft.network.chat.Component.literal(
                        "Song Editor"
                )
        );
    }

    @Override
    protected void init() {

        int centerX = width / 2;

        noteBox =
                new EditBox(
                        font,
                        centerX - 100,
                        70,
                        80,
                        20,
                        net.minecraft.network.chat.Component.literal(
                                "Note"
                        )
                );

        timeBox =
                new EditBox(
                        font,
                        centerX + 20,
                        70,
                        80,
                        20,
                        net.minecraft.network.chat.Component.literal(
                                "Time"
                        )
                );

        noteBox.setValue("1");
        timeBox.setValue("0.5");

        addRenderableWidget(noteBox);
        addRenderableWidget(timeBox);

        addRenderableWidget(
                Button.builder(
                        net.minecraft.network.chat.Component.literal(
                                "Add Note"
                        ),
                        button -> addNote()
                ).bounds(
                        centerX - 100,
                        105,
                        200,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        net.minecraft.network.chat.Component.literal(
                                "Play"
                        ),
                        button -> {

                            PianoClient.PLAYER.play(
                                    PianoClient.CONFIG
                                            .getCurrentSong()
                            );

                        }
                ).bounds(
                        centerX - 100,
                        135,
                        95,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        net.minecraft.network.chat.Component.literal(
                                "Stop"
                        ),
                        button ->
                                PianoClient.PLAYER.stop()
                ).bounds(
                        centerX + 5,
                        135,
                        95,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        net.minecraft.network.chat.Component.literal(
                                "Clear Song"
                        ),
                        button -> {

                            PianoClient.CONFIG
                                    .getCurrentSong()
                                    .notes
                                    .clear();

                            PianoClient.CONFIG.save();

                            message =
                                    "Song cleared.";

                        }
                ).bounds(
                        centerX - 100,
                        165,
                        200,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        net.minecraft.network.chat.Component.literal(
                                "Done"
                        ),
                        button -> onClose()
                ).bounds(
                        centerX - 100,
                        195,
                        200,
                        20
                ).build()
        );
    }

    private void addNote() {

        int note;

        double seconds;

        try {

            note =
                    Integer.parseInt(
                            noteBox.getValue()
                    );

            seconds =
                    Double.parseDouble(
                            timeBox.getValue()
                    );

        } catch (NumberFormatException e) {

            message =
                    "Invalid number.";

            return;
        }

        if (note < 1 || note > 24) {

            message =
                    "Note must be 1-24.";

            return;
        }

        if (seconds <= 0) {

            message =
                    "Time must be greater than 0.";

            return;
        }

        long milliseconds =
                Math.round(
                        seconds * 1000.0
                );

        PianoClient.CONFIG
                .getCurrentSong()
                .notes
                .add(
                        new SongNote(
                                note,
                                milliseconds
                        )
                );

        PianoClient.CONFIG.save();

        message =
                "Added #" +
                        note +
                        " for " +
                        seconds +
                        " seconds.";
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {

        renderBackground(
                graphics,
                mouseX,
                mouseY,
                delta
        );

        int centerX = width / 2;

        graphics.drawCenteredString(
                font,
                "Song Editor",
                centerX,
                30,
                0xFFFFFF
        );

        graphics.drawString(
                font,
                "Note #",
                centerX - 100,
                55,
                0xAAAAAA
        );

        graphics.drawString(
                font,
                "Seconds",
                centerX + 20,
                55,
                0xAAAAAA
        );

        Song song =
                PianoClient.CONFIG
                        .getCurrentSong();

        int y = 235;

        for (int i = 0;
             i < song.notes.size();
             i++) {

            SongNote note =
                    song.notes.get(i);

            graphics.drawString(
                    font,
                    (i + 1) +
                            ".  #" +
                            note.noteBlock +
                            "    " +
                            (note.durationMs / 1000.0) +
                            "s",
                    centerX - 100,
                    y,
                    0xFFFFFF
            );

            y += 12;

            if (y > height - 15) {
                break;
            }
        }

        if (!message.isEmpty()) {

            graphics.drawCenteredString(
                    font,
                    message,
                    centerX,
                    220,
                    0xFFFFFF
            );
        }

        super.render(
                graphics,
                mouseX,
                mouseY,
                delta
        );
    }

    @Override
    public boolean isPauseScreen() {

        return false;
    }
}
