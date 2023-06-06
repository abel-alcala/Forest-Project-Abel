/**
 * An action that can be taken by an entity
 */
public final class Animation implements Action{
    private final AnimationEntity entity;
    private final int repeatCount;

    public Animation(AnimationEntity entity,int repeatCount) {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }
    public void executeAction(EventScheduler scheduler) {
        entity.nextImage();

        if (repeatCount != 1) {
            scheduler.scheduleEvent((Entity) entity, Functions.createAnimationAction(entity, Math.max(repeatCount - 1, 0)), entity.getAnimationPeriod());
        }
    }
}
