# API Financeiro - Backend ByteBank

## 📋 Visão Geral

A **API Financeiro** é o backend da aplicação ByteBank, desenvolvida em **Java 17** com **Spring Boot 3**. Ela fornece todos os endpoints REST necessários para gerenciar usuários, contas bancárias e transações financeiras.

A API utiliza banco de dados **H2** (em memória) para desenvolvimento e testes, e está preparada para migração para bancos de dados em produção (PostgreSQL, MySQL).

## 🎯 Responsabilidades

### 1. **Autenticação e Autorização**
- Login de usuários com JWT
- Registro de novos usuários
- Validação de tokens
- Gerenciamento de sessões

### 2. **Gerenciamento de Contas**
- Criação de contas bancárias
- Consulta de saldo
- Listagem de contas por usuário
- Atualização de dados da conta

### 3. **Transações Financeiras**
- Depósitos
- Saques
- Transferências entre contas
- Consulta de extrato
- Histórico de transações

### 4. **Validações de Negócio**
- Saldo suficiente para saques/transferências
- Limites de transação
- Validação de dados de entrada
- Integridade referencial

## 🏗️ Arquitetura

```
api-financeiro/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── tach_challenge_2/
│   │   │       └── api_financeiro/
│   │   │           ├── ApiFinanceiroApplication.java  # Main class
│   │   │           ├── controller/                    # REST Controllers
│   │   │           │   ├── AuthController.java
│   │   │           │   ├── AccountController.java
│   │   │           │   └── TransactionController.java
│   │   │           ├── dto/                          # Data Transfer Objects
│   │   │           │   ├── LoginRequest.java
│   │   │           │   ├── RegisterRequest.java
│   │   │           │   ├── TransactionRequest.java
│   │   │           │   └── ApiResponse.java
│   │   │           ├── model/                        # Entities
│   │   │           │   ├── User.java
│   │   │           │   ├── Account.java
│   │   │           │   └── Transaction.java
│   │   │           ├── repository/                   # JPA Repositories
│   │   │           │   ├── UserRepository.java
│   │   │           │   ├── AccountRepository.java
│   │   │           │   └── TransactionRepository.java
│   │   │           ├── service/                      # Business Logic
│   │   │           │   ├── AuthService.java
│   │   │           │   ├── AccountService.java
│   │   │           │   └── TransactionService.java
│   │   │           ├── security/                     # Security Config
│   │   │           │   ├── JwtTokenProvider.java
│   │   │           │   ├── JwtAuthenticationFilter.java
│   │   │           │   └── SecurityConfig.java
│   │   │           ├── exception/                    # Exception Handling
│   │   │           │   ├── GlobalExceptionHandler.java
│   │   │           │   ├── ResourceNotFoundException.java
│   │   │           │   └── InsufficientBalanceException.java
│   │   │           └── config/                       # Configurations
│   │   │               ├── CorsConfig.java
│   │   │               └── DataInitializer.java
│   │   └── resources/
│   │       ├── application.properties                # Configurações
│   │       └── data.sql                             # Dados iniciais
│   └── test/
│       └── java/
│           └── tach_challenge_2/
│               └── api_financeiro/
│                   └── ApiFinanceiroApplicationTests.java
├── build.gradle                                      # Dependências Gradle
├── gradlew                                          # Gradle Wrapper (Unix)
├── gradlew.bat                                      # Gradle Wrapper (Windows)
├── Dockerfile                                       # Docker image
└── README.md                                        # Este arquivo
```

## 📦 Modelo de Dados

### User (Usuário)

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password; // Hash BCrypt
    
    @Column(nullable = false, unique = true)
    private String cpf;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Account> accounts;
}
```

### Account (Conta Bancária)

```java
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber; // Gerado automaticamente
    
    @Column(nullable = false)
    private String agency; // Ex: "0001"
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type; // CHECKING, SAVINGS
    
    @Column(nullable = false)
    private BigDecimal balance;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transaction> transactions;
}

public enum AccountType {
    CHECKING,  // Conta Corrente
    SAVINGS    // Poupança
}
```

### Transaction (Transação)

```java
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // DEPOSIT, WITHDRAWAL, TRANSFER
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private String category; // Ex: "Alimentação", "Transporte"
    
    @Column(nullable = false)
    private LocalDateTime date;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @Column(name = "target_account_id")
    private String targetAccountId; // Para transferências
    
    @Column(nullable = false)
    private BigDecimal balance; // Saldo resultante após transação
}

