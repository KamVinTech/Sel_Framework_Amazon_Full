# Enterprise Selenium Test Automation Framework

A robust, scalable, and maintainable test automation framework built using Selenium WebDriver, TestNG, and Java. This framework follows the Page Object Model design pattern and incorporates industry best practices.

## 🚀 Features

### Core Features
- ✅ Page Object Model (POM) design pattern
- ✅ Thread-safe WebDriver management using ThreadLocal
- ✅ Cross-browser testing support (Chrome, Firefox, Edge)
- ✅ Parallel test execution capability
- ✅ Environment-specific configuration management
- ✅ Retry mechanism for flaky tests

### Test Management
- ✅ Data-driven testing support using CSV and Excel
- ✅ TestNG test organization and grouping
- ✅ Custom TestNG listeners for enhanced reporting
- ✅ Test retry capability for failed tests
- ✅ Configurable test parameters

### Reporting & Logging
- ✅ Extent Reports integration for rich HTML reports
- ✅ Log4j2 integration for comprehensive logging
- ✅ Automatic screenshot capture on test failure
- ✅ Test execution logs with timestamp
- ✅ Detailed test suite and test case reports

### Utilities
- ✅ Screenshot utility
- ✅ Excel data provider
- ✅ Configuration reader
- ✅ Custom exception handling
- ✅ Framework constants management

## 🏗️ Framework Structure

```
src/
├── main/
│   └── java/
│       ├── constants/
│       │   ├── FrameworkConstants.java
│       │   └── FrameworkException.java
│       ├── listeners/
│       │   ├── RetryAnalyzer.java
│       │   └── TestListener.java
│       ├── pages/
│       │   ├── HomePage.java
│       │   ├── ProductDetailsPage.java
│       │   └── SearchResultsPage.java
│       └── utils/
│           ├── ConfigReader.java
│           ├── ExcelDataProvider.java
│           ├── ExtentReportManager.java
│           ├── ScreenshotUtils.java
│           └── WebDriverFactory.java
├── test/
│   ├── java/
│   │   ├── base/
│   │   │   └── BaseTest.java
│   │   └── tests/
│   │       ├── NavigationTest.java
│   │       ├── ProductTest.java
│   │       └── SearchTest.java
│   └── resources/
│       ├── config/
│       │   ├── config.properties
│       │   ├── qa.properties
│       │   └── staging.properties
│       ├── testdata/
│       │   ├── TestData.csv
│       │   └── SearchTestData.csv
│       └── log4j2.xml
```

## 🛠️ Prerequisites

- Java JDK 11 or higher
- Maven 3.8 or higher
- Chrome/Firefox/Edge browser
- IDE (IntelliJ IDEA or Eclipse)

## 📋 Configuration

### Environment Configuration
- Multiple environment support (QA, Staging)
- Environment-specific properties files
- Dynamic configuration switching

### Browser Configuration
```properties
browser=chrome
headless=false
implicit.wait=10
explicit.wait=20
page.load.timeout=30
script.timeout=20
```

### Test Configuration
```properties
parallel.execution=true
thread.count=2
retry.failed.tests=true
max.retry.count=2
```

## 📊 Test Data Management

### CSV Format
Test data is maintained in CSV files for easy maintenance and version control:
```csv
TestCase ID,Product Name,Category,Expected Results,Price Range,Tags
TC_SEARCH_001,iPhone 15 Pro Max,Electronics,Multiple results,>100000,Smoke
```

## 📝 Test Execution

### Running Tests
```bash
mvn clean test
```

### Running Specific Test Groups
```bash
mvn clean test -Dgroups=smoke,regression
```

### Running with Different Environments
```bash
mvn clean test -Denv=qa
mvn clean test -Denv=staging
```

## 📈 Reporting

### Extent Reports
- Rich HTML reports with test execution details
- Screenshots embedded for failed tests
- Test execution timeline
- Test suite and test case statistics

### Logging
- Comprehensive logging using Log4j2
- Different log levels (INFO, DEBUG, ERROR)
- Rolling file appender for log management

## 🔧 Best Practices

### WebDriver Management
- Thread-safe driver initialization
- Automatic driver cleanup
- Screenshot capture on failure
- Configurable timeouts and options

### Page Objects
- Encapsulated page elements and actions
- Robust element handling with explicit waits
- Error handling and logging
- Fluent page navigation

### Test Organization
- Clear test case categorization
- Data-driven test support
- Parallel execution capability
- Retry mechanism for flaky tests

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

##
