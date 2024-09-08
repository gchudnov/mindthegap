# Contributing to Mind the Gap

Thank you for considering contributing to the **Mind the Gap** project! Whether you're fixing a bug, adding a new feature, or improving documentation, we welcome your input.

## Table of Contents

1. [How Can I Contribute?](#how-can-i-contribute)
   - [Reporting Bugs](#reporting-bugs)
   - [Suggesting Enhancements](#suggesting-enhancements)
   - [Pull Requests](#pull-requests)
2. [Development Setup](#development-setup)
3. [Coding Guidelines](#coding-guidelines)
4. [Commit Guidelines](#commit-guidelines)

---

## How Can I Contribute?

There are several ways you can contribute to the **Mind the Gap** library:

### Reporting Bugs

If you've encountered a bug, please report it by [opening an issue](https://github.com/gchudnov/mindthegap/issues). Before submitting the issue, please check if it has already been reported. 

**When creating a bug report**, include:
- A clear and descriptive title.
- The steps to reproduce the issue.
- The expected behavior and what happened instead.
- Any relevant logs, error messages, or screenshots.
- The environment (e.g., OS, Scala version) you're using.

### Suggesting Enhancements

To suggest an enhancement, [open an issue](https://github.com/gchudnov/mindthegap/issues) and describe your suggestion in detail.

**When suggesting enhancements**, please provide:
- A clear explanation of what the enhancement is and why it would be useful.
- Any potential implementation ideas or alternatives.
- If possible, examples of how the feature could be used.

### Pull Requests

If you'd like to contribute code, please follow these steps:
1. **Fork** the repository and create your branch:  
   ```bash
   git checkout -b my-feature-branch
   ```
2. **Make your changes** and ensure that your code follows the project’s coding guidelines.
3. **Write tests** to cover the new functionality.
4. **Commit your changes**:  
   ```bash
   git commit -m "Add some feature"
   ```
5. **Push to your branch**:  
   ```bash
   git push origin my-feature-branch
   ```
6. **Create a Pull Request**: Submit the pull request through GitHub.

If your pull request addresses an existing issue, make sure to link to the issue by including `closes #issue_number` in your pull request description.

---

## Development Setup

### Prerequisites

Ensure you have the following installed:
- [Scala](https://www.scala-lang.org/)
- [sbt (Scala Build Tool)](https://www.scala-sbt.org/)

### Setting Up the Development Environment

1. **Fork and clone** the repository:
   ```bash
   git clone https://github.com/your-username/mindthegap.git
   cd mindthegap
   ```

2. **Run tests** to ensure everything is set up correctly.

### Running Tests

Make sure your contributions include relevant tests. To run all the tests, execute:
```bash
sbt test
```

If you’re working on a specific test case or test suite, you can run individual tests by using:
```bash
sbt "testOnly *YourTestClass*"
```

### Code Coverage

To ensure the quality and reliability of the code, we encourage running code coverage analysis as part of your testing process.

You can generate coverage reports using the following commands:

```bash
# Run coverage on all tests
# Coverage reports will be saved in the target/scala-<scala-version>/scoverage-report directory.
sbt clean coverage test coverageReport
```

If you're working on a specific module (e.g., `mtg`), you can run coverage for just that module:

```bash
# Run coverage on a specific module
sbt coverage mtg/test mtg/coverageReport
```

If you're working within the `sbt` console and want to run coverage without restarting `sbt`, you can use the following sequence of commands:

```bash
# Enable coverage in sbt CLI
set coverageEnabled := false; clean; coverage; test; coverageReport
```

After running the above commands, the coverage report will be saved in the `target/scala-<scala-version>/scoverage-report` directory. You can open the `index.html` file inside this directory to view the report in your browser.

---

## Coding Guidelines

Please ensure your code follows these guidelines:

1. **Code Style**: Use [Scalafmt](https://scalameta.org/scalafmt/) to format your code. A configuration file is included in the project.
   
2. **Write tests** for new functionality or bug fixes.
   
3. **Comment your code**: Ensure your code is easy to understand by including comments where necessary, especially for complex logic.

4. **Modular code**: Keep your code modular and reusable. Avoid duplicating code where possible.

---

## Commit Guidelines

To maintain a clean and consistent commit history, please follow these guidelines:

1. **Commit messages** should be descriptive and follow this structure:
   ```
   <type>(<scope>): <subject>
   ```

   **Types** include:
   - `feat`: A new feature.
   - `fix`: A bug fix.
   - `docs`: Documentation changes.
   - `test`: Adding or modifying tests.
   - `chore`: Changes to the build process or auxiliary tools and libraries.

   **Examples**:
   ```
   feat(intervals): add new inflation method for intervals
   fix(algorithms): resolve edge case in intersection function
   docs: update contributing guidelines
   ```

2. **Commit early and often**: Don't wait for your contribution to be perfect before committing. Smaller, focused commits make it easier to review and collaborate.
