
public class GameLoop {
    private boolean runFlag = false;
    private Game game;

    public GameLoop(Game game) {
        this.game = game;
    }

    /**
     * Begin the game loop
     *
     * @param delta time between logic updates (in seconds)
     */
    public void run(double delta) {
        runFlag = true;

        startup();
        // convert the time to seconds
        double nextTime = (double) System.nanoTime() / 1000000000.0;
        double maxTimeDiff = 0.5;
        int skippedFrames = 1;
        int maxSkippedFrames = 5;
        while (runFlag) {
            // convert the time to seconds
            double currTime = (double) System.nanoTime() / 1000000000.0;
            if ((currTime - nextTime) > maxTimeDiff) nextTime = currTime;
            if (currTime >= nextTime) {
                // assign the time for the next update
                nextTime += delta;
                update();
                if ((currTime < nextTime) || (skippedFrames > maxSkippedFrames)) {
                    draw();
                    skippedFrames = 1;
                } else {
                    skippedFrames++;
                }
            } else {
                // calculate the time to sleep
                int sleepTime = (int) (1000.0 * (nextTime - currTime));
                // sanity check
                if (sleepTime > 0) {
                    // sleep until the next update
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                }
            }
        }
        shutdown();
    }

    public boolean isRunning() {
        return runFlag;
    }

    public void stop() {
        runFlag = false;
    }

    public void startup() {
        game.reset();
    }

    public void shutdown() {
    }

    public void update() {
        game.update();
    }

    public void draw() {
        game.draw();
    }
}
