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
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Directions;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;

@Mixin(World.class)
public abstract class WorldMixin implements IWorld, ModWorld {

	@Shadow private boolean isClient;
	@Shadow private WorldData data;

	@Shadow private void updateBlock(int x, int y, int z, int neighborBlockId) { }

	@Redirect(
		method = "setBlockWithMetadata",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/World;onBlockChanged(IIII)V"
		)
	)
	private void notifyBlockChanged(World world, int x, int y, int z, int blockId) {
		onBlockChanged(x, y, z, blockId, true);
	}

	@Inject(
		method = "setBlockWithMetadata",
		at = @At(
			value = "RETURN"
		)
	)
	private void updateObserversOnBlockChange(int x, int y, int z, int blockId, int metadata, int flags, CallbackInfoReturnable<Boolean> cir) {
		if (!isClient && (flags & SetBlockFlags.SKIP_UPDATE_OBSERVERS) == 0 && cir.getReturnValue()) {
			updateObservers(x, y, z, blockId);
		}
	}

	@Redirect(
		method = "setBlockMetadata",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/World;onBlockChanged(IIII)V"
		)
	)
	private void notifyBlockMetadataChanged(World world, int x, int y, int z, int blockId) {
		onBlockChanged(x, y, z, blockId, true);
	}

	@Inject(
		method = "setBlockMetadata",
		at = @At(
			value = "RETURN"
		)
	)
	private void updateObserversOnBlockMetadataChange(int x, int y, int z, int metadata, int flags, CallbackInfoReturnable<Boolean> cir) {
		if (!isClient && (flags & SetBlockFlags.SKIP_UPDATE_OBSERVERS) == 0 && cir.getReturnValue()) {
			updateObservers(x, y, z, getBlock(x, y, z));
		}
	}

	@Inject(
		method = "onBlockChanged(IIII)V",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void onBlockChanged(int x, int y, int z, int blockId, CallbackInfo ci) {
		onBlockChanged(x, y, z, blockId, false);
		ci.cancel();
	}

	@Inject(
		method = "updateNeighbors",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void updateNeighbors(int x, int y, int z, int blockId, CallbackInfo ci) {
		updateNeighbors(x, y, z, blockId, false);
		ci.cancel();
	}

	@Override
	public void onBlockChanged(int x, int y, int z, int blockId, boolean updateObservers) {
		updateNeighbors(x, y, z, blockId, updateObservers);
	}

	@Override
	public void updateNeighbors(int x, int y, int z, int blockId, boolean updateObservers) {
		for (int dir : UPDATE_ORDER) {
			updateBlock(x + Directions.X_OFFSET[dir], y + Directions.Y_OFFSET[dir], z + Directions.Z_OFFSET[dir], blockId);
		}
		if (updateObservers) {
			updateObservers(x, y, z, blockId);
		}
	}

	@Override
	public void updateObservers(int x, int y, int z, int blockId) {
		for (int dir : UPDATE_ORDER) {
			updateObserver(x + Directions.X_OFFSET[dir], y + Directions.Y_OFFSET[dir], z + Directions.Z_OFFSET[dir], blockId, x, y, z);
		}
	}

	@Override
	public void updateObserver(int x, int y, int z, int neighborBlockId, int neighborX, int neighborY, int neighborZ) {
		if (isClient) {
			return;
		}

		int blockId = getBlock(x, y, z);

		if (blockId != ModBlocks.OBSERVER.id) {
			return;
		}

		try {
			ModBlocks.OBSERVER.update((World)(Object)this, x, y, z, neighborBlockId, neighborX, neighborY, neighborZ);
		} catch (Throwable t) {
			CrashReport report = CrashReport.of(t, "Exception while updating neighbors");
			CashReportCategory category = report.addCategory("Block being updated");
			category.add("Source block type", () -> {
				try {
					return String.format("ID #%d (%s // %s)", neighborBlockId, Block.BY_ID[neighborBlockId].getTranslationKey(),
						Block.BY_ID[neighborBlockId].getClass().getCanonicalName());
				} catch (Throwable throwable) {
					return "ID #" + neighborBlockId;
				}
			});
			CashReportCategory.addBlockDetails(category, x, y, z, blockId, getBlockMetadata(x, y, z));

			throw new CrashException(report);
		}
	}
}
