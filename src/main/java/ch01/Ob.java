package ch01;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("deprecation")
public class Ob {

  // Iterable  <--> Observable

  /**
   * Iterable<Integer> iter = () ->
   *         new Iterator<Integer>() {
   *           int i = 0;
   *           static final int MAX = 10;
   *
   *           public boolean hasNext() {
   *             return i < MAX;
   *           }
   *
   *           public Integer next() {
   *             return ++i;
   *           }
   *         };
   *
   *     for (Integer i : iter) {
   *       System.out.println(i);
   *     }
   */

  static class IntObservable extends Observable implements Runnable {

    @Override
    public void run() {
      for (int i = 0; i <= 10; i++) {
        setChanged();
        notifyObservers(i);
      }
    }
  }

  public static void main(String[] args) {
    // iterable은 pull 방식: DATA method(void)
    // observer는 push 방식: method(DATA)
    Observer ob = (o, arg) -> {
      System.out.println(Thread.currentThread().getName() + " " + arg);
    };

    IntObservable io = new IntObservable();
    io.addObserver(ob);

    ExecutorService es = Executors.newSingleThreadExecutor();
    es.execute(io);

    System.out.println(Thread.currentThread().getName() + " EXIT");
    es.shutdown();
  }
}
