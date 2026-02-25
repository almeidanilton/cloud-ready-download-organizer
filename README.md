# ☁️ Cloud Ready Download Organizer

Sistema desenvolvido em **Java** para organização automática da pasta Downloads, com classificação baseada em regras e preparado para futura integração com armazenamento em nuvem.

---

## 🚀 Sobre o Projeto

Este projeto nasceu de uma necessidade real do meu dia a dia: manter organizada a pasta Downloads, especialmente trabalhando com arquivos técnicos como STL, bibliotecas do Exocad e pacotes compactados.

A solução automatiza a organização aplicando regras específicas e gera um relatório detalhado da execução.

---

## ⚙️ Funcionalidades

- 📂 Classificação automática de arquivos:
  - **EXOCAD** (implantes, anatomia, bibliotecas, scanbody etc.)
  - **STL** (.stl)
  - **PACOTES** (.rar, .zip, .7z)
  - **OUTROS**
- 🔁 Modo seguro (**COPY**)  
- 🧹 Modo limpeza (**MOVE**)  
- 📄 Geração automática de relatório (.txt)  
- 🗂️ Criação dinâmica de pastas por categoria  
- 📊 Resumo da execução no console  

---

## 🛠️ Tecnologias Utilizadas

- Java
- `java.nio.file` (NIO)
- Stream API
- Manipulação de arquivos
- Tratamento de exceções

---

## ▶️ Como Executar

No IntelliJ (Program arguments):

### Modo seguro (não remove da origem)
```
"D:\BACKUP NOVO\Downloads" "D:\Ze-arquivos" COPY
```

### Modo limpeza (remove da origem)
```
"D:\BACKUP NOVO\Downloads" "D:\Ze-arquivos" MOVE
```

---

## 📂 Estrutura Gerada

```
D:\Ze-arquivos
 ├── EXOCAD
 ├── STL
 ├── PACOTES
 └── OUTROS
```

---

## 📄 Relatório

Após a execução, o sistema gera automaticamente um arquivo:

```
relatorio-organizacao-YYYY-MM-DD-HH-mm-ss.txt
```

Contendo:

- Data e hora
- Origem e destino
- Modo de execução
- Total processado
- Erros (se houver)

---

## 📈 Roadmap

Próximas evoluções planejadas:

- ☁️ Integração com armazenamento em nuvem
- ⏰ Execução automática agendada
- 📦 Empacotamento como .jar executável
- 🖥️ Interface gráfica simples

---

## 💡 Motivação

Mais do que um exercício técnico, este projeto representa a aplicação prática da programação para resolver problemas reais e automatizar tarefas repetitivas do fluxo de trabalho.

---
