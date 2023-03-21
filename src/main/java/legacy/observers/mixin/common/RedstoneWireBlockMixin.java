package legacy.observers.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import legacy.observers.block.ModBlocks;
import legacy.observers.block.ObserverBlock;

import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.world.IWorld;

@Mixin(RedstoneWireBlock.class)
public class RedstoneWireBlockMixin {

	@Inject(
		method = "shouldConnectTo(Lnet/minecraft/world/IWorld;IIII)Z",
		locals = LocalCapture.CAPTURE_FAILHARD,
		cancellable = true,
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/block/Block;REDSTONE_WIRE:Lnet/minecraft/block/RedstoneWireBlock;"
		)
	)
	private static void shouldConnectTo(IWorld world, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir, int neighborBlockId) {
		if (neighborBlockId == ModBlocks.OBSERVER.id) {
			cir.setReturnValue(side == ObserverBlock.getFacing(world.getBlockMetadata(x, y, z)));
		}
	}
}
