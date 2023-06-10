import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class Vulture_Full extends EntityAb implements AnimationEntity, ActivityEntity {
    private int resourceLimit;
    private double actionPeriod;
    private double animationPeriod;

    //private PathingStrategy strategy = new SingleStepPathingStrategy();
    private PathingStrategy strategy = new AStarPathingStrategy();

    public Vulture_Full(String id, Point position, List<PImage> images, int resourceLimit, double actionPeriod, double animationPeriod, int health) {
        super(id, position, images, health, 0);
        this.resourceLimit = resourceLimit;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public double getAnimationPeriod() {
        return this.animationPeriod;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), this.getAnimationPeriod());
    }

    private boolean moveToFull(WorldModel world, Entity target, EventScheduler scheduler) {
        if (Functions.adjacent(this.getPosition(), target.getPosition())) {
            return true;
        } else {
            Point nextPos = nextPositionVulture(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<EntityAb> fullTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(Pyramid.class)));

        if (fullTarget.isPresent() && this.moveToFull(world, fullTarget.get(), scheduler)) {
            transformFull(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }

    private Point nextPositionVulture(WorldModel world, Point destPos) {
        Predicate<Point> canPassThrough = point ->
                world.withinBounds(point) &&
                        (!world.isOccupied(point) || world.getOccupancyCell(point) instanceof Stump || world.getOccupancyCell(point) instanceof Sapling);


        BiPredicate<Point, Point> withinReach = (point1, point2) -> point1.adjacent(point2);

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

    private void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Vulture_Not_Full vulture = Functions.createVultureNotFull(this.getId(), this.getPosition(), this.actionPeriod, this.animationPeriod, this.resourceLimit, this.getImages());

        world.removeEntity(scheduler, this);

        world.addEntity(vulture);
        vulture.scheduleActions(scheduler, world, imageStore);
    }
}
