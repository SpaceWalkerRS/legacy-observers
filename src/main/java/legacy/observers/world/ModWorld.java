package legacy.observers.world;

public interface ModWorld {

	public static final int[] UPDATE_ORDER = {
		4, // west
		5, // east
		0, // down
		1, // up
		2, // north
		3  // south
	};

	void onBlockChanged(int x, int y, int z, int blockId, boolean updateObservers);

	void updateNeighbors(int x, int y, int z, int blockId, boolean updateObservers);

	void updateObservers(int x, int y, int z, int blockId);

	void updateObserver(int x, int y, int z, int neighborBlockId, int neighborX, int neighborY, int neighborZ);

}
