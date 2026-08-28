package name.modid;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

public class NoteBlockScreen extends Screen {

    private EditBox numberBox;

    private String message = "";

    public NoteBlockScreen() {

        super(
                net.minecraft.network.chat.Component.literal(
                        "Note Block Manager"
                )
        );
    }

    @Override
    protected void init() {

        int centerX = width / 2;

        numberBox =
                new EditBox(
                        font,
                        centerX - 50,
                        70,
                        100,
                        20,
                        net.minecraft.network.chat.Component.literal(
                                "Number"
                        )
                );

        numberBox.setValue("1");

        addRenderableWidget(numberBox);

        addRenderableWidget(
                Button.builder(
                        net.minecraft.network.chat.Component.literal(
                                "Assign Looking-At Block"
                        ),
                        button -> assign()
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
                                "Song Editor"
                        ),
                        button -> {

                            Minecraft.getInstance()
                                    .setScreen(
                                            new SongScreen()
                                    );

                        }
                ).bounds(
                        centerX - 100,
                        135,
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
                        165,
                        200,
                        20
                ).build()
        );
    }

    private void assign() {

        Minecraft client =
                Minecraft.getInstance();

        if (client.hitResult == null) {

            message = "You are not looking at a block.";

            return;
        }

        if (!(client.hitResult instanceof
                net.minecraft.world.phys.BlockHitResult hit)) {

            message = "You are not looking at a block.";

            return;
        }

        BlockPos pos =
                hit.getBlockPos();

        if (client.level == null) {
            return;
        }

        if (!client.level
                .getBlockState(pos)
                .is(Blocks.NOTE_BLOCK)) {

            message = "That is not a note block.";

            return;
        }

        int number;

        try {

            number =
                    Integer.parseInt(
                            numberBox.getValue()
                    );

        } catch (NumberFormatException e) {

            message = "Enter a number from 1 to 24.";

            return;
        }

        if (number < 1 || number > 24) {

            message = "Number must be 1-24.";

            return;
        }

        PianoClient.CONFIG.setNoteBlock(
                number,
                new NoteBlockData(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ()
                )
        );

        message =
                "Saved #" +
                        number +
                        " at " +
                        pos.getX() +
                        ", " +
                        pos.getY() +
                        ", " +
                        pos.getZ();
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

        graphics.drawCenteredString(
                font,
                "Note Block Manager",
                width / 2,
                35,
                0xFFFFFF
        );

        graphics.drawCenteredString(
                font,
                "Enter 1-24, then look at a note block.",
                width / 2,
                55,
                0xAAAAAA
        );

        if (!message.isEmpty()) {

            graphics.drawCenteredString(
                    font,
                    message,
                    width / 2,
                    200,
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
