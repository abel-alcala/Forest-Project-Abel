import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class Fairy extends EntityAb implements AnimationEntity, ActivityEntity {
    private double actionPeriod;
    private double animationPeriod;
    private static final String SAPLING_KEY = "sapling";

    //private PathingStrategy strategy = new SingleStepPathingStrategy();
    private PathingStrategy strategy = new AStarPathingStrategy();

    public Fairy(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        super(id, position, images, 0, 0);
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    private boolean moveToFairy(WorldModel world, Entity target, EventScheduler scheduler) {
        if (Functions.adjacent(this.getPosition(), target.getPosition())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = nextPositionFairy(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<EntityAb> fairyTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(Stump.class)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (this.moveToFairy(world, fairyTarget.get(), scheduler)) {
                Sapling sapling = Functions.createSapling(SAPLING_KEY + "_" + fairyTarget.get().getId(), tgtPos, imageStore.getImageList(this.SAPLING_KEY), 0);
                world.addEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
    }

    private Point nextPositionFairy(WorldModel world, Point destPos) {
        Predicate<Point> canPassThrough = point ->
                world.withinBounds(point) &&
                        (!world.isOccupied(point));


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
