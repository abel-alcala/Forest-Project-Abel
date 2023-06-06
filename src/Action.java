/**
 * An action that can be taken by an entity
 */
public interface Action {
    abstract void executeAction(EventScheduler scheduler);
}
