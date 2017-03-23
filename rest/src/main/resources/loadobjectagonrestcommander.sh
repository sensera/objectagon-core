#!/usr/bin/env bash
function objectagon() {
    if [ -z "$1" ]; then
        echo "Objectagon REST command"
        echo "  create transaction                            creates a transaction within the associated session"
        echo "  create class [alias]                          creates a class named with the optional alias"
        echo "  create instance [alias] [known class alias]   creates a instance named with the optional alias of class \"class alias\""
        kill -INT $$
    fi
    if [ -z "$OBJECTAGON_REST_URL" ]; then
        echo "env varible OBJECTAGON_REST_URL is not set"
        kill -INT $$
    fi
    TOKEN="--header OBJECTAGON_REST_TOKEN:1234567890"
    if [ -n "$OBJECTAGON_REST_TOKEN" ]; then
        TOKEN="--header OBJECTAGON_REST_TOKEN:$OBJECTAGON_REST_TOKEN"
    fi

    RESULT=""
    if [ "$1/$2" == "create/transaction" ] ; then
        echo "curl $TOKEN {$OBJECTAGON_REST_URL}transaction/"
        RESULT=$(curl -s $TOKEN {$OBJECTAGON_REST_URL}transaction/)
    fi

    if [ "$1/$2" == "create/class" ] ; then
        ALIAS=""
        if [ -n "$3" ]; then
            ALIAS="?alias=$3"
        fi
        echo "curl $TOKEN {$OBJECTAGON_REST_URL}class/$ALIAS"
        RESULT=$(curl -s -X PUT $TOKEN {$OBJECTAGON_REST_URL}class/$ALIAS)
    fi

    if [ "$1/$2" == "create/instance" ] ; then
        ALIAS=""
        if [ -n "$3" ]; then
            ALIAS="?alias=$3"
        fi
        CLASS=""
        if [ -n "$4" ]; then
            CLASS="$4"
        fi
        echo "curl $TOKEN {$OBJECTAGON_REST_URL}class/{$CLASS}/instance/$ALIAS"
        RESULT=$(curl -s -X PUT $TOKEN {$OBJECTAGON_REST_URL}class/{$CLASS}/instance/$ALIAS)
    fi

    if [ -n "$RESULT" ]; then
        echo "RESULT"
        echo $RESULT
    else
        echo -NO RESULT-
    fi
}

#curl -i -X PUT --header "OBJECTAGON_REST_TOKEN:1234567890" localhost:9999/class
# 2029  curl -v localhost:9999/transaction
# 2030  curl -v -i -X PUT --header "OBJECTAGON_REST_TOKEN:1234567890" localhost:9999/class
# 2031  curl -v localhost:9999/transaction
# 2032  curl -v -i -X PUT --header "OBJECTAGON_REST_TOKEN:1234567890" localhost:9999/class
# 2033  curl -v -i -X PUT --header "OBJECTAGON_REST_TOKEN:1234567890" localhost:9999/class?alias=Item
# 2034  curl -v localhost:9999/transaction
# 2035  curl -v -i -X PUT --header "OBJECTAGON_REST_TOKEN:1234567890" localhost:9999/class?alias=Item
# 2036  curl -v localhost:9999/transaction
# 2037  curl -v -i -X PUT --header "OBJECTAGON_REST_TOKEN:1234567890" localhost:9999/class?alias=Item
# 2038  curl -v -i -X PUT --header "OBJECTAGON_REST_TOKEN:1234567890" localhost:9999/class/Item/instance?alias=Item1
