# CLAUDE.md - AI Assistant Guide for Mind the Gap

## Project Overview

**Mind the Gap** is a Scala library for working with intervals, relations, and algorithms. It implements Allen's Interval Algebra and provides comprehensive interval operations, including intersection, union, span, gap detection, grouping, and diagram generation.

- **Language**: Scala 3.3.4
- **Build Tool**: sbt (Scala Build Tool)
- **License**: MIT
- **Repository**: https://github.com/gchudnov/mindthegap

## Repository Structure

```
mindthegap/
├── mtg/                    # Core library module
│   └── src/
│       ├── main/scala/     # Main source code
│       └── test/scala/     # Test specifications
├── diagram/                # Base diagram generation module
├── diagram-ascii/          # ASCII diagram renderer
├── diagram-mermaid/        # Mermaid diagram renderer
├── examples/               # Example code and usage demonstrations
├── doc/                    # Documentation (Zola-based website)
├── project/                # SBT build configuration
│   ├── Settings.scala      # Build settings and publishing configuration
│   ├── Dependencies.scala  # Project dependencies
│   └── plugins.sbt         # SBT plugins
├── .github/workflows/      # CI/CD workflows
├── build.sbt               # Main build definition
├── version.sbt             # Version management
└── CONTRIBUTING.md         # Contribution guidelines
```

## Module Organization

The project is organized as a multi-module SBT build:

### Core Modules

1. **mtg** - Core interval library
   - Location: `mtg/src/main/scala/com/github/gchudnov/mtg/`
   - Published as: `com.github.gchudnov:mtg`
   - Contains:
     - `Interval.scala` - Main interval trait and operations
     - `Domain.scala` - Domain abstraction for successor/predecessor operations
     - `internal/` - Internal implementation details
       - `alg/` - Algorithm implementations (Intersection, Union, Gap, etc.)
       - `domain/` - Domain implementations (Integral, Fractional, DateTime, etc.)
       - `ordering/` - Ordering implementations for intervals and values
       - `rel/` - Allen's Interval Algebra relations

2. **diagram** - Base diagram functionality
   - Published as: `com.github.gchudnov:mtg-diagram`
   - Depends on: `mtg`

3. **diagram-ascii** - ASCII diagram rendering
   - Published as: `com.github.gchudnov:mtg-diagram-ascii`
   - Depends on: `mtg`, `diagram`

4. **diagram-mermaid** - Mermaid diagram rendering
   - Published as: `com.github.gchudnov:mtg-diagram-mermaid`
   - Depends on: `mtg`, `diagram`

5. **examples** - Example applications
   - Not published
   - Depends on: `mtg`, `diagramAscii`, `diagramMermaid`

## Key Technical Concepts

### Intervals
The library supports various interval types:
- **Bounded**: `[a, b]`, `(a, b)`, `[a, b)`, `(a, b]`
- **Unbounded**: `(-∞, b]`, `[a, +∞)`, `(-∞, +∞)`
- **Empty**: Intervals where left > right
- **Point**: Degenerate intervals where left == right

### Allen's Interval Algebra
Implements all 13 relations between intervals:
- Before, After
- Meets, MetBy
- Overlaps, OverlappedBy
- Starts, StartedBy
- During, Contains
- Finishes, FinishedBy
- EqualsTo

### Domain Abstraction
The `Domain[T]` trait provides:
- `succ(x)` - Successor function
- `pred(x)` - Predecessor function
- `count(start, end)` - Cardinality calculation
- Built-in support for: Int, Long, Float, Double, LocalDate, LocalTime, LocalDateTime, OffsetDateTime, ZonedDateTime

## Development Workflows

### Prerequisites
- **Java**: JDK 21 (Temurin distribution)
- **Scala**: 3.3.4 (managed by sbt)
- **sbt**: Latest version (uses sbt build tool)

### Common SBT Commands

```bash
# Run all tests
sbt test

# Run specific test suite
sbt "testOnly *UnionSpec*"

# Format code
sbt fmt

# Check code formatting
sbt chk

# Run coverage analysis
sbt clean coverage test coverageReport

# Compile all modules
sbt compile

# Interactive console
sbt console

# Show dependency updates
sbt upd

# Clean build artifacts
sbt clean
```

