package legacy.observers.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface ModWorld {

	public static final Direction[] UPDATE_ORDER = {
		Direction.WEST,
		Direction.EAST,
		Direction.DOWN,
		Direction.UP,
		Direction.NORTH,
		Direction.SOUTH
	};

	void onBlockChanged(BlockPos pos, Block block, boolean updateObservers);

	void updateNeighbors(BlockPos pos, Block block, boolean updateObservers);

	void updateObservers(BlockPos pos, Block block);

	void updateObserver(BlockPos pos, Block neighborBlock, BlockPos neighborPos);

}
