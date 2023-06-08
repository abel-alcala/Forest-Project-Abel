import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class Mummy extends EntityAb implements AnimationEntity, ActivityEntity {
    private double actionPeriod;
    private double animationPeriod;
    private static final String MUMMY_KEY = "mummy";

    //private PathingStrategy strategy = new SingleStepPathingStrategy();
    private PathingStrategy strategy = new AStarPathingStrategy();

    public Mummy(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        super(id, position, images, 0, 0);
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    private boolean moveToMummy(WorldModel world, Entity target, EventScheduler scheduler) {
        if (Functions.adjacent(this.getPosition(), target.getPosition())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = nextPositionMummy(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<EntityAb> mummyTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(Dude_Not_Full.class, Dude_Full.class)));

        if (mummyTarget.isPresent()) {
            Point tgtPos = mummyTarget.get().getPosition();

            if (this.moveToMummy(world, mummyTarget.get(), scheduler)) {
                Mummy mummy = Functions.createMummy(MUMMY_KEY + "_" + mummyTarget.get().getId(), tgtPos, 0.55, 0.25, imageStore.getImageList(MUMMY_KEY));
                world.removeEntity(scheduler, mummyTarget.get());
                world.addEntity(mummy);
                mummy.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
    }

    private Point nextPositionMummy(WorldModel world, Point destPos) {
        Predicate<Point> canPassThrough = point ->
                world.withinBounds(point) && !world.isOccupied(point);

        BiPredicate<Point, Point> withinReach = Point::adjacent;

        Function<Point, Stream<Point>> potentialNeighbors = point ->
                Stream.of(
                        new Point(point.x + 1, point.y),
                        new Point(point.x - 1, point.y),
                        new Point(point.x, point.y + 1),
                        new Point(point.x, point.y - 1)
                );

        List<Point> path = strategy.computePath(
                this.getPosition(),
                destPos,
                canPassThrough,
                withinReach,
                potentialNeighbors
        );

        if (!path.isEmpty()) {
            return path.get(0);
        }

        return this.getPosition();
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), this.getAnimationPeriod());
    }

    public double getAnimationPeriod() {
        return animationPeriod;
    }
}
