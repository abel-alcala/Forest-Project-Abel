import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

class AStarPathingStrategy
        implements PathingStrategy
{
        public List<Point> computePath(Point start, Point end,
                                       Predicate<Point> canPassThrough,
                                       BiPredicate<Point, Point> withinReach,
                                       Function<Point, Stream<Point>> potentialNeighbors) {
            PriorityQueue<Neighbor> openList = new PriorityQueue<>();
            Map<Point, Neighbor> closedList = new HashMap<>();
            Neighbor startNode = new Neighbor(start, null, 0, heuristic(start, end));
            openList.add(startNode);
            closedList.put(start, startNode);
            while (!openList.isEmpty()) {
                Neighbor current = openList.poll();
                if (withinReach.test(current.getPosition(), end))
                    return constructPath(current);
                potentialNeighbors.apply(current.getPosition())
                        .filter(canPassThrough)
                        .forEach(neighbor -> {
                            int gScore = current.getGScore() + 1;
                            int hScore = heuristic(neighbor, end);
                            int fScore = gScore + hScore;
                            Neighbor neighborNode = closedList.getOrDefault(neighbor, null);
                            if (neighborNode == null || fScore < neighborNode.getFScore()) {
                                if (neighborNode != null) {
                                    openList.remove(neighborNode);
                                }
                                Neighbor newNode = new Neighbor(neighbor, current, gScore, hScore);
                                openList.add(newNode);
                                closedList.put(neighbor, newNode);
                            }
                        });
            }
            return new ArrayList<Point>();
        }
        private int heuristic(Point point, Point goal) {
            return Math.abs(goal.x - point.x) + Math.abs(goal.y - point.y);
        }
        private List<Point> constructPath(Neighbor endNode) {
            List<Point> path = new ArrayList<>();
            Neighbor current = endNode;
            while (current.getPriorNeighbor() != null) {
                path.add(current.getPosition());
                current = current.getPriorNeighbor();
            }
            Collections.reverse(path);
            return path;
        }


    }
