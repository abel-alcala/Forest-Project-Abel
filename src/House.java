import processing.core.PImage;

import java.util.List;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class House extends EntityAb implements ActivityEntity{
    private double actionPeriod;
    private static final String BROKEN_KEY = "housebroken";

    public House(String id, Point position, List<PImage> images, double actionPeriod, int health) {
        super(id, position, images, health, 0);
        this.actionPeriod = actionPeriod;
    }
    private boolean transformHouse(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        return transformTree(world, scheduler, imageStore);
    }
    private boolean transformTree(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.getHealth() <= 0) {
            Broken_House brokenHouse = Functions.createBroken(BROKEN_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList(BROKEN_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(brokenHouse);

            return true;
        }

        return false;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

        if (!this.transformHouse(world, scheduler, imageStore)) {

            scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
    }
}
