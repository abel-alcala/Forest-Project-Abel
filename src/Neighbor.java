public class Neighbor implements Comparable<Neighbor> {
    private Point position;
    private Neighbor priorNeighbor;
    private int gScore;
    private int hScore;

    public Neighbor(Point position, Neighbor priorNeighbor, int gScore, int hScore) {
        this.position = position;
        this.priorNeighbor = priorNeighbor;
        this.gScore = gScore;
        this.hScore = hScore;
    }

    public Point getPosition() {
        return position;
    }

    public Neighbor getPriorNeighbor() {
        return priorNeighbor;
    }

    public int getHScore(){
        return hScore;
    }
    public int getGScore() {
        return gScore;
    }

    public int getFScore() {
        return gScore + hScore;
    }
    public int compareTo(Neighbor other) {
        return Integer.compare(this.getFScore(), other.getFScore());
    }
}
