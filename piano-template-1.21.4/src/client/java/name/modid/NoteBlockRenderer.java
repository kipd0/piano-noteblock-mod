package name.modid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;

public class NoteBlockRenderer {

    public static void register() {

        WorldRenderEvents.AFTER_ENTITIES.register(
                NoteBlockRenderer::render
        );
    }

    private static void render(
            WorldRenderContext context
    ) {

        if (!PianoClient.PLAYER.isPlaying()) {
            return;
        }

        int currentNote =
                PianoClient.PLAYER
                        .getCurrentNote();

        int nextNote =
                PianoClient.PLAYER
                        .getNextNote();

        if (currentNote < 1 ||
                currentNote > 24) {
            return;
        }

        PoseStack matrices =
                context.matrixStack();

        if (matrices == null) {
            return;
        }

        var consumers =
                context.consumers();

        if (consumers == null) {
            return;
        }

        var camera =
                context.camera();

        double camX =
                camera.getPosition().x;

        double camY =
                camera.getPosition().y;

        double camZ =
                camera.getPosition().z;

        matrices.pushPose();

        matrices.translate(
                -camX,
                -camY,
                -camZ
        );

        VertexConsumer vertices =
                consumers.getBuffer(
                        RenderType.debugFilledBox()
                );

        /*
         * SAME BLOCK TWICE
         *
         * Purple = click this block now,
         * then click it again.
         */

        if (currentNote == nextNote) {

            renderFilledBlock(
                    matrices,
                    vertices,
                    currentNote,

                    // PURPLE
                    0.72F,
                    0.25F,
                    1.0F,

                    // TRANSPARENCY
                    0.48F
            );

        } else {

            /*
             * CURRENT NOTE
             *
             * Cyan = click now.
             */

            renderFilledBlock(
                    matrices,
                    vertices,
                    currentNote,

                    // CYAN
                    0.10F,
                    0.90F,
                    1.0F,

                    // TRANSPARENCY
                    0.48F
            );

            /*
             * NEXT NOTE
             *
             * Pink = next block.
             */

            if (nextNote >= 1 &&
                    nextNote <= 24) {

                renderFilledBlock(
                        matrices,
                        vertices,
                        nextNote,

                        // PINK
                        1.0F,
                        0.18F,
                        0.62F,

                        // TRANSPARENCY
                        0.38F
                );
            }
        }

        matrices.popPose();
    }

    private static void renderFilledBlock(
            PoseStack matrices,
            VertexConsumer vertices,
            int noteNumber,
            float red,
            float green,
            float blue,
            float alpha
    ) {

        NoteBlockData data =
                PianoClient.CONFIG
                        .getNoteBlock(noteNumber);

        if (data == null) {
            return;
        }

        BlockPos pos =
                new BlockPos(
                        data.x,
                        data.y,
                        data.z
                );

        /*
         * Slight expansion prevents
         * z-fighting with the real block.
         */

        double expansion = 0.002;

        double minX =
                pos.getX() - expansion;

        double minY =
                pos.getY() - expansion;

        double minZ =
                pos.getZ() - expansion;

        double maxX =
                pos.getX() + 1 + expansion;

        double maxY =
                pos.getY() + 1 + expansion;

        double maxZ =
                pos.getZ() + 1 + expansion;

        ShapeRenderer.addChainedFilledBoxVertices(
                matrices,
                vertices,

                minX,
                minY,
                minZ,

                maxX,
                maxY,
                maxZ,

                red,
                green,
                blue,
                alpha
        );
    }
}
