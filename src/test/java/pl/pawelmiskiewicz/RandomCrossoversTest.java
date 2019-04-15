package pl.pawelmiskiewicz;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;

public class RandomCrossoversTest {
    private Random rand = new Random();

    @Test
    public void shouldGenerateTwoOneAfterAnother() {
        AtomicInteger equals = new AtomicInteger(0);
        IntStream.range(0, 10)
                .forEach(ij -> {
                    IntStream.range(0, 1000)
                            .forEach(i -> {
                                int[] parent1 = new int[rand.nextInt(i + 50) + 1];

                                int xoverpoint = rand.nextInt(parent1.length);
                                int xoverpoint2 = Math.min(xoverpoint + rand.nextInt(parent1.length - xoverpoint) + 1, parent1.length - 1);

                                assertTrue(xoverpoint < parent1.length);
                                assertTrue(xoverpoint2 >= xoverpoint);
                                assertTrue(xoverpoint2 < parent1.length);

                                if(xoverpoint == xoverpoint2)
                                    equals.incrementAndGet();

                                //System.out.format("%d : %d\n", xoverpoint, xoverpoint2);
                            });
                });


        System.out.format("%d/%d, %d\n", equals.get(), 1000 * 10, equals.get()/1000 * 10);
    }
}
