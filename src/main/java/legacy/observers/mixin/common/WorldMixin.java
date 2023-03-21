package legacy.observers.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import legacy.observers.block.ModBlocks;
import legacy.observers.world.ModWorld;
import legacy.observers.world.SetBlockFlags;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import net.minecraft.world.gen.WorldGeneratorType;

@Mixin(World.class)
public abstract class WorldMixin implements IWorld, ModWorld {

	@Shadow private boolean isClient;
	@Shadow private WorldData data;

	@Shadow private void updateBlock(BlockPos pos, Block neighborBlock) { }

	@Redirect(
		method = "setBlockState",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/World;onBlockChanged(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V"
		)
	)
	private void notifyBlockChanged(World world, BlockPos pos, Block block) {
		onBlockChanged(pos, block, true);
	}

	@Inject(
		method = "setBlockState",
		at = @At(
			value = "RETURN"
		)
	)
	private void updateObserversOnBlockChange(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<Boolean> cir) {
		if (!isClient && (flags & SetBlockFlags.SKIP_UPDATE_OBSERVERS) == 0 && cir.getReturnValue()) {
			updateObservers(pos, state.getBlock());
		}
	}

	@Inject(
		method = "onBlockChanged(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void onBlockChanged(BlockPos pos, Block block, CallbackInfo ci) {
		onBlockChanged(pos, block, false);
		ci.cancel();
	}

	@Inject(
		method = "updateNeighbors",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void updateNeighbors(BlockPos pos, Block block, CallbackInfo ci) {
		updateNeighbors(pos, block, false);
		ci.cancel();
	}

	@Override
	public void onBlockChanged(BlockPos pos, Block block, boolean updateObservers) {
		if (data.getGeneratorType() != WorldGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			updateNeighbors(pos, block, updateObservers);
		}
	}

	@Override
	public void updateNeighbors(BlockPos pos, Block block, boolean updateObservers) {
		for (Direction dir : UPDATE_ORDER) {
			updateBlock(pos.offset(dir), block);
		}
		if (updateObservers) {
			updateObservers(pos, block);
		}
	}

	@Override
	public void updateObservers(BlockPos pos, Block block) {
		for (Direction dir : UPDATE_ORDER) {
			updateObserver(pos.offset(dir), block, pos);
		}
	}

	@Override
	public void updateObserver(BlockPos pos, Block neighborBlock, BlockPos neighborPos) {
		if (isClient) {
			return;
		}

		BlockState state = getBlockState(pos);

		if (state.getBlock() != ModBlocks.OBSERVER) {
			return;
		}

		try {
			ModBlocks.OBSERVER.update(state, (World)(Object)this, pos, neighborBlock, neighborPos);
		} catch (Throwable t) {
			CrashReport report = CrashReport.of(t, "Exception while updating neighbors");
			CashReportCategory category = report.addCategory("Block being updated");
			category.add("Source block type", () -> {
				try {
					return String.format("ID #%d (%s // %s)", Block.getId(neighborBlock), neighborBlock.getTranslationKey(),
						neighborBlock.getClass().getCanonicalName());
				} catch (Throwable throwable) {
					return "ID #" + Block.getId(neighborBlock);
				}
			});
			CashReportCategory.addBlockDetails(category, pos, state);

			throw new CrashException(report);
		}
	}
}
