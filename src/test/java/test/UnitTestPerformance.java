package test;

import java.util.Date;
import java.util.function.Consumer;

/** A convenience class with testing methods that accept Lambda expressions */
public final class UnitTestPerformance<T> {

	private Consumer<T> consumer;
	
	public UnitTestPerformance(Consumer<T> consumer) {
		this.consumer = consumer;
	}
	
	public void run(String scenario, T input, long iterations, long runs) {

		System.out.print(scenario);
		long total = 0, r = 0;
		while (r < runs) {
			
			long i = iterations; ++r;
			long start = new Date().getTime();
			while (i > 0) {
				consumer.accept(input); --i;
			}
			long duration = new Date().getTime() - start;
			total += duration; System.out.print(" " + total/r);
		}
	}

}
