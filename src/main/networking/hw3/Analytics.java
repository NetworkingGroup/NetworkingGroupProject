package networking.hw3;


import java.util.concurrent.*;

/**
 * This class returns the result number from the youtube analytics for each month
 */
public class Analytics {

    private static final ExecutorService threadpool = Executors.newFixedThreadPool(3);

    static Future future;

    /**
     * Gets a task from the youtube api, which is worked on over time.
     * The task may be interrupted by other clients or canceled. All
     * over which is handled.
     * @param index Index of the chunk that is it going to be analyzed
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public long analyze (int index) throws ExecutionException, InterruptedException {
        YoutubeTask task = new YoutubeTask(index);
        future = threadpool.submit(task);
        long viewCount;
        try {
            viewCount = (long) future.get();
        } catch (final InterruptedException ex) {
            System.out.println(ex);
            return -1;
        } catch (final ExecutionException ex) {
            System.out.println(ex);
            return -1;
        } catch(CancellationException ce){
            System.out.println(ce);
            return -1;
        }
        return viewCount;
    }

    /**
     * A runnable class to launch the query and return a result.
     */
    private class YoutubeTask implements Callable {

        int index = 0;
        Query query = new Query();

        /**
         * Calls a query on the youtube api analytics and gets back a view count.
         * @param index Position of the chunk in the set that is being worked on
         */
        public YoutubeTask(int index) {
            this.index = index;
        }

        @Override
        public Object call() throws Exception {
            return query.countViews(index);
        }
    }
}