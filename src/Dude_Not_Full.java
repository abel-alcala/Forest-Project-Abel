import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class Dude_Not_Full extends EntityAb implements AnimationEntity, ActivityEntity {
    private int resourceLimit;
    private int resourceCount;
    private double actionPeriod;
    private double animationPeriod;

    //private PathingStrategy strategy = new SingleStepPathingStrategy();
    private PathingStrategy strategy = new AStarPathingStrategy();

    public Dude_Not_Full(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod, double animationPeriod) {
        super(id, position, images, 0, 0);
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), this.getAnimationPeriod());
    }

    public double getAnimationPeriod() {
        return this.animationPeriod;
    }

    private boolean moveToNotFull(WorldModel world, Entity target, EventScheduler scheduler) {
        if (Functions.adjacent(this.getPosition(), target.getPosition())) {
            this.resourceCount += 1;
            target.setHealth(target.getHealth() - 1);
            return true;
        } else {
            Point nextPos = nextPositionDude(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<EntityAb> target = world.findNearest(this.getPosition(), new ArrayList<>(Arrays.asList(Tree.class, Sapling.class)));

        if (target.isEmpty() || !this.moveToNotFull(world, target.get(), scheduler) || !transformNotFull(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }

    private Point nextPositionDude(WorldModel world, Point destPos) {
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

    private boolean transformNotFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.resourceCount >= this.resourceLimit) {
            Dude_Full dude = Functions.createDudeFull(this.getId(), this.getPosition(), this.actionPeriod, this.animationPeriod, this.resourceLimit, this.getImages());

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude);
            dude.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }
}