### SBT Command Aliases (defined in build.sbt)
- `fmt` - Format all code (scalafmtSbt, scalafmt, test:scalafmt)
- `chk` - Check code formatting
- `plg` - List plugin dependencies
- `upd` - Check for dependency updates

### Project-Specific Commands

```bash
# Build documentation
./build.sh

# Coverage for specific module
sbt coverage mtg/test mtg/coverageReport
```

## Testing Conventions

### Test Framework
- **Framework**: ScalaTest with ScalaCheck
- **Base Class**: Tests extend `TestSpec`
- **Naming**: Test files end with `Spec.scala` (e.g., `UnionSpec.scala`)
- **Filter**: Only files ending with "Spec" are executed as tests

### Test Structure
```scala
final class UnionSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration =
    PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Union" when {
    "a.union(b)" should {
      "any if A and B are empty" in {
        forAll(genEmptyIntArgs, genEmptyIntArgs) { case (argsX, argsY) =>
          // Test implementation
        }
      }
    }
  }
```

### Test Location
- Main tests: `mtg/src/test/scala/com/github/gchudnov/mtg/`
- Module-specific tests follow the same pattern in their respective modules

### Test Dependencies
- ScalaTest 3.2.19
- ScalaCheck 1.18.1
- ScalaTestPlus 3.2.19.0

## Code Style and Formatting

### Scalafmt Configuration
- Configuration file: `.scalafmt.conf`
- Version: 3.8.3
- Max column: 140
- Dialect: Scala 3
- Indentation: 2 spaces
- Align preset: most

### Key Style Rules
- Use Scala 3 new syntax (significant indentation)
- No optional braces (old syntax removed)
- Trailing commas: multiple
- Rewrite rules: AvoidInfix, Imports, RedundantBraces
- Import sorting: original order

### Running Formatters
```bash
# Format all code
sbt fmt

# Check formatting without changes
sbt chk
```

## Build System

### JVM Options (.jvmopts)
```
--add-opens=java.base/java.util=ALL-UNNAMED
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED
-Dio.netty.tryReflectionSetAccessible=true
```

### Build Settings (project/Settings.scala)
- **Organization**: `com.github.gchudnov`
- **Scala Version**: 3.3.4
- **Cross Build**: Disabled (Scala 3 only)
- **Compiler Options**: Strict (-Xfatal-warnings enabled)
- **Publishing**: Sonatype OSS / Maven Central

### Compiler Flags
- `-deprecation` - Warn about deprecated APIs
- `-feature` - Warn about features requiring explicit import
- `-unchecked` - Enable additional warnings
- `-Xfatal-warnings` - Treat warnings as errors
- `-new-syntax` - Require Scala 3 syntax
- `-indent` - Allow significant indentation

## CI/CD Pipeline

### GitHub Actions (.github/workflows/scala.yml)
- **Trigger**: Push/PR to `main` branch
- **OS**: Ubuntu latest
- **Java**: JDK 21 (Temurin)
- **Steps**:
  1. Checkout code
  2. Set up JDK with sbt caching
  3. Run tests (`sbt test`)

## Contributing Guidelines

### Commit Message Format
```
<type>(<scope>): <subject>

Types:
- feat: New feature
- fix: Bug fix
- docs: Documentation changes
- test: Adding/modifying tests
- chore: Build process or tooling changes

Examples:
- feat(intervals): add new inflation method for intervals
- fix(algorithms): resolve edge case in intersection function
- docs: update contributing guidelines
```

### Pull Request Process
1. Fork and create a feature branch
2. Make changes following code style
3. Write tests for new functionality
4. Format code with `sbt fmt`
5. Ensure all tests pass (`sbt test`)
6. Commit with descriptive messages
7. Push and create PR

### Code Quality Requirements
- All tests must pass
- Code must be formatted with Scalafmt
- No compiler warnings (due to -Xfatal-warnings)
- Tests required for new features
- Coverage reports encouraged

## Documentation

### Documentation Structure
- **Location**: `doc/` directory
- **Generator**: Zola static site generator
- **Theme**: Custom xdoc theme
- **Build**: `./build.sh` (builds theme and site)
- **Published**: https://gchudnov.github.io/mindthegap/

