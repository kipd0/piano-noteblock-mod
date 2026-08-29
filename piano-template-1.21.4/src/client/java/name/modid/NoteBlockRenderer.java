package name.modid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
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

        int repeatCount =
                PianoClient.PLAYER
                        .getRemainingRepeatCount();

        if (currentNote < 1 ||
                currentNote > 24) {
            return;
        }

        PoseStack matrices =
                context.matrixStack();

        if (matrices == null) {
            return;
        }

        MultiBufferSource consumers =
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

        /*
         * Move world rendering relative
         * to the camera.
         */

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
         * =====================================================
         * CURRENT BLOCK
         * =====================================================
         *
         * Always green.
         *
         * Repeated notes are ALSO green now.
         */

        renderFilledBlock(
                matrices,
                vertices,
                currentNote,

                // GREEN
                0.25F,
                1.00F,
                0.35F,

                // TRANSPARENCY
                0.45F
        );

        /*
         * =====================================================
         * NEXT BLOCK
         * =====================================================
         *
         * Only show blue when the next note
         * is a DIFFERENT block.
         *
         * If it is the same block, the white
         * repeat number handles that instead.
         */

        if (nextNote >= 1 &&
                nextNote <= 24 &&
                nextNote != currentNote) {

            renderFilledBlock(
                    matrices,
                    vertices,
                    nextNote,

                    // SKY BLUE
                    0.20F,
                    0.55F,
                    1.00F,

                    // TRANSPARENCY
                    0.30F
            );
        }

        matrices.popPose();

        /*
         * =====================================================
         * REPEAT NUMBER
         * =====================================================
         *
         * Only show 2 or higher.
         *
         * Pure white:
         *
         * RGB   = 255,255,255
         * Alpha = 255
         *
         * #FFFFFFFF
         */

        if (repeatCount > 1) {

            renderRepeatNumber(
                    context,
                    currentNote,
                    repeatCount
            );
        }
    }

    /*
     * =========================================================
     * FILLED BLOCK
     * =========================================================
     */

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
         * Tiny expansion prevents the
         * overlay from fighting with
         * the real block texture.
         */

        double expansion =
                0.002;

        double minX =
                pos.getX() -
                        expansion;

        double minY =
                pos.getY() -
                        expansion;

        double minZ =
                pos.getZ() -
                        expansion;

        double maxX =
                pos.getX() +
                        1 +
                        expansion;

        double maxY =
                pos.getY() +
                        1 +
                        expansion;

        double maxZ =
                pos.getZ() +
                        1 +
                        expansion;

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

    /*
     * =========================================================
     * WHITE REPEAT NUMBER
     * =========================================================
     */

    private static void renderRepeatNumber(
            WorldRenderContext context,
            int noteNumber,
            int repeatCount
    ) {

        NoteBlockData data =
                PianoClient.CONFIG
                        .getNoteBlock(noteNumber);

        if (data == null) {
            return;
        }

        Minecraft client =
                Minecraft.getInstance();

        Font font =
                client.font;

        PoseStack matrices =
                context.matrixStack();

        MultiBufferSource consumers =
                context.consumers();

        if (matrices == null ||
                consumers == null) {
            return;
        }

        var camera =
                context.camera();

        /*
         * Center of the note block.
         *
         * Number sits slightly above
         * the center so it is easy to see.
         */

        double x =
                data.x + 0.5;

        double y =
                data.y + 0.75;

        double z =
                data.z + 0.5;

        String text =
                String.valueOf(
                        repeatCount
                );

        matrices.pushPose();

        /*
         * Move to block position relative
         * to camera.
         */

        matrices.translate(
                x -
                        camera.getPosition().x,

                y -
                        camera.getPosition().y,

                z -
                        camera.getPosition().z
        );

        /*
         * Make the number always face
         * the player's camera.
         */

        matrices.mulPose(
                camera.rotation()
        );

        /*
         * Scale Minecraft font down so
         * it fits nicely on the block.
         *
         * Negative X/Y are intentional
         * for world-space text.
         */

        float scale =
                0.035F;

        matrices.scale(
                -scale,
                -scale,
                scale
        );

        /*
         * Center the number.
         */

        float textX =
                -font.width(text) /
                        2.0F;

        /*
         * PURE WHITE.
         *
         * FF = full alpha
         * FF = red
         * FF = green
         * FF = blue
         */

        int pureWhite =
                0xFFFFFFFF;

        font.drawInBatch(
                text,
                textX,
                0.0F,
                pureWhite,

                false,

                matrices
                        .last()
                        .pose(),

                consumers,

                Font.DisplayMode.NORMAL,

                0,

                15728880
        );

        matrices.popPose();
    }
}
