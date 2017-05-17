#!/bin/bash

VERSION=1.0.1

time java -jar target/spammer-$VERSION-standalone.jar 10;
time java -jar target/spammer-$VERSION-standalone.jar 100;
time java -jar target/spammer-$VERSION-standalone.jar 1000;
time java -jar target/spammer-$VERSION-standalone.jar 10000;
time java -jar target/spammer-$VERSION-standalone.jar 100000;
time java -jar target/spammer-$VERSION-standalone.jar 1000000;


