package smalltalk.example;

import java.lang.reflect.Method;
import org.junit.Test;

/**
 * Confirms proper operation of the Bistro Smalltalk library code.
 * @author nik
 */
@SuppressWarnings("unchecked")
public class RuntimeTest {

    @Test
    public void examples() throws Exception {
        for (Class testClass : TestClasses) {
            runTest(testClass);
        }
    }

    private void runTest(Class testClass) throws Exception {
        System.out.println();
        System.out.println("running " + testClass.getName());

        String[] args = { };
        Class[] argTypes = { args.getClass() };
        Method m = testClass.getMethod("main", argTypes);
        m.invoke(null, (Object)args);
    }

    private static final Class[] TestClasses = {
        HelloWorld.class,
        SimpleHanoi.class,
        SticBenchmark.class,
        TestIntegers.class,
        TestMagnitudes.class,
        TestGeometry.class,
        TestCollections.class,
        TestStreams.class,
        TestLiterals.class,
        TestExceptions.class,
        TestSends.class,
        TestThreads.class,
    };
}
