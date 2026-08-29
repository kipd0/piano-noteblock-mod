package name.modid.client.mixin;

import name.modid.NoteBlockData;
import name.modid.PianoClient;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
         * Only do anything while
         * a song is playing.
         */
        if (!PianoClient.PLAYER.isPlaying()) {
            return;
        }

        /*
         * Make sure the player is
         * actually looking at a block.
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
         * GET THE NOTE THE SONG EXPECTS
         * =====================================================
         */

        int expectedNote =
                PianoClient.PLAYER
                        .getCurrentNote();

        /*
         * -1 means there is no current note.
         *
         * Note 0 is valid.
         */
        if (expectedNote < 0 ||
                expectedNote > 24) {
            return;
        }

        /*
         * =====================================================
         * GET THAT NOTE'S ASSIGNED BLOCK
         * =====================================================
         *
         * This automatically uses the
         * currently selected layout.
         */

        NoteBlockData expectedBlock =
                PianoClient.CONFIG
                        .getNoteBlock(
                                expectedNote
                        );

        if (expectedBlock == null) {
            return;
        }

        /*
         * =====================================================
         * CHECK THE CLICKED POSITION
         * =====================================================
         *
         * We DON'T search every assignment anymore.
         *
         * We only check:
         *
         * "Did the player click the block belonging
         *  to the CURRENT expected note?"
         *
         * This avoids duplicate-coordinate problems.
         */

        if (clickedPos.getX() != expectedBlock.x ||
                clickedPos.getY() != expectedBlock.y ||
                clickedPos.getZ() != expectedBlock.z) {

            /*
             * Wrong block.
             */
            return;
        }

        /*
         * =====================================================
         * CORRECT BLOCK
         * =====================================================
         */

        PianoClient.PLAYER.clickNote(
                expectedNote
        );
    }
}