### Documentation Sections
- Intervals
- Relations (Allen's Algebra)
- Algorithms
- Examples

## Key Files for AI Assistants

### Critical Files to Understand
1. **build.sbt** - Module definitions and dependencies
2. **project/Settings.scala** - Compiler settings and build configuration
3. **project/Dependencies.scala** - All project dependencies
4. **mtg/src/main/scala/com/github/gchudnov/mtg/Interval.scala** - Core interval API
5. **mtg/src/main/scala/com/github/gchudnov/mtg/Domain.scala** - Domain abstraction
6. **CONTRIBUTING.md** - Contribution guidelines

### Source Code Patterns to Follow
1. **Scala 3 Syntax**: Use significant indentation, new syntax
2. **Extension Methods**: Interval operations implemented as trait methods
3. **Type Classes**: Domain instances provided via givens
4. **Immutability**: All operations return new intervals
5. **Internal Package**: Implementation details in `internal/` package

## Common Tasks for AI Assistants

### Adding a New Algorithm
1. Create implementation in `mtg/src/main/scala/com/github/gchudnov/mtg/internal/alg/`
2. Add trait method to `AlgBasic.scala` or create new trait
3. Mix trait into `Interval` trait
4. Create test in `mtg/src/test/scala/com/github/gchudnov/mtg/algorithms/`
5. Format code: `sbt fmt`
6. Run tests: `sbt test`

### Adding Domain Support
1. Create domain implementation in `internal/domain/`
2. Extend `Domain[T]` trait
3. Implement `succ`, `pred`, `count` methods
4. Add to `DomainFactory` or `DomainLowPriority`
5. Add tests for new domain
6. Run: `sbt clean test`

### Fixing a Bug
1. Create/locate test that reproduces the issue
2. Verify test fails: `sbt "testOnly *YourTestSpec*"`
3. Fix implementation
4. Format: `sbt fmt`
5. Verify fix: `sbt test`
6. Check coverage if needed: `sbt coverage test coverageReport`

### Updating Dependencies
1. Check for updates: `sbt upd`
2. Update version in `project/Dependencies.scala`
3. Test compatibility: `sbt clean compile test`
4. Update `build.sbt` if major version changes

## Special Considerations for AI Assistants

### Code Organization
- Keep public API in main package (`com.github.gchudnov.mtg`)
- Keep implementation details in `internal` package
- Use traits for composable functionality
- Prefer immutable operations

### Type Safety
- Leverage Scala 3's type system
- Use givens for implicit context
- Maintain type safety across transformations
- Avoid runtime type checks where possible

### Performance
- Algorithms should be efficient for typical use cases
- Consider both time and space complexity
- Profile before optimizing
- Document complexity in comments

### Testing
- Property-based testing with ScalaCheck for general properties
- Example-based testing for specific scenarios
- Test edge cases: empty intervals, unbounded intervals, point intervals
- Test all Allen relations where applicable

### Documentation
- Use ScalaDoc for public APIs
- Include examples in documentation
- Document complexity and edge cases
- Keep README and docs in sync

## Version Management

- **Current Version**: Managed in `version.sbt`
- **Release Process**: Uses sbt-release plugin
- **Versioning**: Early SemVer (0.y.z or x.y.z)
- **Publishing**: Sonatype OSS, Maven Central

## Resources

- **Project Site**: https://gchudnov.github.io/mindthegap/
- **Repository**: https://github.com/gchudnov/mindthegap
- **Issues**: https://github.com/gchudnov/mindthegap/issues
- **Allen's Interval Algebra**: https://www.ics.uci.edu/~alspaugh/cls/shr/allen.html

## Quick Reference

### Most Common Commands
```bash
sbt test                    # Run all tests
sbt fmt                     # Format code
sbt "testOnly *MySpec*"     # Run specific test
sbt clean coverage test coverageReport  # Generate coverage
./build.sh                  # Build documentation
```

### File Patterns
- Source: `*/src/main/scala/com/github/gchudnov/mtg/**/*.scala`
- Tests: `*/src/test/scala/com/github/gchudnov/mtg/**/*Spec.scala`
- Build: `*.sbt`, `project/*.scala`

### Important Conventions
- All tests end with `Spec.scala`
- Internal implementations go in `internal/` package
- Use Scala 3 syntax exclusively
- Format before committing
- No warnings allowed (will fail build)
