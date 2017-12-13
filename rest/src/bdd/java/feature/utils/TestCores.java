package feature.utils;

import org.objectagon.core.rest2.RestServer;
import org.objectagon.core.server.LocalServerId;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TestCores {
    private int portCounter = 9900;
    private Map<String,TestCore> cores = new HashMap<>();
    private Map<String, Supplier<TestCore>> coreSuppliers = new HashMap<>();

    public Supplier<TestCore> get(String name) {
        synchronized (cores) {
            final TestCore testCore = cores.get(name);
            if (testCore != null) {
                return () -> testCore;
            }
            final Supplier<TestCore> coreSupplier = coreSuppliers.get(name);
            if (testCore != null) {
                return coreSupplier;
            }
            return new TestCoreSupplier(name, portCounter++);
        }
    }

    public void stop(String name) {
        synchronized (cores) {
            cores.remove(name);
        }
    }

    private class TestCoreSupplier implements Supplier<TestCore> {
        private String name;
        private int port;

        public TestCoreSupplier(String name, int port) {
            this.name = name;
            this.port = port;
        }

        @Override public synchronized TestCore get() {
            RestServer restServer = new RestServer(LocalServerId.local("test.server."+name), port);
            TestCore testCore = new TestCore(name, restServer);
            synchronized (cores) {
                cores.put(name, testCore);
            }
            return testCore;
        }
    }
}
