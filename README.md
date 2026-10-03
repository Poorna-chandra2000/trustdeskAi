# 🛡️ TrustDesk AI

> **An Enterprise Agentic Support Automation & Human-In-The-Loop (HITL) Platform**
> Built with Java 21, Spring Boot 3, Spring AI, Ollama, and PostgreSQL (PgVector).

---

## 🚀 Quick Start (Docker)

The fastest way to test TrustDesk AI is using the official Docker image. Since the application requires a Database and an LLM, follow these three simple steps:

### 1. Start the Database
Run a PostgreSQL instance with `pgvector` support:
```bash
docker run -d \
  --name trustdesk-postgres \
  -e POSTGRES_DB=trustdesk \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=poorna \
  -p 5434:5432 \
  pgvector/pgvector:pg16
```

### 2. Start Ollama
Install Ollama from [ollama.com](https://ollama.com) and pull the required model:
```bash
ollama pull gemma4:31b-cloud
```

### 3. Run the Application
Pull and run the TrustDesk AI image. We use `host.docker.internal` to allow the container to communicate with Ollama and Postgres running on your host machine.

```bash
docker run -p 8083:8083 \
  -e DB_HOST=host.docker.internal \
  -e OLLAMA_BASE_URL=http://host.docker.internal:11434 \
  -e OLLAMA_MODEL=gemma4:31b-cloud \
  your-username/trustdesk-ai:latest
```
*Replace `your-username` with the actual Docker Hub username.*

**The app is now live at:** `http://localhost:8083`

---

## 📋 Table of Contents

1. [System Overview](#-system-overview)
2. [Architecture & Workflow Lifecycle](#-architecture--workflow-lifecycle)
3. [Project Directory Structure](#-project-directory-structure)
4. [Core Technical Deep-Dives](#-core-technical-deep-dives)
5. [Database Schema & Data Model](#-database-schema--data-model)
6. [Prerequisites & Dependency Matrix](#-prerequisites--dependency-matrix)
7. [Step-by-Step Installation & Setup](#-step-by-step-installation--setup)
8. [Configuration Reference Matrix](#-configuration-reference-matrix)
9. [Complete API Reference](#-complete-api-reference)
10. [Testing & Verification](#-testing--verification)
11. [Troubleshooting & Performance Tuning](#-troubleshooting--performance-tuning)

---

## 🎯 System Overview

**TrustDesk AI** is an intelligent, autonomous customer service resolution engine designed to handle customer inquiries, order verifications, and refund claims. Instead of relying on rigid decision trees or ungrounded generative models, TrustDesk AI uses **Agentic RAG (Retrieval-Augmented Generation)** combined with **deterministic database tools**.

When a support ticket is received:

1. The AI Agent inspects the user request.
2. It dynamically executes function calls to retrieve exact purchase and shipping details from PostgreSQL.
3. It performs semantic vector searches against internal policy documents stored in `pgvector`.
4. It calculates policy windows (e.g., return timelines, eligibility rules) deterministically.
5. If the request involves high-risk actions (such as refunds or high-priority escalations), the engine places the ticket into a **Human-In-The-Loop (HITL)** approval queue where support agents can review, audit, approve, or reject proposed decisions.

---

## 🏗️ Architecture & Workflow Lifecycle

### High-Level System Architecture

```
                                      +------------------------------------+
                                      |         Customer / Client          |
                                      +------------------------------------+
                                                        |
                                                        v
                                      +------------------------------------+
                                      |   Spring Boot REST Controllers     |
                                      +------------------------------------+
                                                        |
                                                        v
                                      +------------------------------------+
                                      |         TicketAgentService         |
                                      +------------------------------------+
                                                        |
                                      +-----------------+-----------------+
                                      |                                   |
                                      v                                   v
                      +-------------------------------+   +-------------------------------+
                      |   Spring AI ChatClient        |   |   Spring AI VectorStore       |
                      |   (Advisor Chain & Memory)    |   |   (PgVector / PostgreSQL)     |
                      +-------------------------------+   +-------------------------------+
                                      |                                   |
                                      +-----------------------+-----------------------+           |
                                      |                                               |           |
                                      v                                               v           v
+---------------------------+                   +----------------------------------+
|      Ollama Runtime       |                   |       PostgreSQL Database        |
|  (gemma4:31b-cloud)     |                   |  - orders                        |
+---------------------------+                   |  - ticket_evaluations            |
              |                                 |  - vector_store (Policy Chunks)  |
              +----------- Tool Executions ---->+----------------------------------+
```

### Agentic Execution Sequence

```
User/System              TicketAgentService          ChatClient / Ollama            Postgres / VectorStore
   |                             |                           |                                 |
   |-- POST /agent/evaluate ---->|                           |                                 |
   |                             |-- Construct Prompt ------>|                                 |
   |                             |   + Advisors & Tools      |                                 |
   |                             |                           |-- Query order details --------->|
   |                             |                           |<-- Return order JSON -----------|
   |                             |                           |                                 |
   |                             |                           |-- Perform Similarity Search --->|
   |                             |                           |<-- Return Policy Chunks --------|
   |                             |                           |                                 |
   |                             |<-- Structured Result -----|                                 |
   |                             |   (TicketEvaluationResult)|                                 |
   |                             |                                                             |
   |                             |-- Persist Evaluation Entity (Status: PENDING_HUMAN) ------->|
   |                             |<-- Entity Saved --------------------------------------------|
   |<-- Return Evaluation DTO ---|                                                             |
```

---

## 📁 Project Directory Structure

```text
trustdeskAi/
├── src/
│   ├── main/
│   │   ├── java/com/demo/trustdeskAi/
│   │   │   ├── config/
│   │   │   │   ├── AgentToolsConfig.java       # Spring AI Function/Tool definitions (@Bean)
│   │   │   │   ├── VectorStoreConfig.java      # PgVector VectorStore & Embedding configuration
│   │   │   │   └── WebConfig.java              # CORS and MVC configurations
│   │   │   ├── controllers/
│   │   │   │   ├── OrderController.java         # REST endpoints for Order management
│   │   │   │   └── TicketAgentController.java  # REST endpoints for Evaluation & HITL Queue
│   │   │   ├── dto/
│   │   │   │   ├── OrderLookupRequest.java      # Function Tool input schema for order lookup
│   │   │   │   ├── PolicyQueryRequest.java     # Function Tool input schema for vector search
│   │   │   │   ├── TicketEvaluationRequestDto.java
│   │   │   │   ├── TicketEvaluationResultDto.java
│   │   │   │   └── HumanReviewRequestDto.java
│   │   │   ├── entities/
│   │   │   │   ├── OrderEntity.java            # JPA Entity mapping postgres `orders`
│   │   │   │   ├── TicketEvaluationEntity.java # JPA Entity mapping postgres `ticket_evaluations`
│   │   │   │   └── TicketStatus.java           # Enum: AUTO_RESOLVED, PENDING_HUMAN_APPROVAL, etc.
│   │   │   ├── repositories/
│   │   │   │   ├── OrderRepository.java        # Spring Data JPA Repository for Orders
│   │   │   │   └── TicketEvaluationRepository.java
│   │   │   └── service/
│   │   │       ├── OrderService.java           # Business logic for seeding/querying orders
│   │   │       ├── PolicyIngestionService.java # Reads Markdown & chunks into PgVector
│   │   │       └── TicketAgentService.java     # Orchestrates ChatClient prompt execution & persistence
│   │   └── resources/
│   │       ├── application.properties          # Environment & Spring AI configuration
│   │       └── policies/                       # Policy Markdown documents
│   │           ├── KB-REFUND-001.md
│   │           ├── KB-SHIPPING-002.md
│   │           └── KB-DAMAGED-003.md
├── test_hitl.sh                                # Automated E2E test script
├── pom.xml                                     # Maven dependency definition
└── README.md
```

---

## 🔬 Core Technical Deep-Dives

### 1. Agentic Function Calling & Tool Registry

The AI Agent dynamically retrieves data from relational tables and vector databases through Spring AI Function Beans defined in `AgentToolsConfig.java`. Tools use standard Java `Function<Request, Response>` syntax annotated with `@Description`, providing strict schema context to Ollama.

#### Safe Tool Implementation Example (`getOrderDetails`)

To ensure resilient model interaction, tool functions are wrapped with internal exception handlers that return structured JSON error messages rather than crashing the execution stack:

```java
@Bean
@Description("Fetch customer order details from PostgreSQL by order ID")
public Function<OrderLookupRequest, String> getOrderDetails(OrderRepository orderRepository, ObjectMapper objectMapper) {
    return request -> {
        try {
            return orderRepository.findById(request.orderId())
                    .map(order -> {
                        try {
                            return objectMapper.writeValueAsString(order);
                        } catch (Exception e) {
                            return "{\"error\": \"Order serialization error\"}";
                        }
                    })
                    .orElse("{\"error\": \"Order " + request.orderId() + " not found.\"}");
        } catch (Exception ex) {
            return "{\"error\": \"Order lookup failed: " + ex.getMessage() + "\"}";
        }
    };
}
```

---

### 2. Policy RAG & PgVector Mechanics

Policy documents written in Markdown are split into semantically coherent segments using Spring AI's `TokenTextSplitter` and embedded into `pgvector` with HNSW indexing.

* **Distance Strategy**: Cosine Distance ($1 - \text{cosine similarity}$)
* **Index Strategy**: Hierarchical Navigable Small World (HNSW) for vector search performance
* **Ingestion Logic**: Reads `.md` files, assigns `source` metadata, and indexes vector embeddings into PostgreSQL.

```java
public void ingestPolicies() {
    List<Document> documents = new ArrayList<>();
    // Load markdown policy files from classpath resources
    Resource[] resources = resourcePatternResolver.getResources("classpath:policies/*.md");
    
    for (Resource resource : resources) {
        String content = resource.getContentAsString(StandardCharsets.UTF_8);
        Document doc = new Document(content, Map.of("source", resource.getFilename()));
        documents.add(doc);
    }
    
    TokenTextSplitter splitter = new TokenTextSplitter(500, 100, 5, 10000, true);
    List<Document> splitDocs = splitter.apply(documents);
    vectorStore.add(splitDocs);
}
```

---

### 3. HITL State Machine & Workflow

Every ticket decision goes through a deterministic lifecycle managed by `TicketStatus`:

```
                 +----------------------+
                 |  Ticket Submitted    |
                 +----------------------+
                            |
                            v
                 /----------------------\
                /   Requires Human       \
               <     Approval?            >
                \                        /
                 \----------------------/
                   /                  \
             YES  /                    \  NO
                 v                      v
  +---------------------------+   +-------------------+
  |  PENDING_HUMAN_APPROVAL   |   |   AUTO_RESOLVED   |
  +---------------------------+   +-------------------+
                |
        Agent Action
        /          \
       v            v
+---------------+  +---------------+
| APPROVED_BY_  |  | REJECTED_BY_  |
|    HUMAN      |  |    HUMAN      |
+---------------+  +---------------+
```

---

## 🗄️ Database Schema & Data Model

The application uses two primary relational tables (`orders` and `ticket_evaluations`) alongside the `vector_store` table managed by PgVector.

```sql
-- 1. Customer Orders Table
CREATE TABLE orders (
    order_id VARCHAR(64) PRIMARY KEY,
    customer_id VARCHAR(64) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    purchase_date TIMESTAMP NOT NULL,
    delivery_date TIMESTAMP,
    order_status VARCHAR(32) NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    items_json TEXT NOT NULL
);

-- 2. Ticket Evaluations (HITL Audit Table)
CREATE TABLE ticket_evaluations (
    ticket_id VARCHAR(64) PRIMARY KEY,
    conversation_id VARCHAR(64) NOT NULL,
    issue_description TEXT NOT NULL,
    proposed_decision TEXT,
    final_decision TEXT,
    priority VARCHAR(16) NOT NULL,
    status VARCHAR(32) NOT NULL, -- PENDING_HUMAN_APPROVAL, AUTO_RESOLVED, APPROVED_BY_HUMAN, REJECTED_BY_HUMAN
    requires_human_approval BOOLEAN NOT NULL DEFAULT FALSE,
    reasoning TEXT,
    cited_policy_doc VARCHAR(128),
    human_reviewer_notes TEXT,
    created_at TIMESTAMP NOT NULL,
    reviewed_at TIMESTAMP
);

-- 3. PgVector Store Table (Auto-managed by Spring AI)
CREATE TABLE vector_store (
    id UUID PRIMARY KEY,
    content TEXT,
    metadata JSONB,
    embedding VECTOR(384) -- Dimension size matches local embedding model
);

CREATE INDEX ON vector_store USING hnsw (embedding vector_cosine_ops);
```

---

## 🛠️ Prerequisites & Dependency Matrix

| Technology | Version Requirement | Description |
| --- | --- | --- |
| **Java JDK** | `21` or higher | Core Language Runtime |
| **Spring Boot** | `3.2.x` / `3.3.x` | Application Framework |
| **Spring AI** | `2.0.1` | AI Orchestration Framework |
| **Ollama** | `0.3.x` or higher | Local LLM Execution Engine |
| **PostgreSQL** | `15` or `16` | Relational Database |
| **pgvector Extension** | `0.5.0` or higher | Vector Similarity Plugin |
| **Apache Maven** | `3.8+` | Build & Dependency Tool |

---

## 🚀 Step-by-Step Installation & Setup

### 1. Configure PostgreSQL & PgVector

Ensure PostgreSQL is running locally or via Docker with `pgvector` enabled:

```bash
# Docker option for PostgreSQL + PgVector
docker run -d \
  --name trustdesk-postgres \
  -e POSTGRES_DB=trustdesk \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=poorna \
  -p 5434:5432 \
  pgvector/pgvector:pg16
```

Verify `vector` extension support:

```sql
psql -U postgres -d trustdesk -c "CREATE EXTENSION IF NOT EXISTS vector;"
```

---

### 2. Configure & Run Ollama

Install Ollama from [ollama.com](https://ollama.com) and pull a model that supports function calling:

```bash
# Pull gemma4 (Recommended for tool usage)
ollama pull gemma4:31b-cloud

# Verify Ollama is listening at http://localhost:11434
curl http://localhost:11434/
```

---

### 3. Application Build & Execution

Clone the repository, verify `src/main/resources/application.properties`, and start the service:

```bash
# Clean build
mvn clean install -DskipTests

# Run Spring Boot Application
mvn spring-boot:run
```

---

## ⚙️ Configuration Reference Matrix

Below are the operational configuration keys available in `application.properties`:

| Configuration Key | Example / Default Value | Purpose / Description |
| --- | --- | --- |
| `server.port` | `8083` | HTTP listener port for REST endpoints |
| `spring.datasource.url` | `jdbc:postgresql://postgres:5432/trustdesk` | PostgreSQL connection string |
| `spring.datasource.username` | `postgres` | Database username |
| `spring.datasource.password` | `poorna` | Database password |
| `spring.ai.ollama.base-url` | `http://host.docker.internal:11434` | Ollama service endpoint |
| `spring.ai.ollama.chat.options.model` | `gemma4:31b-cloud` | Target model for function calling & reasoning |
| `spring.ai.ollama.chat.options.temperature` | `0.2` | Determinism control (lower = more deterministic) |
| `spring.ai.vectorstore.pgvector.index-type` | `HNSW` | Index algorithm (`HNSW` or `IVFFLAT`) |
| `spring.ai.vectorstore.pgvector.distance-type` | `COSINE` | Distance calculation metric (`COSINE`, `EUCLIDEAN`) |
| `spring.ai.vectorstore.pgvector.dimensions` | `384` | Embedding vector size |

---

## 📡 Complete API Reference

### 1. Order Management (`/orders`)

#### `POST /ingest-orders`

Seeds default mock order dataset into the PostgreSQL database.

* **Response (`200 OK`):**
```json
"Orders successfully ingested into PostgreSQL."
```

---

#### `POST /orders/bulk`

Bulk ingests an array of customer order items.

* **Request Body:**
```json
[
  {
    "orderId": "ORD-3001",
    "customerId": "CUST-301",
    "customerEmail": "dave@example.com",
    "purchaseDate": "2026-09-25T10:00:00",
    "deliveryDate": "2026-09-28T14:00:00",
    "orderStatus": "DELIVERED",
    "totalAmount": 199.99,
    "itemsJson": "[{\"sku\": \"4K-WEBCAM-PRO\", \"name\": \"4K Ultra HD Webcam\", \"quantity\": 1}]"
  }
]
```

* **Response (`200 OK`):**
```json
"Successfully ingested 1 orders into PostgreSQL."
```

---

#### `GET /orders/{orderId}`

Retrieves order details for a specific order ID.

* **curl Example:**
```bash
curl -X GET http://localhost:8083/orders/ORD-3001
```

* **Response (`200 OK`):**
```json
{
  "orderId": "ORD-3001",
  "customerId": "CUST-301",
  "customerEmail": "dave@example.com",
  "purchaseDate": "2026-09-25T10:00:00",
  "deliveryDate": "2026-09-28T14:00:00",
  "orderStatus": "DELIVERED",
  "totalAmount": 199.99,
  "itemsJson": "[{\"sku\": \"4K-WEBCAM-PRO\", \"name\": \"4K Ultra HD Webcam\", \"quantity\": 1}]"
}
```

---

### 2. Knowledge Base & Agent Execution (`/agent`)

#### `POST /agent/ingest-policies`

Parses and embeds Markdown policy files into `pgvector`.

* **Response (`200 OK`):**
```json
"All policy Markdown files successfully ingested into PgVector."
```

---

#### `POST /agent/evaluate`

Submits a ticket for AI agent processing, function calling, policy checking, and decision persistence.

* **Request Body:**
```json
{
  "conversationId": "conv-cust-301",
  "ticketId": "TCK-3001",
  "issueDescription": "I bought a 4K Ultra HD Webcam under order ORD-3001 delivered 3 days ago. It does not fit my setup and I want to return it for a refund."
}
```

* **Response (`200 OK`):**
```json
{
  "ticketId": "TCK-3001",
  "decision": "Recommend for Refund Review",
  "priority": "MEDIUM",
  "reasoning": "The customer is requesting a return for a 4K Ultra HD Webcam (ORD-3001). The item was delivered on 2026-09-28 and the ticket was created on 2026-10-03, meaning 5 days have elapsed. This falls within the 7-calendar-day return window specified in KB-REFUND-001. As per policy, refund requests require human approval.",
  "requiresHumanApproval": true,
  "citedPolicyDoc": "KB-REFUND-001"
}
```

---

### 3. Human-In-The-Loop Queue (`/agent/pending-approvals`)

#### `GET /agent/pending-approvals`

Returns all tickets currently flagged with `PENDING_HUMAN_APPROVAL`.

* **Response (`200 OK`):**
```json
[
  {
    "ticketId": "TCK-3001",
    "conversationId": "conv-cust-301",
    "issueDescription": "I bought a 4K Ultra HD Webcam under order ORD-3001 delivered 3 days ago...",
    "proposedDecision": "Recommend for Refund Review",
    "finalDecision": null,
    "priority": "MEDIUM",
    "status": "PENDING_HUMAN_APPROVAL",
    "requiresHumanApproval": true,
    "reasoning": "The customer is requesting a return...",
    "citedPolicyDoc": "KB-REFUND-001",
    "humanReviewerNotes": null,
    "createdAt": "2026-10-03T17:47:36.613338",
    "reviewedAt": null
  }
]
```

---

#### `POST /agent/review`

Submits a human agent decision for a pending ticket.

* **Request Body:**
```json
{
  "ticketId": "TCK-3001",
  "approved": true,
  "reviewerNotes": "Approved return request within 7-day policy window."
}
```

* **Response (`200 OK`):**
```json
{
  "ticketId": "TCK-3001",
  "conversationId": "conv-cust-301",
  "issueDescription": "I bought a 4K Ultra HD Webcam under order ORD-3001 delivered 3 days ago...",
  "proposedDecision": "Recommend for Refund Review",
  "finalDecision": "Recommend for Refund Review",
  "priority": "MEDIUM",
  "status": "APPROVED_BY_HUMAN",
  "requiresHumanApproval": true,
  "reasoning": "The customer is requesting a return...",
  "citedPolicyDoc": "KB-REFUND-001",
  "humanReviewerNotes": "Approved return request within 7-day policy window.",
  "createdAt": "2026-10-03T17:47:36.613338",
  "reviewedAt": "2026-10-03T17:47:36.766452"
}
```

---

## 🧪 Testing & Verification

The project includes an automated end-to-end bash test script (`test_hitl.sh`).

### Execution Command:

```bash
chmod +x test_hitl.sh
./test_hitl.sh
```

### What the Test Script Validates:

1. Ingests mock seed orders into PostgreSQL.
2. Ingests policy documentation Markdown files into PgVector.
3. Submits ticket `TCK-3001` to trigger tool calls (`getOrderDetails` + `queryPolicyKnowledgeBase`).
4. Confirms `TCK-3001` enters `PENDING_HUMAN_APPROVAL` status.
5. Verifies ticket presence via `/agent/pending-approvals`.
6. Executes human approval via `/agent/review`.
7. Asserts the pending HITL queue is cleared (`[]`).

---

## 🔍 Troubleshooting & Performance Tuning

### 1. `Failed to generate content` or API Quota Limits

* **Symptom:** AI execution crashes with HTTP 429 or generation failure.
* **Cause:** Attempting to call commercial APIs (e.g., Google Gemini free tier) past rate/daily limits.
* **Resolution:** Ensure `spring.ai.ollama.base-url` points to a local Ollama instance running `gemma4:31b-cloud`.

### 2. Vector Dimensions Mismatch

* **Symptom:** PostgreSQL throws `ERROR: different vector dimensions`.
* **Cause:** The dimension setting in `application.properties` does not match your embedding model output.
* **Resolution:** Ensure `spring.ai.vectorstore.pgvector.dimensions` matches your active embedding model (e.g., `384` for all-MiniLM-L6-v2).

### 3. Tool Execution Exception Handling

* **Symptom:** LLM prompt execution abruptly stops when a lookup fails.
* **Cause:** Tool method throwing uncaught Java runtime exceptions back into Spring AI's advisor chain.
* **Resolution:** Ensure tool `@Bean` methods return structured JSON strings describing errors rather than rethrowing exceptions.

---

## 🛡️ AI Governance & Compliance

### 1. Agent Guardrails
The system implements a strict guardrail layer defined in `src/main/resources/guardrails/main_agent_guardrail.md` to ensure safety and reliability:
- **Prompt Injection Defense**: Strict directives to treat all external inputs as data, not instructions.
- **Deterministic Workflow**: Mandatory order of operations (Order Lookup $\rightarrow$ Policy Lookup $\rightarrow$ Evaluation).
- **Safety Escalation**: Automated `URGENT` flagging for safety hazards.
- **Constraint-Based Decisions**: Prohibits promising refunds or replacements without human approval.
- **Temporal Grounding**: Requires evaluation against ticket `created_at` timestamps.

### 2. Embedding & Vector Policy
- **Embedding Model**: Uses `bge-small-en-v1.5` (ONNX Transformers) for local, high-performance embedding generation.
- **Dimensionality**: 384 dimensions.
- **Vector Store**: `PgVector` utilizing HNSW indexing for efficient similarity search.
- **Distance Metric**: Cosine Similarity to ensure semantic alignment between user queries and policy documentation.

### 3. Operational Policies
- **HITL Mandatory Review**: All high-risk decisions are routed to `PENDING_HUMAN_APPROVAL`.
- **Grounding Requirement**: Every agent decision must be explicitly cited from the retrieved Knowledge Base (e.g., `KB-REFUND-001`).
- **Data Isolation**: Relational data (Orders) and Unstructured data (Policies) are fetched via separate, specialized tools to maintain strict data boundaries.
