## 📄 SciReview - Sistema de Submissão de Artigos

Sistema de Submissão de Artigos Científicos para a disciplina de Padrões de Projeto de Software (PPS) do Curso Superior de Sistemas para Internet do Instituto Federal da Paraíba.

## Pré-requisitos

- [Java JDK 21+](https://adoptium.net/)
- [Apache Maven 3.9+](https://maven.apache.org/download.cgi)

Verifique a instalação:

```bash
java -version
mvn -version
```

## Como executar

Clone o repositório e entre na pasta do projeto:

```bash
git clone https://github.com/pedrolucasi/paper-submission.git
cd paper-submission
```

### Compilar o projeto

```bash
mvn clean compile
```

### Executar os testes

```bash
mvn test
```

### Gerar o pacote (JAR)

```bash
mvn package
```

### Executar a aplicação

Via Maven:

```bash
mvn exec:java -Dexec.mainClass="br.edu.ifpb.cstsi.pss.scireview.App"
```

Ou, após o `mvn package`:

```bash
java -cp target/paper-submission-1.0-SNAPSHOT.jar br.edu.ifpb.cstsi.pss.scireview.App
```

Saída esperada:

```
SciReview - Sistema de Submissão de Artigos
```
