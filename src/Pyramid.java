import processing.core.PImage;

import java.util.List;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Pyramid extends EntityAb{
    public Pyramid(String id, Point position, List<PImage> images) {
        super(id, position, images, 0, 0);
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        return;
    }
}
