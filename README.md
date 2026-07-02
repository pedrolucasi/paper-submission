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
mvn exec:java -Dexec.mainClass="br.edu.ifpb.cstsi.pss.scireview.Main"
```

Ou, após o `mvn package`:

```bash
java -cp target/paper-submission-1.0-SNAPSHOT.jar br.edu.ifpb.cstsi.pss.scireview.Main
```

Saída esperada:

```
SciReview - Sistema de Submissão de Artigos
```

## Carga de dados via CSV (E1)

Os dados iniciais do sistema são carregados automaticamente a partir de arquivos CSV em `src/main/resources/dados/`:

| Arquivo | Conteúdo |
|---------|----------|
| `usuarios.csv` | Usuários do sistema (email, senha, instituição, papéis) |
| `evento.csv` | Configuração do evento ativo |
| `areas.csv` | Áreas temáticas |
| `revisores_areas.csv` | Associação entre revisores e áreas |
| `artigos.csv` | Artigos submetidos pelos autores |

### Formato dos arquivos

Delimitador: `;` (ponto e vírgula). A primeira linha de cada arquivo é o cabeçalho.

**usuarios.csv**
```
email;senha;instituicao;papeis
coordenador@evento.com;senha123;IFPB;COORDENADOR
autor1@email.com;senha123;UFPB;AUTOR
revisor1@email.com;senha123;USP;REVISOR
```

Papéis múltiplos podem ser separados por `|` (ex.: `AUTOR|REVISOR`).

**evento.csv**
```
nome;cidade;periodo;dias_inicio_antes_hoje;dias_fim_depois_hoje
Simposio Brasileiro de Sistemas de Informacao - 2026;Vitoria - ES;25 de maio a 28 de maio de 2026;30;30
```

Os campos de dias definem o prazo de submissão em relação à data de execução (`hoje - N` até `hoje + N`).

**areas.csv**
```
nome
Inteligencia Artificial
Machine Learning
```

**revisores_areas.csv**
```
email_revisor;area
revisor1@email.com;Inteligencia Artificial
```

**artigos.csv**
```
email_autor;titulo;resumo;coautores;areas;paginas;recomendado
autor1@email.com;Titulo do artigo;Resumo do artigo;coautor1@email.com;Inteligencia Artificial|Machine Learning;10;true
```

Coautores e áreas múltiplos podem ser separados por `|` (ex.: `coautor1@email.com|coautor2@email.com`). Coautores devem estar cadastrados em `usuarios.csv`. O campo `recomendado` (`true`/`false`) orienta a simulação de pareceres na demonstração.

Para alterar os dados de demonstração, edite os CSVs e execute novamente a aplicação.
