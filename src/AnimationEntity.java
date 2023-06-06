import processing.core.PImage;

import java.util.List;

public interface AnimationEntity {
    double getAnimationPeriod();
    int getImageIndex();
    void setImageIndex(int currentIndex);

    default void nextImage(){
        setImageIndex(getImageIndex() + 1);
    }
    List<PImage> getImages();

}
