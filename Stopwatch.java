public class Stopwatch { 

    private long start;

    /**
     * Initializes a new stopwatch.
     */
    public Stopwatch() {
        start = 0;
    } 
    
    
    /**
     *  Starts the stopwatch.
     */
    public void start(){
    	start = System.currentTimeMillis();
    }

    
    /**
     * Returns the elapsed CPU time (in seconds) since the stopwatch was created.
     *
     * @return elapsed CPU time (in seconds) since the stopwatch was created
     */
    public double elapsedTime() {
        long now = System.currentTimeMillis();
        return (now - start) / 1000.0;
    }
}
