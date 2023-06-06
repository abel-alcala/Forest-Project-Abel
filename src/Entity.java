import processing.core.PImage;

import java.util.List;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public interface Entity {
    String getId();
    Point getPosition();
    void setPosition(Point pos);
    int getHealth();
    void setHealth(int i);
    int getImageIndex();
    default PImage getCurrentImage() {
        return getImages().get(getImageIndex() % getImages().size());
    }
    List<PImage> getImages();
    String log();
}
