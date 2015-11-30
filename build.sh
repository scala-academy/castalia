#!/bin/bash

set -ev
sbt clean coverage test it:test
sbt coverageReport
sbt coverageAggregate
sbt codacyCoverage
