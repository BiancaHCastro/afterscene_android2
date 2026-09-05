# AfterScene

O **AfterScene** é um aplicativo Android desenvolvido em **Kotlin** com o objetivo de permitir ao usuário criar um catálogo pessoal de filmes, séries, animes e doramas, registrando não apenas informações da obra, mas também a emoção que ela despertou durante a experiência.

O aplicativo foi desenvolvido como projeto para o curso de Android do Capacita, priorizando uma implementação simples, organizada e seguindo os principais conceitos de desenvolvimento Android moderno.

O projeto foi **melhorado posteriormente para incluir uma terceira tela de recomendações baseadas em humor (Mood)**. Essa nova funcionalidade utiliza **Retrofit para comunicação com uma API própria desenvolvida em FastAPI**, permitindo consultar recomendações e sugerir novas obras para cada categoria de humor.

A biblioteca pessoal do usuário continua sendo armazenada exclusivamente no **Room Database**.

---

# Funcionalidades

O aplicativo permite:

* Visualizar uma biblioteca de obras cadastradas;
* Pesquisar obras pelo título;
* Adicionar novas obras;
* Editar obras existentes;
* Excluir registros;
* Selecionar uma imagem da galeria para representar a capa da obra;
* Armazenar permanentemente as informações utilizando **Room Database**;
* Acessar recomendações organizadas por diferentes moods;
* Consultar recomendações através de uma API utilizando `GET`;
* Sugerir novas obras para um mood utilizando `POST`.

A ideia é executar o projeto cadastrando suas próprias obras consumidas, sejam filmes, séries, animes ou doramas, e utilizando a área de recomendações para descobrir novas obras de acordo com seu humor.

---

# Tecnologias Utilizadas

O projeto Android utiliza:

* Kotlin
* Jetpack Compose
* Material 3
* Navigation Compose
* Room Database
* MVVM (Model-View-ViewModel)
* Repository Pattern
* StateFlow
* Kotlin Coroutines
* Coil
* Activity Result API
* Retrofit

A API de recomendações utiliza:

* Python
* FastAPI
* Uvicorn
* Arquivo JSON para armazenamento das recomendações

---

# Arquitetura

O projeto utiliza a arquitetura **MVVM (Model-View-ViewModel)**, promovendo a separação entre interface, lógica de negócio e acesso aos dados.

O fluxo principal da biblioteca continua sendo:

```text
Interface (Jetpack Compose)
        ↓
ViewModel
        ↓
Repository
        ↓
Room Database
```

Para as recomendações, foi adicionada uma segunda fonte de dados:

```text
Interface (Jetpack Compose)
        ↓
ViewModel
        ↓
Repository
        ↓
Retrofit
        ↓
FastAPI
        ↓
moods.json
```

Dessa forma, o **Room continua responsável pela biblioteca pessoal**, enquanto a API é responsável pelas recomendações baseadas em humor.

---

# Persistência de Dados

Para a biblioteca pessoal, foi utilizado exclusivamente o **Room Database**, com armazenamento local no dispositivo.

Não são utilizados:

* Firebase;
* MongoDB;
* outros bancos de dados externos;
* autenticação;
* sistema de login.

A API criada para as recomendações utiliza apenas um arquivo `moods.json` para armazenar as categorias e obras recomendadas.

---

# Estrutura das Telas

O aplicativo possui três telas principais:

### 1. Biblioteca

Exibe as obras cadastradas pelo usuário e permite pesquisar, editar e excluir registros.

### 2. Cadastro/Detalhes

Permite adicionar ou editar uma obra, incluindo informações como título, tipo, gênero, emoção, nota, descrição e capa.

### 3. Recomendações por Mood

Permite selecionar uma categoria de humor e consultar obras recomendadas através da API.

Também é possível sugerir uma nova obra utilizando uma requisição `POST`.

---

# Como Executar o Projeto

O projeto possui **duas partes que precisam estar em execução** para testar completamente a aplicação:

```text
AfterScene
├── Aplicativo Android
└── API FastAPI
```

## 1. Executar a API

Entre na pasta da API:

```bash
cd afterscene-api
```

Crie o ambiente virtual:

```bash
python -m venv .venv
```

Ative o ambiente virtual no Windows:

```bash
.venv\Scripts\activate
```

Instale as dependências:

```bash
pip install -r requirements.txt
```

Execute o servidor:

```bash
uvicorn main:app --reload
```

A API estará disponível localmente em:

```text
http://127.0.0.1:8000
```

Para o Android Emulator, o aplicativo deve utilizar:

```text
http://10.0.2.2:8000/
```

O endereço `10.0.2.2` é necessário porque, dentro do emulador Android, `localhost` representa o próprio dispositivo virtual e não o computador que está executando a API.

---

## 2. Executar o aplicativo Android

Abra a pasta do projeto no **Android Studio**.

Aguarde a sincronização do Gradle e verifique se o SDK e o dispositivo virtual estão configurados corretamente.

Depois:

```text
Run ▶
```

Inicie o Android Emulator e execute o aplicativo.

Para testar a terceira tela e as recomendações, **a API FastAPI deve permanecer executando no terminal** enquanto o aplicativo estiver sendo utilizado.

---

# Desenvolvimento

Um dos principais desafios encontrados durante o desenvolvimento não esteve relacionado apenas à implementação das funcionalidades, mas também à configuração do ambiente.

A integração entre as bibliotecas do Jetpack Compose, Room, Navigation Compose, Coil, Retrofit e suas respectivas versões exigiu atenção para garantir compatibilidade entre o **Android Studio**, **Gradle**, **Android Gradle Plugin (AGP)** e **Kotlin**.

Também foi necessário utilizar um template padrão para os arquivos de configuração do Gradle (`build.gradle` e `libs.versions.toml`). Por esse motivo, algumas dependências presentes no projeto não são necessariamente utilizadas diretamente na implementação do aplicativo, mas permanecem por fazerem parte da configuração padrão gerada.

Outro desafio foi executar corretamente o projeto no Android Studio, principalmente durante os testes no emulador, envolvendo sincronização do Gradle, configuração do SDK, reconstrução do projeto e resolução de erros relacionados ao ambiente de desenvolvimento.

Com a inclusão da API, também foi necessário configurar a comunicação entre o Android Emulator e o servidor local, utilizando `10.0.2.2` como endereço da máquina hospedeira.

---

# Uso de Inteligência Artificial

Foi utilizado:

* **Uso da IA Gemini do Google como auxiliar de correção de código Kotlin e ChatGPT para escrita deste arquivo README.**

---

# Considerações Finais

O projeto permitiu aplicar, de forma prática, conceitos fundamentais do desenvolvimento Android moderno, como a construção de interfaces declarativas com Jetpack Compose, navegação entre telas, persistência local utilizando Room Database e organização da aplicação por meio da arquitetura MVVM.

A evolução do projeto acrescentou uma terceira tela de **recomendações por humor**, introduzindo comunicação com uma API própria através do Retrofit e FastAPI, sem alterar a função do Room como banco de dados local da biblioteca pessoal.

Mesmo sendo um aplicativo simples, o desenvolvimento proporcionou experiência com organização do código, gerenciamento de estado, separação de responsabilidades, persistência de dados e comunicação entre uma aplicação Android e uma API, formando uma base importante para projetos Android de maior complexidade.
