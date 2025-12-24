# RAG 应用聚合服务

RAG 应用聚合服务是一个检索增强生成（RAG）系统的中央聚合服务，负责协调文档上传、向量存储和语言模型推理三个独立服务，为用户提供文档问答功能。

## 核心功能

- **文档上传处理**：接收用户上传的文档，调用RAG Ingestion服务进行文档解析和分块
- **向量存储管理**：将文档块向量化并存储到向量数据库，支持语义检索
- **智能问答推理**：基于用户问题和检索到的相关文档，调用语言模型生成准确回答

### 环境要求

- Java 17+
- Spring Boot 4.0.1
- 三个独立的下游服务：
    - RAG Ingestion 服务（文档解析服务）
    - RAG Vector 服务（向量存储与检索服务）
    - RAG Inference 服务（语言模型推理服务）

### 服务配置

在 [application.yaml](file://G:\github\rag-app\src\main\resources\application.yaml) 中配置下游服务地址：

```yaml
services:
  ingestion:
    base-url: http://localhost:{port}
  vector:
    base-url: http://localhost:{port}
  inference:
    base-url: http://localhost:{port}
```

配置系统提示词：
```yaml
rag:
  prompt:
    system-prompt:
```

### 接口文档

完整 API 说明请参阅：[API 文档](docs/api.md)

### 业务流程

1. **文档上传流程**：
    - 用户上传文档 → 调用RAG Ingestion服务解析文档为文本块 → 调用RAG Vector服务将文本块向量化并存储

2. **问答检索流程**：
    - 用户提交问题 → 调用RAG Vector服务进行语义检索 → 调用RAG Inference服务生成答案 → 返回用户

### 致谢

- OpenFeign 客户端 [OpenFeign](https://github.com/OpenFeign/feign)
- 微服务架构支持 [Spring Cloud](https://spring.io/projects/spring-cloud)