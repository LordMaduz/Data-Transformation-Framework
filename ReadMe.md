
# Data Transformation Service

> Enterprise-grade Spring Boot microservice for processing and transforming financial trade data with dynamic rule-based booking generation and multi-typology support.

## Project Overview

A production-ready backend service that orchestrates complex financial trade transformations for FX (Foreign Exchange) trading operations. The system processes instruction events, applies configurable business rules, and generates Murex booking records with support for multiple trade typologies including FX Spot, FX Swap, and FX NDF (Non-Deliverable Forward).

**Key Business Value:**
- Automates hedge booking generation based on configurable business rules
- Supports multi-entity trade processing with currency exposure management
- Provides real-time transformation with transactional integrity
- Enables audit trails through comprehensive observability

---

## Architecture Overview
<img width="1589" height="288" alt="diagram-export-13-10-2025-11_04_02-PM" src="https://github.com/user-attachments/assets/bffa739d-f55b-471d-a1db-eb7e3cea9d18" />

## Tech Stack

| Category | Technologies |
|----------|-------------|
| **Core** | Java 21 (Virtual Threads, Records), Spring Boot 3.5.0 |
| **Data** | MariaDB, Spring JDBC, FreeMarker (SQL Templates) |
| **Messaging** | Apache Kafka (Producer/Consumer) |
| **Mapping** | MapStruct, Jackson, Apache Commons BeanUtils |
| **Observability** | Spring Actuator, SLF4J/Logback |
| **API Docs** | SpringDoc OpenAPI 3 (Swagger) |
| **Build** | Maven 3.x |

## Key Features

### 1. Dynamic Rule-Based Processing
- Configuration-driven transformation rules stored in database
- JSON-based rule definitions for runtime flexibility
- Multi-criteria rule matching and selection
- No code deployment needed for rule changes

### 2. Multi-Strategy Pattern Implementation
- **Strategy A**: Single-record processing pattern
- **Strategy B**: Dual-record processing with sub-record handling
- **Strategy C**: Complex multi-record processing with cross-record references
- Extensible factory pattern for adding new strategies without modifying existing code

### 3. Concurrent Processing with Structured Concurrency
- Virtual thread-based parallel processing using Java 21 `StructuredTaskScope`
- All-or-none transaction semantics ensure data consistency
- Automatic failure handling and rollback
- Processes 10,000+ concurrent operations on modest hardware

### 4. Dynamic Field Transformation
- Reflection-based field mapping using VarHandles (3x faster than standard reflection)
- Configurable calculation rules
- Runtime field filtering and selection
- Support for complex nested field mappings

### 5. Template-Based SQL Generation
- FreeMarker templates for dynamic SQL queries
- Type-safe parameterization prevents SQL injection
- Query result caching for performance
- Complex multi-table joins with dynamic filtering

### 6. Event-Driven Architecture
- Asynchronous event publishing to Apache Kafka
- Resilient consumer with exponential backoff retry (6 attempts)
- Manual acknowledgment for reliable message processing
- Dead letter queue handling for failed messages

## Getting Started

### Prerequisites
```bash
- Java 21+
- Maven 3.8+
- MariaDB 10.6+
- Apache Kafka 3.x (optional for full functionality)
```

### Installation
```bash
git clone <repository-url>
cd Instruction-Builder-Prototype
mvn clean install
```

### Configuration

Edit src/main/resources/application.yml:
```bash
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/database_name
    username: your_username
    password: your_password
    
  kafka:
    bootstrap-servers: localhost:9094
    
  threads:
    virtual:
      enabled: true
```
### Run the application

```bash
mvn spring-boot:run
The service starts on http://localhost:8080
```