public enum TransactionType {
    DEPOSIT,      // Depósito
    WITHDRAWAL,   // Saque
    TRANSFER      // Transferência
}
```

## 🔌 Endpoints da API

### Autenticação

#### POST `/api/auth/register`
Registra novo usuário.

**Request Body:**
```json
{
  "name": "João Silva",
  "email": "joao@email.com",
  "password": "senha123",
  "cpf": "123.456.789-00"
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "Usuário registrado com sucesso",
  "data": {
    "id": "uuid",
    "name": "João Silva",
    "email": "joao@email.com"
  }
}
```

#### POST `/api/auth/login`
Autentica usuário e retorna JWT token.

**Request Body:**
```json
{
  "email": "joao@email.com",
  "password": "senha123"
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Login realizado com sucesso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": "uuid",
      "name": "João Silva",
      "email": "joao@email.com"
    }
  }
}
```

#### GET `/api/auth/me`
Retorna dados do usuário autenticado.

**Headers:**
```
Authorization: Bearer {token}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "name": "João Silva",
    "email": "joao@email.com",
    "cpf": "123.456.789-00"
  }
}
```

### Contas

#### GET `/api/account?userId={userId}`
Lista contas do usuário.

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
- `userId` (required): ID do usuário

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "accounts": [
      {
        "id": "uuid",
        "accountNumber": "00001234-5",
        "agency": "0001",
        "type": "CHECKING",
        "balance": 1500.00,
        "createdAt": "2024-01-01T10:00:00"
      }
    ]
  }
}
```

#### GET `/api/account/{accountId}`
Busca conta específica.

**Headers:**
```
Authorization: Bearer {token}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "accountNumber": "00001234-5",
    "agency": "0001",
    "type": "CHECKING",
    "balance": 1500.00,
    "userId": "user-uuid"
  }
}
```

#### POST `/api/account`
Cria nova conta bancária.

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "userId": "user-uuid",
  "type": "CHECKING",
  "initialBalance": 100.00
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "Conta criada com sucesso",
  "data": {
    "id": "uuid",
    "accountNumber": "00001234-5",
    "agency": "0001",
    "type": "CHECKING",
    "balance": 100.00
  }
}
```

#### GET `/api/account/{accountId}/statement`
Busca extrato da conta (todas as transações).

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters (opcionais):**
- `startDate`: Data inicial (formato: yyyy-MM-dd)
- `endDate`: Data final (formato: yyyy-MM-dd)
- `type`: Tipo de transação (DEPOSIT, WITHDRAWAL, TRANSFER)

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "account": {
      "id": "uuid",
      "accountNumber": "00001234-5",
      "balance": 1500.00
    },
    "transactions": [
      {
        "id": "uuid",
        "type": "DEPOSIT",
        "amount": 500.00,
        "description": "Depósito inicial",
        "category": "Depósito",
        "date": "2024-01-01T10:00:00",
        "balance": 500.00
      },
      {
        "id": "uuid",
        "type": "WITHDRAWAL",
        "amount": 100.00,
        "description": "Saque caixa eletrônico",
        "category": "Saque",
        "date": "2024-01-02T14:30:00",
        "balance": 400.00
      }
    ]
  }
}
```

### Transações

#### POST `/api/transaction`
Cria nova transação.

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body (Depósito):**
```json
{
  "accountId": "account-uuid",
  "type": "DEPOSIT",
  "amount": 500.00,
  "description": "Depósito em dinheiro",
  "category": "Depósito"
}
```

**Request Body (Saque):**
```json
{
  "accountId": "account-uuid",
  "type": "WITHDRAWAL",
  "amount": 100.00,
  "description": "Saque caixa eletrônico",
  "category": "Saque"
}
```

**Request Body (Transferência):**
```json
{
  "accountId": "account-uuid",
  "type": "TRANSFER",
  "amount": 200.00,
  "description": "Transferência para João",
  "category": "Transferência",
  "targetAccountId": "target-account-uuid"
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "Transação realizada com sucesso",
  "data": {
    "transaction": {
      "id": "uuid",
      "type": "DEPOSIT",
      "amount": 500.00,
      "description": "Depósito em dinheiro",
      "category": "Depósito",
      "date": "2024-01-01T10:00:00",
      "balance": 1500.00
    },
    "account": {
      "id": "account-uuid",
      "balance": 1500.00
    }
  }
}
```

#### GET `/api/transaction/{transactionId}`
Busca transação específica.

**Headers:**
```
Authorization: Bearer {token}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "type": "DEPOSIT",
    "amount": 500.00,
    "description": "Depósito em dinheiro",
    "category": "Depósito",
    "date": "2024-01-01T10:00:00",
    "accountId": "account-uuid",
    "balance": 1500.00
  }
}
```

## 🔒 Segurança

### JWT (JSON Web Token)

A API utiliza JWT para autenticação stateless:

**Estrutura do Token:**
```
Header: { "alg": "HS256", "typ": "JWT" }
Payload: { "sub": "user-id", "email": "user@email.com", "exp": 1234567890 }
Signature: HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
```

