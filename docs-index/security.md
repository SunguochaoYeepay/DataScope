# 安全设计

## 1. 数据源密码加密

### 1.1 加密算法实现

```java
@Component
public class PasswordEncryption {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    
    @Value("${datascope.encryption.key}")
    private String encryptionKey;
    
    public String encrypt(String password, String salt) {
        try {
            byte[] iv = generateIv();
            SecretKey key = generateKey(encryptionKey, salt);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            
            byte[] cipherText = cipher.doFinal(password.getBytes());
            byte[] encrypted = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, encrypted, 0, iv.length);
            System.arraycopy(cipherText, 0, encrypted, iv.length, cipherText.length);
            
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new SecurityException("Password encryption failed", e);
        }
    }
    
    public String decrypt(String encryptedPassword, String salt) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedPassword);
            
            byte[] iv = new byte[IV_LENGTH_BYTE];
            byte[] cipherText = new byte[decoded.length - IV_LENGTH_BYTE];
            System.arraycopy(decoded, 0, iv, 0, IV_LENGTH_BYTE);
            System.arraycopy(decoded, IV_LENGTH_BYTE, cipherText, 0, cipherText.length);
            
            SecretKey key = generateKey(encryptionKey, salt);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            
            return new String(cipher.doFinal(cipherText));
        } catch (Exception e) {
            throw new SecurityException("Password decryption failed", e);
        }
    }
    
    private SecretKey generateKey(String key, String salt) {
        try {
            KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new SecurityException("Key generation failed", e);
        }
    }
    
    private byte[] generateIv() {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
}
```

### 1.2 密钥管理配置

```yaml
datascope:
  encryption:
    key: ${ENCRYPTION_KEY}  # 从环境变量获取
    rotation:
      enabled: true
      interval: 90d  # 90天轮换一次
    backup:
      enabled: true
      location: /secure/keys/backup
```

## 2. SQL注入防护

### 2.1 参数化查询处理

```java
@Component
public class SQLSanitizer {
    private static final Pattern UNSAFE_PATTERN = Pattern.compile(
        "(?i)(\\b(select|insert|update|delete|drop|union|alter)\\b)"
    );
    
    public String sanitize(String sql) {
        // 检查SQL注入风险
        if (UNSAFE_PATTERN.matcher(sql).find()) {
            throw new SecurityException("Potential SQL injection detected");
        }
        return sql;
    }
    
    public PreparedStatement prepareStatement(Connection conn, String sql, Map<String, Object> params) {
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                setParameter(stmt, entry.getKey(), entry.getValue());
            }
            return stmt;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to prepare statement", e);
        }
    }
    
    private void setParameter(PreparedStatement stmt, String name, Object value) {
        // 参数绑定实现
    }
}
```

### 2.2 SQL白名单配置

```yaml
sql:
  whitelist:
    tables:
      - name: "users"
        operations: ["SELECT"]
      - name: "orders"
        operations: ["SELECT"]
    patterns:
      - "^SELECT .* FROM allowed_table WHERE .*$"
      - "^SELECT COUNT\\(\\*\\) FROM allowed_table.*$"
```

## 3. 访问控制

### 3.1 API访问限流

```java
@Configuration
public class RateLimitConfig {
    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.builder()
            .requestsPerSecond(100)
            .burstSize(200)
            .warmupPeriod(Duration.ofSeconds(10))
            .build();
    }
}

@Aspect
@Component
public class RateLimitAspect {
    private final RateLimiter rateLimiter;
    
    @Around("@annotation(RateLimit)")
    public Object rateLimit(ProceedingJoinPoint point) throws Throwable {
        if (!rateLimiter.tryAcquire()) {
            throw new TooManyRequestsException();
        }
        return point.proceed();
    }
}
```

### 3.2 查询频率限制

