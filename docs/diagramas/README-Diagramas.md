# Diagramas — SciReview

## Diagrama de estados do Artigo

Arquivo: [`estado-artigo.puml`](estado-artigo.puml)

### Como visualizar

**Opção 1 — Site PlantUML**

1. Abra https://www.plantuml.com/plantuml
2. Cole o conteúdo de `estado-artigo.puml`
3. Exporte como PNG ou SVG

**Opção 2 — VS Code / Cursor**

1. Instale a extensão **PlantUML**
2. Abra `estado-artigo.puml`
3. Use `Alt+D` para pré-visualizar

**Opção 3 — Linha de comando**

```bash
java -jar plantuml.jar docs/diagramas/estado-artigo.puml
```

Gera `docs/diagramas/estado-artigo.png` na mesma pasta.

## Diagrama de classes (visão geral)

Arquivo: [`classes-visao-geral.puml`](classes-visao-geral.puml)

Diagrama em camadas com as classes principais, relacionamentos e os **6 padrões de projeto** destacados na legenda.

### Como visualizar

Mesmas opções do diagrama de estados — abra o `.puml` na extensão PlantUML ou em https://www.plantuml.com/plantuml

```bash
java -jar plantuml.jar docs/diagramas/classes-visao-geral.puml
```

### Camadas representadas

| Pacote | Conteúdo |
|--------|----------|
| `model` | Entidades de domínio |
| `model.estado` | State pattern (RF05) |
| `model.categoria` | Strategy pattern (RF04) |
| `service` | Regras de negócio |
| `observer` | Observer pattern (RF09) |
| `command` | Command + Singleton (RF10) |
| `loader` | E1 — CSV |
| `dashboard` | RF08 |
| `presentation` | Saída console (RNF) |

### Fluxo representado

```
Submetido → EmRevisao → Revisado → Aceito | Rejeitado
```

Corresponde ao **State pattern** em `model.estado` e ao fluxo executado no `Main`.

## Recortes dos 6 padrões de projeto

Pasta: [`padroes/`](padroes/)

Cada arquivo foca em **um padrão** com as classes e relações relevantes do código.

| # | Arquivo | Padrão | RF |
|---|---------|--------|-----|
| 1 | [`padroes/01-state.puml`](padroes/01-state.puml) | State | RF05 |
| 2 | [`padroes/02-strategy.puml`](padroes/02-strategy.puml) | Strategy | RF04 |
| 3 | [`padroes/03-observer.puml`](padroes/03-observer.puml) | Observer | RF09 |
| 4 | [`padroes/04-template-method.puml`](padroes/04-template-method.puml) | Template Method | RF09 |
| 5 | [`padroes/05-command.puml`](padroes/05-command.puml) | Command | RF10 |
| 6 | [`padroes/06-singleton.puml`](padroes/06-singleton.puml) | Singleton | RF10 / RNF |

### Como gerar todos de uma vez

```bash
java -jar plantuml.jar docs/diagramas/padroes/*.puml
```

Ou gere o conjunto completo (estados + classes + padrões):

```bash
java -jar plantuml.jar docs/diagramas/**/*.puml
```
