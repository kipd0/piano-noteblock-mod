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
         * =====================================================
         * SAME NOTE TWICE
         * =====================================================
         *
         * Purple = current note is also the next note.
         *
         * Example:
         *
         * 6,6,4
         *
         * #6 will be purple.
         */

        if (currentNote == nextNote) {

            renderGlowBox(
                    matrices,
                    vertices,
                    currentNote,

                    // PURPLE
                    0.75F,
                    0.25F,
                    1.0F
            );

        } else {

            /*
             * =================================================
             * CURRENT NOTE
             * =================================================
             *
             * Bright cyan.
             */

            renderGlowBox(
                    matrices,
                    vertices,
                    currentNote,

                    // CYAN
                    0.15F,
                    0.90F,
                    1.0F
            );

            /*
             * =================================================
             * NEXT NOTE
             * =================================================
             *
             * Hot pink.
             */

            if (nextNote >= 1 &&
                    nextNote <= 24) {

                renderGlowBox(
                        matrices,
                        vertices,
                        nextNote,

                        // PINK
                        1.0F,
                        0.20F,
                        0.65F
                );
            }
        }

        matrices.popPose();
    }

    /*
     * =========================================================
     * OSU-STYLE GLOW BOX
     * =========================================================
     */

    private static void renderGlowBox(
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

        /*
         * Outer glow
         */

        ShapeRenderer.renderLineBox(
                matrices,
                vertices,
                box.inflate(0.10),
                red,
                green,
                blue,
                0.25F
        );

        /*
         * Middle glow
         */

        ShapeRenderer.renderLineBox(
                matrices,
                vertices,
                box.inflate(0.075),
                red,
                green,
                blue,
                0.50F
        );

        /*
         * Strong colored outline
         */

        ShapeRenderer.renderLineBox(
                matrices,
                vertices,
                box.inflate(0.05),
                red,
                green,
                blue,
                1.0F
        );

        /*
         * Small bright inner outline.
         *
         * This gives the target a brighter,
         * rhythm-game-like edge.
         */

        ShapeRenderer.renderLineBox(
                matrices,
                vertices,
                box.inflate(0.03),
                1.0F,
                1.0F,
                1.0F,
                0.85F
        );
    }
}