```java
@Service
public class QueryRateLimiter {
    private final LoadingCache<String, RateLimiter> userLimiters;
    
    public QueryRateLimiter() {
        userLimiters = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build(new CacheLoader<String, RateLimiter>() {
                @Override
                public RateLimiter load(String userId) {
                    return RateLimiter.create(10.0); // 每秒10个请求
                }
            });
    }
    
    public boolean allowQuery(String userId) {
        return userLimiters.getUnchecked(userId).tryAcquire();
    }
}
```

## 4. 数据脱敏

### 4.1 脱敏规则配置

```json
{
  "maskingRules": {
    "default": {
      "type": "partial",
      "keepStart": 3,
      "keepEnd": 4,
      "maskChar": "*"
    },
    "patterns": [
      {
        "type": "regex",
        "field": "email",
        "pattern": "(?<=.{3}).(?=.*@)",
        "replacement": "*"
      },
      {
        "type": "regex",
        "field": "phone",
        "pattern": "(\\d{3})\\d{4}(\\d{4})",
        "replacement": "$1****$2"
      },
      {
        "type": "custom",
        "field": "idCard",
        "maskStart": 6,
        "maskLength": 8,
        "maskChar": "*"
      }
    ]
  }
}
```

### 4.2 脱敏处理实现

```java
@Component
public class DataMasker {
    private final Map<String, MaskingRule> rules;
    
    public Object mask(String fieldName, Object value) {
        if (value == null) {
            return null;
        }
        
        MaskingRule rule = rules.get(fieldName);
        if (rule == null) {
            return value;
        }
        
        return switch (rule.getType()) {
            case PARTIAL -> maskPartial(value.toString(), rule);
            case REGEX -> maskRegex(value.toString(), rule);
            case CUSTOM -> maskCustom(value.toString(), rule);
            default -> value;
        };
    }
    
    private String maskPartial(String value, MaskingRule rule) {
        // 部分掩码实现
    }
    
    private String maskRegex(String value, MaskingRule rule) {
        // 正则掩码实现
    }
    
    private String maskCustom(String value, MaskingRule rule) {
        // 自定义掩码实现
    }
}
```

## 5. 审计日志

### 5.1 审计日志配置

```yaml
audit:
  enabled: true
  log:
    path: /var/log/datascope/audit
    retention: 90d
  events:
    - type: DATA_ACCESS
      level: INFO
    - type: CONFIG_CHANGE
      level: WARN
    - type: SECURITY_VIOLATION
      level: ERROR
```

### 5.2 审计日志实现

```java
@Aspect
@Component
public class AuditLogAspect {
    private final AuditLogger auditLogger;
    
    @Around("@annotation(Audited)")
    public Object audit(ProceedingJoinPoint point) throws Throwable {
        AuditEvent event = new AuditEvent();
        event.setTimestamp(LocalDateTime.now());
        event.setUser(SecurityContextHolder.getContext().getAuthentication().getName());
        event.setAction(point.getSignature().getName());
        event.setParameters(Arrays.toString(point.getArgs()));
        
        try {
            Object result = point.proceed();
            event.setStatus("SUCCESS");
            return result;
        } catch (Exception e) {
            event.setStatus("FAILED");
            event.setError(e.getMessage());
            throw e;
        } finally {
            auditLogger.log(event);
        }
    }
}
```

## 6. 安全配置

### 6.1 应用安全配置

```yaml
security:
  headers:
    content-security-policy: "default-src 'self'"
    x-frame-options: DENY
    x-content-type-options: nosniff
    x-xss-protection: "1; mode=block"
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEY_STORE_PASSWORD}
    key-alias: datascope
  cors:
    allowed-origins: 
      - "https://*.company.com"
    allowed-methods:
      - GET
      - POST
      - PUT
      - DELETE
    allowed-headers:
      - Authorization
      - Content-Type
    max-age: 3600
```

### 6.2 安全过滤器配置

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
            .headers()
                .contentSecurityPolicy("default-src 'self'")
                .and()
                .frameOptions()
                .deny()
                .and()
                .xssProtection()
                .and()
                .contentTypeOptions()
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .antMatchers("/api/v1/public/**").permitAll()
                .anyRequest().authenticated();
    }
}
```