package name.modid;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

public class PianoHudRenderer {

    public static void register() {

        HudRenderCallback.EVENT.register(
                (graphics, tickDelta) -> {

                    if (!PianoClient.PLAYER.isPlaying()) {
                        return;
                    }

                    Minecraft client =
                            Minecraft.getInstance();

                    if (client.font == null) {
                        return;
                    }

                    int current =
                            PianoClient.PLAYER
                                    .getCurrentNote();

                    int next =
                            PianoClient.PLAYER
                                    .getNextNote();

                    int nextNext =
                            PianoClient.PLAYER
                                    .getNextNextNote();

                    int centerX =
                            client.getWindow()
                                    .getGuiScaledWidth()
                                    / 2;

                    int y = 40;

                    graphics.drawCenteredString(
                            client.font,
                            "NOW: #" + current,
                            centerX,
                            y,
                            0xFFFF55
                    );

                    if (next != -1) {

                        graphics.drawCenteredString(
                                client.font,
                                "NEXT: #" + next,
                                centerX,
                                y + 14,
                                0xFFFFFF
                        );
                    }

                    if (nextNext != -1) {

                        graphics.drawCenteredString(
                                client.font,
                                "THEN: #" + nextNext,
                                centerX,
                                y + 28,
                                0xAAAAAA
                        );
                    }
                }
        );
    }
}
