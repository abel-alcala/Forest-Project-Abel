import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public abstract class EntityAb implements Entity{
    private String id;
    private Point position;
    private int health;
    private List<PImage> images;
    private int imageIndex;

    public EntityAb(String id, Point position, List<PImage> images, int health, int imageIndex) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.health = health;
        this.imageIndex = imageIndex;
    }
    public String getId() {
        return this.id;
    }
    public Point getPosition() {
        return this.position;
    }
    public void setPosition(Point pos)
    {
        this.position = pos;
    }
    public List<PImage> getImages() {
        return images;
    }
    public int getImageIndex() {
        return imageIndex;
    }
    public void setImageIndex(int currentIndex) {
        this.imageIndex = currentIndex;
    }
    public int getHealth() {
        return health;
    }
    public void setHealth(int i){
        health = i;
    }
    public abstract void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);
    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }
}
