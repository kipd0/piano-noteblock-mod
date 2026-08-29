package name.modid.client.mixin;

import name.modid.NoteBlockData;
import name.modid.NoteBlockLayout;
import name.modid.PianoClient;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(
            method = "startAttack",
            at = @At("HEAD")
    )
    private void piano$onLeftClick(
            CallbackInfoReturnable<Boolean> cir
    ) {

        Minecraft client =
                Minecraft.getInstance();

        /*
         * Only care about clicks while
         * a song is actively playing.
         */
        if (!PianoClient.PLAYER.isPlaying()) {
            return;
        }

        /*
         * Make sure we are actually
         * looking at a block.
         */
        HitResult hit =
                client.hitResult;

        if (!(hit instanceof BlockHitResult blockHit)) {
            return;
        }

        BlockPos clickedPos =
                blockHit.getBlockPos();

        /*
         * =====================================================
         * CURRENT LAYOUT
         * =====================================================
         *
         * IMPORTANT:
         *
         * We must read from the CURRENT selected layout.
         *
         * The old code used:
         *
         * PianoClient.CONFIG.noteBlocks
         *
         * which is only the old migration storage.
         */

        NoteBlockLayout layout =
                PianoClient.CONFIG
                        .getCurrentLayout();

        if (layout == null ||
                layout.noteBlocks == null) {
            return;
        }

        int clickedNote =
                -1;

        /*
         * Find which assigned note number
         * matches the block we clicked.
         *
         * Supports note numbers:
         *
         * 0 through 24.
         */
        for (Map.Entry<Integer, NoteBlockData> entry :
                layout.noteBlocks.entrySet()) {

            NoteBlockData data =
                    entry.getValue();

            if (data == null) {
                continue;
            }

            if (data.x == clickedPos.getX() &&
                    data.y == clickedPos.getY() &&
                    data.z == clickedPos.getZ()) {

                clickedNote =
                        entry.getKey();

                break;
            }
        }

        /*
         * -1 means this block isn't assigned
         * in the current layout.
         *
         * Note 0 is VALID, so do NOT use:
         *
         * clickedNote <= 0
         */
        if (clickedNote == -1) {
            return;
        }

        /*
         * Tell the song player which
         * note block was clicked.
         */
        PianoClient.PLAYER.clickNote(
                clickedNote
        );
    }
}
