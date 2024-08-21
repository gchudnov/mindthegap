# CONTRIBUTION

## Build

```bash
# build the project
sbt clean compile
```

## Code Coverage

```bash
# run coverage
# coverage reports will be saved in target/scala-<scala-version>/scoverage-report directory. 
sbt clean coverage test coverageReport

# run coverage for the single module
sbt coverage mtg/test mtg/coverageReport

# if run in the sbt cli
set coverageEnabled := false; clean; coverage; test; coverageReport
```
