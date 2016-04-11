package me.artspb.jmh.benchmark;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class InstanceOfIsInstanceBenchmark {

    @Param("100")
    private int count;

    @Param({"0.0", "0.1", "0.5", "0.9", "1.0"})
    private double bias;

    private Data[] data;

    @Setup
    public void prepare() {
        Random r = new Random(12345);
        List<Data> d = new ArrayList<Data>();
        for (int c = 0; c < count; c++) {
            d.add(new Data(c < bias * count ? 1 : 0));
        }
        Collections.shuffle(d, r);
        data = d.toArray(new Data[0]);
    }

    @Benchmark
    public void baseline() {
    }

    @Benchmark
    public void measureInstanceOf() {
        Data[] d = this.data;
        int c = this.count;
        for (int i = 0; i < c; i++) {
            d[i].instanceOf();
        }
    }

    @Benchmark
    public void measureIsInstance() {
        Data[] d = this.data;
        int c = this.count;
        for (int i = 0; i < c; i++) {
            d[i].isInstance();
        }
    }

    public class Foo {
    }

    public class Bar {
    }

    public class Data {

        private final Object o;

        public Data(int id) {
            o = getObject(id);
        }

        private Object getObject(int id) {
            switch (id) {
                case 0:
                    return new Foo();
                case 1:
                    return new Bar();
                default:
                    throw new IllegalStateException(Integer.toString(id));
            }
        }

        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        public boolean instanceOf() {
            return o instanceof Foo;
        }

        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        public boolean isInstance() {
            return Foo.class.isInstance(o);
        }
    }
}
