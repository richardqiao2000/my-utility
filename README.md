## Build
```shell
# in exe folder, run
./build.sh
```
## Package
```shell
# package exe jar
./package.sh

# package code, in code root directory
tar -cvf richardqiao-practice-code.tar -T README.md pom.xml exe/* src/*
```
## UnPackage
```shell
# unpack code jar
mkdir richardqiao-practice-code
tar -xzf richardqiao-practice-code.tar -C richardqiao-practice-code
```
## Run
```shell
./run.sh test.txt
```