**Configuração:**
```properties
# application.properties
jwt.secret=bytebank-secret-key-change-in-production
jwt.expiration=86400000  # 24 horas em milissegundos
```

### Fluxo de Autenticação

```
1. Cliente faz POST /api/auth/login com email/senha
   ↓
2. API valida credenciais no banco
   ↓
3. Se válido, gera JWT token com informações do usuário
   ↓
4. Token é retornado ao cliente
   ↓
5. Cliente armazena token (localStorage/sessionStorage)
   ↓
6. Para requisições subsequentes, cliente envia:
   Header: Authorization: Bearer {token}
   ↓
7. JwtAuthenticationFilter intercepta e valida token
   ↓
8. Se válido, adiciona Authentication no SecurityContext
   ↓
9. Controller processa requisição com usuário autenticado
```

### Password Hashing

Senhas são criptografadas com BCrypt:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // Strength 12
}

// Ao criar usuário
String hashedPassword = passwordEncoder.encode(plainPassword);
user.setPassword(hashedPassword);

// Ao validar login
boolean matches = passwordEncoder.matches(plainPassword, user.getPassword());
```

### CORS Configuration

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins(
                "http://localhost:9000",
                "http://localhost:9001",
                "http://localhost:9002",
                "http://localhost:9003"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

## 🗄️ Banco de Dados

### H2 Database (Desenvolvimento)

```properties
# application.properties

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Datasource
spring.datasource.url=jdbc:h2:mem:bytebankdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

**Acessar Console H2:**
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:bytebankdb`
- Username: `sa`
- Password: (vazio)

### Dados Iniciais

```java
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Criar usuário de teste
        User user = new User();
        user.setName("João Silva");
        user.setEmail("joao@bytebank.com");
        user.setPassword(passwordEncoder.encode("senha123"));
        user.setCpf("123.456.789-00");
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        
        // Criar conta para o usuário
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setAgency("0001");
        account.setType(AccountType.CHECKING);
        account.setBalance(BigDecimal.valueOf(1000.00));
        account.setUser(user);
        account.setCreatedAt(LocalDateTime.now());
        accountRepository.save(account);
        
        log.info("Dados iniciais criados com sucesso!");
    }
    
    private String generateAccountNumber() {
        return String.format("%08d-%d", 
            ThreadLocalRandom.current().nextInt(10000000),
            ThreadLocalRandom.current().nextInt(10));
    }
}
```

## 🚀 Executando a Aplicação

### Requisitos

- Java 17 ou superior
- Gradle 8+ (incluído via wrapper)

### Comandos

#### Compilar o Projeto
```bash
./gradlew build
```

#### Executar Aplicação
```bash
./gradlew bootRun
```

#### Executar Testes
```bash
./gradlew test
```

#### Gerar JAR
```bash
./gradlew bootJar

# JAR gerado em: build/libs/api-financeiro.jar
# Executar:
java -jar build/libs/api-financeiro.jar
```

#### Limpar Build
```bash
./gradlew clean
```

### Docker

#### Build da Imagem
```bash
docker build -t bytebank-api:latest .
```

#### Executar Container
```bash
docker run -p 8080:8080 bytebank-api:latest
```

## 📊 Dependências (build.gradle)

```gradle
dependencies {
    // Spring Boot Starters
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    
    // JWT
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'
    
    // H2 Database
    runtimeOnly 'com.h2database:h2'
    
    // Lombok (opcional, para reduzir boilerplate)
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    
    // Testes
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
}
```

## 🧪 Testando a API

### Com cURL

```bash
# Registrar usuário
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"João Silva","email":"joao@email.com","password":"senha123","cpf":"123.456.789-00"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"joao@email.com","password":"senha123"}'

# Listar contas (substitua {token} e {userId})
curl -X GET "http://localhost:8080/api/account?userId={userId}" \
  -H "Authorization: Bearer {token}"

# Criar transação
curl -X POST http://localhost:8080/api/transaction \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{"accountId":"{accountId}","type":"DEPOSIT","amount":500.00,"description":"Depósito","category":"Depósito"}'
```

### Com Postman/Insomnia

1. Importar collection com todos os endpoints
2. Configurar variável de ambiente para token
3. Testar fluxo completo

## 🔍 Troubleshooting

### Porta 8080 já em uso
```bash
# Identificar processo
lsof -i :8080

# Matar processo
kill -9 {PID}
```

### Erro ao conectar ao H2
Verificar se `spring.h2.console.enabled=true` em application.properties.

### JWT Token inválido
Verificar se secret key está configurada corretamente.

## 📚 Recursos Adicionais

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/)
- [H2 Database Documentation](http://www.h2database.com/)

## 👥 Equipe

**FIAP Grupo 30 - Tech Challenge 2**
