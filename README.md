## 📄 SciReview - Sistema de Submissão de Artigos

Boas vindas ao repositório do projeto SciReview, desenvolvido como trabalho final da disciplina de Padrões de Projeto de Software, do curso de Sistemas para Internet no instituto Federal da Paraíba (IFPB).

A aplicação consiste em um sistema de terminal que gerencia todo o fluxo de submissão e avaliação por pares de artigos científicos em um evento acadêmico, seguindo o protocolo *blind review* — autores e revisores permanecem anônimos entre si.

O sistema gira em torno de três papéis:

- **Coordenador (chair):** prepara o evento, define a categoria dos artigos (Full Paper, Short Paper ou Demo), cadastra as áreas temáticas e registra os revisores do comitê técnico.
- **Autor:** submete artigos (título, resumo, coautores e áreas de interesse) dentro do prazo e acompanha o status de cada submissão.
- **Revisor:** declara suas áreas de interesse e emite pareceres sobre os artigos que lhe são atribuídos.

Cada artigo percorre um ciclo de vida bem definido — **submetido → em revisão → revisado → aceito/rejeitado**. A distribuição dos artigos aos revisores é automática, equilibrando a carga e priorizando a afinidade de área, sem que um revisor receba o próprio trabalho. Ao final do ciclo, os autores são notificados por e-mail com o parecer consolidado dos revisores.

**Disciplina:** Padrões de Projeto de Software — 5º período (Sistemas para Internet, IFPB)
**Professor:** Alex Cunha
**Equipe:** Suetone Carneiro, Pedro Lucas e Pedro Arthur

## 🧩 Padrões de Projeto aplicados

A solução emprega **seis padrões de projeto**, cada um resolvendo uma necessidade concreta do domínio. Os diagramas de classe de cada padrão (PlantUML) estão em [`docs/diagramas/padroes/`](docs/diagramas/padroes/).

| # | Padrão | Onde está (classes) | RF / uso |
|---|--------|---------------------|----------|
| 1 | **State** | `model.estado`: `EstadoArtigo`, `Submetido`, `EmRevisao`, `Revisado`, `Aceito`, `Rejeitado` (contexto: `Artigo`) | **RF05** — ciclo de vida/status do artigo, com transições válidas garantidas pelo próprio estado |
| 2 | **Strategy** | `model.categoria`: `CategoriaArtigo`, `FullPaper`, `ShortPaper`, `Demo` (contexto: `Evento`; consumo: `SubmissaoArtigo`) | **RF04** — regras de submissão por categoria (limite de páginas e resumo), sem `if/else` por tipo |
| 3 | **Observer** | `observer.Observer`, `service.ServicoEmail` (observer), `service.GerenciadorEvento.notificarObservadores()` (subject) | **RF09** — notificação por e-mail aos autores ao final do ciclo de revisões |
| 4 | **Template Method** | `service.GeradorEmail` → `EmailAceite` / `EmailRejeicao` | **RF09** — corpo do e-mail: esqueleto fixo (`gerarEmail`) com variações por resultado (aceite/rejeição) |
| 5 | **Command** | `command.Command` + os sete `*Command`, orquestrados por `command.CommandHistory` (invoker) | **RF10** — ações do coordenador encapsuladas, auditáveis e com *undo* |
| 6 | **Singleton** | `command.CommandHistory.getInstance()` | **RF10** — histórico único e global de ações em toda a aplicação |

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

### Gerar o pacote (JAR)

```bash
mvn package
```

### Executar a aplicação

Via Maven (a partir da raiz do projeto):

```bash
mvn exec:java -Dexec.mainClass="br.edu.ifpb.cstsi.pss.scireview.Main"
```

Ou, após o `mvn package`:

```bash
java -cp target/paper-submission-1.0-SNAPSHOT.jar br.edu.ifpb.cstsi.pss.scireview.Main
```

Ao iniciar, a aplicação pergunta em qual **modo** você deseja executá-la:

```
Escolha o modo de execucao:
  1) Modo exemplo    (demonstracao automatica de todos os RFs)
  2) Modo interativo (uso manual via terminal)
```

### Modo exemplo (1)

Executa, sem interação, uma demonstração completa de ponta a ponta a partir dos dados dos CSVs: inicia o evento, define a categoria, cadastra áreas, monta o comitê, submete os artigos, distribui aos revisores, coleta os pareceres, decide o resultado, notifica os autores e exibe o histórico de ações (RF10) com um *undo*. É a forma mais rápida de ver todos os requisitos funcionando.

### Modo interativo (2)

Abre um menu de terminal em que o próprio usuário exercita cada requisito manualmente — cadastrar-se, cadastrar áreas, submeter artigos, distribuir, revisar, decidir e notificar. Os dados iniciais continuam vindo dos CSVs, e **os novos dados criados durante a sessão são persistidos de volta** nos arquivos em `src/main/resources/dados/` (ver seção seguinte).

| Opção | Requisito | Ação |
|-------|-----------|------|
| 1  | RF02 | Cadastrar usuário |
| 2  | RF03 | Cadastrar área temática *(coordenador)* |
| 3  | RF03 | Revisor declara área de interesse |
| 4  | RF04 | Definir categoria do evento *(coordenador)* |
| 5  | RF04 | Registrar revisor no comitê *(coordenador)* |
| 6  | RF05 | Submeter artigo *(autor)* |
| 7  | RF05 | Listar meus artigos e status *(autor)* |
| 8  | RF06 | Distribuir artigos aos revisores *(coordenador)* |
| 9  | RF07 | Revisor emite parecer |
| 10 | RF08 | Dashboard |
| 11 | RF09 | Decisão final + notificação aos autores *(coordenador)* |
| 12 | RF10 | Histórico de ações / desfazer |
| 13 | RF01 | Iniciar novo evento *(coordenador)* |
| 0  | —    | Sair |

Notas de uso:

- Cada artigo recebe **2 revisores** na distribuição; um artigo só passa ao estado `revisado` (elegível para a decisão final do RF09) depois que **ambos** os revisores emitem parecer.
- Para demonstrar todo o fluxo do zero, use a opção **13** (novo evento) e depois `4 → 2 → 3 → 5 → 6 → 8 → 9 → 11`.
- Ao emitir um parecer (opção 9), informe o **ID do artigo** exibido na lista de pendências (visão cega, sem autores).
- No modo interativo, o RF09 usa envio de e-mail **simulado** (o conteúdo é impresso no terminal), evitando a necessidade de credenciais SMTP.

Usuários de exemplo já disponíveis (senha `senha123`): `coordenador@evento.com`, `autor1@email.com`, `autor2@email.com`, `revisor1@email.com`, `revisor2@email.com`, `revisor3@email.com`.

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

### Persistência de novos dados

No **modo interativo**, os dados criados durante a sessão (novos usuários, áreas, associações e artigos) são gravados de volta nesses mesmos arquivos CSV — de modo que reaparecem na próxima execução. Para restaurar os arquivos ao estado inicial versionado:

```bash
git checkout src/main/resources/dados
```
