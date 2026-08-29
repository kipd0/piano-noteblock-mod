package name.modid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

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
                        RenderType.lines()
                );

        /*
         * =====================================
         * CURRENT + NEXT ARE THE SAME NOTE
         * =====================================
         *
         * Example:
         *
         * 6,6,4
         *
         * We cannot draw yellow and red on the
         * same block, so orange means:
         *
         * "Click this block now, and it is also
         * the next note."
         */

        if (currentNote == nextNote) {

            renderNoteBox(
                    matrices,
                    vertices,
                    currentNote,

                    // ORANGE
                    1.0F,
                    0.5F,
                    0.0F
            );

        } else {

            /*
             * =====================================
             * CURRENT NOTE
             * =====================================
             *
             * Yellow = click NOW
             */

            renderNoteBox(
                    matrices,
                    vertices,
                    currentNote,

                    // YELLOW
                    1.0F,
                    1.0F,
                    0.0F
            );

            /*
             * =====================================
             * NEXT NOTE
             * =====================================
             *
             * Red = click NEXT
             */

            if (nextNote >= 1 &&
                    nextNote <= 24) {

                renderNoteBox(
                        matrices,
                        vertices,
                        nextNote,

                        // RED
                        1.0F,
                        0.0F,
                        0.0F
                );
            }
        }

        matrices.popPose();
    }

    /*
     * =========================================================
     * DRAW ONE NOTE BLOCK
     * =========================================================
     */

    private static void renderNoteBox(
            PoseStack matrices,
            VertexConsumer vertices,
            int noteNumber,
            float red,
            float green,
            float blue
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

        AABB box =
                new AABB(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        pos.getX() + 1,
                        pos.getY() + 1,
                        pos.getZ() + 1
                );

        ShapeRenderer.renderLineBox(
                matrices,
                vertices,
                box.inflate(0.05),
                red,
                green,
                blue,
                1.0F
        );
    }
}
