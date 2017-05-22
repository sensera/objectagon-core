#!/usr/bin/env bash

function objectagon() {
    if [ -z "$1" ]; then
        red=`tput setaf 1`
        green=`tput setaf 2`
        reset=`tput sgr0`
        echo "Objectagon REST command"
        echo "  ${green}create transaction${reset}"
        echo "      creates a transaction within the associated session"
        echo "  ${green}create class [alias]${reset}"
        echo "      creates a class named with alias"
        echo "  ${green}set name [name] of class [class alias]${reset}"
        echo "      set the name of a class"
        echo "  ${green}add field [new field alias] to [class alias]${reset}"
        echo "      adds field to class"
        echo "  ${green}add relation class [from new relation class alias] to [to class alias] of [class alias]${reset}"
        echo "      adds relation to class"
        echo "  ${green}create instance [alias] of [known class alias]${reset}"
        echo "      creates a instance named with the optional alias of class \"class alias\""
        echo "  ${green}add relation [from instance alias] to [instance alias] of [relation class alias]${reset}"
        echo "      adds a relation from between to instances"
        echo "${reset}"
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
        RESULT=$(curl -s -X PUT $TOKEN {$OBJECTAGON_REST_URL}transaction/)
    fi

    if [ "$1/$2" == "create/class" ] ; then
        ALIAS=""
        if [ -n "$3" ]; then
            ALIAS="?alias=$3"
        fi
        echo "curl $TOKEN {$OBJECTAGON_REST_URL}class/$ALIAS"
        RESULT=$(curl -s -X PUT $TOKEN {$OBJECTAGON_REST_URL}class/$ALIAS)
    fi

    if [ "$1/$2/$4/$5" == "set/name/of/class" ] ; then
        echo "curl $TOKEN {$OBJECTAGON_REST_URL}class/$6/name/$3/"
        RESULT=$(curl -s -X POST $TOKEN {$OBJECTAGON_REST_URL}class/$6/name/$3/)
    fi

    if [ "$1/$2/$4" == "add/field/to" ] ; then
        ALIAS=""
        if [ -n "$3" ]; then
            ALIAS="?alias=$3"
        fi
        echo "curl $TOKEN {$OBJECTAGON_REST_URL}class/$5/field/$ALIAS"
        RESULT=$(curl -s -X PUT $TOKEN {$OBJECTAGON_REST_URL}class/$5/field/$ALIAS)
    fi

    # add relation class [from new relation class alias] to [to class alias] named [relation class alias] of [class alias]
    if [ "$1/$2/$3/$5/$7" == "add/relation/class/to/of" ] ; then
        ALIAS=""
        if [ -n "$4" ]; then
            ALIAS="?alias=$4"
        fi
        echo "curl $TOKEN {$OBJECTAGON_REST_URL}class/$8/relation/$6/$ALIAS"
        RESULT=$(curl -s -X PUT $TOKEN {$OBJECTAGON_REST_URL}class/$8/relation/$6/$ALIAS)
        #/class/{id}/relation/{id}/
    fi

    # add relation [from instance alias] to [instance alias] of [relation class alias]
    if [ "$1/$2/$4/$6" == "add/relation/to/of" ] ; then
        echo "curl $TOKEN {$OBJECTAGON_REST_URL}instance/$3/relation/$7/$5/"
        RESULT=$(curl -s -X PUT $TOKEN {$OBJECTAGON_REST_URL}instance/$3/relation/$7/$5/)
        #/instance/{id}/relation/{id}/{id}/
    fi

    if [ "$1/$2/$4" == "create/instance/of" ] ; then
        ALIAS=""
        if [ -n "$3" ]; then
            ALIAS="?alias=$3"
        fi
        CLASS=""
        if [ -n "$5" ]; then
            CLASS="$5"
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

export -f objectagon

