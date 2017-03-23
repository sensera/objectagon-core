package org.objectagon.core.rest2.commander.impl;

import org.objectagon.core.rest2.commander.RestCommander;

/**
 * Created by christian on 2017-03-21.
 */
public class RestCommanderImpl implements RestCommander {


    @Override
    public CommandExec<LoginResult> login(String userName, String password) {
        return (params, restHttpCmd, success, fail) -> {
            restHttpCmd.call(
                    "POST",
                    "session/login/",
                    params,
                    (json) -> {
                        success.success(new LoginResult() {
                            @Override
                            public boolean success() {
                                return true;
                            }

                            @Override
                            public String token() {
                                return "987987897";
                            }
                        });
                    },
                    (errorCode,errorText) -> {
                        fail.fail(errorText, false);
                    });
        };
    }

    @Override
    public CommandExec createTransaction() {
        return null;
    }

    @Override
    public CommandExec createInstanceClass(String alias) {
        return null;
    }

    @Override
    public InstanceClass buildInstanceClassCommand(String alias) {
        return null;
    }
}
