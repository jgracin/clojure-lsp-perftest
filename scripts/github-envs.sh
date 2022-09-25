#!/usr/bin/env bash

TODAY=`date +%Y-%m-%d`
BRANCH=master
PREVIOUS_COMMIT=`git rev-list -n 1 --first-parent --before="$TODAY 00:00" $BRANCH`
LATEST_COMMIT_TODAY=`git rev-list -n 1 --first-parent --since="$TODAY 00:00" $BRANCH`

if [ -z "$LATEST_COMMIT_TODAY" ]; then
    # no commits today, nevertheless we must run tests on the last
    # two commits (see README.md for the explanation)
    BASELINE_COMMIT=`git rev-list -n 2 --first-parent $BRANCH | tail -n1`
    MEASURED_COMMIT=`git rev-list -n 2 --first-parent $BRANCH | head -n1`
else
    BASELINE_COMMIT=$PREVIOUS_COMMIT
    MEASURED_COMMIT=$LATEST_COMMIT_TODAY
fi

if [ -n "$BASELINE_COMMIT" -a -n "$MEASURED_COMMIT" ]; then
    echo BASELINE_COMMIT=$BASELINE_COMMIT
    echo MEASURED_COMMIT=$MEASURED_COMMIT
else
    echo "ERROR_MSG=BASELINE_COMMIT or MEASURED_COMMIT empty"
    exit 1
fi
