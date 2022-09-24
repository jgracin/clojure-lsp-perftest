#!/usr/bin/env bash

TODAY=`date +%Y-%m-%d`
BRANCH=master
PREVIOUS_COMMIT=`git rev-list -n 1 --first-parent --before="$TODAY 00:00" $BRANCH`
LATEST_COMMIT_TODAY=`git rev-list -n 1 --first-parent --since="$TODAY 00:00" $BRANCH`

if [ -z "$LATEST_COMMIT_TODAY" ]; then
    # no commits today, nevertheless we must run tests on the last
    # two commits (see README.md for the explanation)
    echo A=1
    echo BASELINE_COMMIT=`git rev-list -n 2 --first-parent $BRANCH | tail -n1`
    echo MEASURED_COMMIT=`git rev-list -n 2 --first-parent $BRANCH | head -n1`
else
    echo B=1
    echo BASELINE_COMMIT=$PREVIOUS_COMMIT
    echo MEASURED_COMMIT=$LATEST_COMMIT_TODAY
fi