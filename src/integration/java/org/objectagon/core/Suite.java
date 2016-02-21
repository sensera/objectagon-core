package org.objectagon.core;

/**
 * Created by christian on 2016-01-06.
 */
public interface Suite {
    void setup(Setup setup);
    void createTests(IntegrationTests tests);

    interface RegisterSuite {
        void add(Suite suite);
    }

    interface Setup {
        void registerAtServer(RegisterAtServer registerAtServer);
    }

    @FunctionalInterface
    interface RegisterAtServer {
        void register(Server server);
    }


}
