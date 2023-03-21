package legacy.observers.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import legacy.observers.block.ObserverBlock;

import net.minecraft.block.Block;
import net.minecraft.client.render.BlockRenderer;
import net.minecraft.world.IWorld;

@Mixin(BlockRenderer.class)
public class BlockRendererMixin {

	@Shadow private IWorld world;
	@Shadow private int northFaceRotation;
	@Shadow private int southFaceRotation;
	@Shadow private int eastFaceRotation;
	@Shadow private int westFaceRotation;
	@Shadow private int topFaceRotation;
	@Shadow private int bottomFaceRotation;

	@Shadow private boolean renderSimpleBlock(Block block, int x, int y, int z) { return false; }

	@Inject(
		method = "renderBlock(Lnet/minecraft/block/Block;III)Z",
		cancellable = true,
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/client/render/BlockRenderer;prepare(Lnet/minecraft/block/Block;)V"
		)
	)
	private void renderObserverBlock(Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
		if (block.getRenderType() == ObserverBlock.RENDER_TYPE) {
			cir.setReturnValue(renderObserver(block, x, y, z));
		}
	}

	@ModifyVariable(
		method = "renderBlockItem",
		index = 2,
		argsOnly = true,
		at = @At(
			value = "HEAD"
		)
	)
	private int modifyObserverItemMetadata(int metadata, Block block, int _metadata, float tickDelta) {
		if (block.getRenderType() == ObserverBlock.RENDER_TYPE) {
			metadata = ObserverBlock.setFacing(metadata, 3);
		}

		return metadata;
	}

	@Inject(
		method = "renderBlockItem",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/Block;getRenderType()I"
		)
	)
	private void beforeGetObserverRenderType(Block block, int metadata, float tickDelta, CallbackInfo ci) {
		ObserverBlock.defaultRenderType = true;
	}

	@Inject(
		method = "renderBlockItem",
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/block/Block;getRenderType()I"
		)
	)
	private void afterGetObserverRenderType(Block block, int metadata, float tickDelta, CallbackInfo ci) {
		ObserverBlock.defaultRenderType = false;
	}

	@Inject(
		method = "isGui3D(I)Z",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private static void isGui3D(int type, CallbackInfoReturnable<Boolean> cir) {
		if (type == ObserverBlock.RENDER_TYPE) {
			cir.setReturnValue(true);
		}
	}

	private boolean renderObserver(Block block, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int facing = ObserverBlock.getFacing(metadata);

		switch (facing) {
		case 0: // down
			eastFaceRotation = 1;
			westFaceRotation = 1;

			break;
		case 1: // up
			eastFaceRotation = 2;
			westFaceRotation = 2;
			northFaceRotation = 3;
			southFaceRotation = 3;
			topFaceRotation = 3;
			bottomFaceRotation = 3;

			break;
		case 2: // north
			topFaceRotation = 3;
			bottomFaceRotation = 3;

			break;
		case 3: // south

			break;
		case 4: // west
			topFaceRotation = 1;
			bottomFaceRotation = 1;

			break;
		case 5: // up
			topFaceRotation = 2;
			bottomFaceRotation = 2;

			break;
		}

		boolean success = renderSimpleBlock(block, x, y, z);

		northFaceRotation = 0;
		southFaceRotation = 0;
		eastFaceRotation = 0;
		westFaceRotation = 0;
		topFaceRotation = 0;
		bottomFaceRotation = 0;

		return success;
	}
}
