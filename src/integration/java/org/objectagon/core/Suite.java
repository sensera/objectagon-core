package org.objectagon.core;

/**
 * Created by christian on 2016-01-06.
 */
public interface Suite {
    void setup(Server server);
    void createTests(IntegrationTests tests);

    interface RegisterSuite {
        void add(Suite suite);
    }
}
