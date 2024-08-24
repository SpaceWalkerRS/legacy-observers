package legacy.observers.world;

public class ModGamerules {

	public static final String NOTE_BLOCKS_TRIGGER_OBSERVERS = "noteBlocksTriggerObservers";

	// fast access that is updated whenever the gamerule is changed
	// can speed things up a lot since this check could be run a lot
	public static boolean noteBlocksTriggerObservers;

}
