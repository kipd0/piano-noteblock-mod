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
         * gold = click this block now,
         * then click it again.
         */

        if (currentNote == nextNote) {

            renderFilledBlock(
                    matrices,
                    vertices,
                    currentNote,

                    // gold
                    1.00F,
                    0.70F,
                    1.10F,

                    // TRANSPARENCY
                    0.45F
            );

        } else {

            /*
             * CURRENT NOTE
             *
             * mint green = click now.
             */

            renderFilledBlock(
                    matrices,
                    vertices,
                    currentNote,

                    // mint green
                    0.25F,
                    1.00F,
                    0.35F,

                    // TRANSPARENCY
                    0.45F
            );

            /*
             * NEXT NOTE
             *
             * sky blue = next block.
             */

            if (nextNote >= 1 &&
                    nextNote <= 24) {

                renderFilledBlock(
                        matrices,
                        vertices,
                        nextNote,

                        // skyblue
                        0.20F,
                        0.55F,
                        1.00F,

                        // TRANSPARENCY
                        0.30F
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
