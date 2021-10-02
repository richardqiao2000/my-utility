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
tar -cvf richardqiao-lyft-code.tar -T README.md pom.xml exe/* src/*
```
## UnPackage
```shell
# unpack code jar
mkdir richardqiao-lyft-code
tar -xzf richardqiao-lyft-code.tar -C richardqiao-lyft-code
```
## Run
```shell
./run.sh test.txt
```
