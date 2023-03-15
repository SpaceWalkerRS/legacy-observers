package legacy.observers.world;

import net.minecraft.block.Block;

public interface ModWorld {

	public static final int[] UPDATE_ORDER = {
		4, // west
		5, // east
		0, // down
		1, // up
		2, // north
		3  // south
	};

	void onBlockChanged(int x, int y, int z, Block block, boolean updateObservers);

	void updateNeighbors(int x, int y, int z, Block block, boolean updateObservers);

	void updateObservers(int x, int y, int z, Block block);

	void updateObserver(int x, int y, int z, Block neighborBlock, int neighborX, int neighborY, int neighborZ);

}
