import java.io.*;

public class Trial implements Serializable {
    private final static long serialVersionUID = 1L;
    private int numOfLaunches = 0;

    public int getNumOfLaunches() {
        return numOfLaunches;
    }

    public void setNumOfLaunches(int numOfLaunches) {
        this.numOfLaunches = numOfLaunches;
    }
}
