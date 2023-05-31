#!/bin/bash

mvn clean test -Dtest=BVTSuite -Dspring.profiles.active=test -Dqe.user=info@clarolab.com -Dqe.pass=123123

# check status
STATUS=$?

echo "Checking out test report directory"
echo "**********************************"
find ~/repo/target/surefire-reports -type f -printf "%f\n"
echo

echo "Performing file change name to junit-results.xml"
cp ~/repo/target/surefire-reports/TEST-*.xml ~/repo/target/surefire-reports/junit-results.xml
echo

echo "Checking out test report directory"
echo "**********************************"
find ~/repo/target/surefire-reports -type f -printf "%f\n"
echo

echo "exit $STATUS"
exit $STATUS