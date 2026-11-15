# NeedleDrop

Projeto academico de microsservicos para uma plataforma de catalogo musical e sessoes de audicao.

Este repositorio tem dois servicos Spring Boot independentes, cada um com seu
banco PostgreSQL. O README mostra como executar o projeto, entender as decisoes e os testes.

## Servicos

- `catalogo-service`: consulta artistas, generos e albuns.
- `sessoes-service`: cria locais, sessoes de audicao e reservas de vagas.

Cada servico tem seu proprio banco PostgreSQL. Ao criar uma sessao, o servico
de sessoes consulta o catalogo por HTTP para confirmar que o album existe.
O endereco do catalogo e configurado pela variavel `CATALOGO_URL`.

## Arquitetura

```text
             Cliente (curl / navegador)
                  |          |
                  v          v
          catalogo:8081    sessoes:8082
                  |          |  |
                  |          |  +-- HTTP GET /albuns/{id} --> catalogo
                  |          |
                  v          v
             PostgreSQL   PostgreSQL
              catalogo     sessoes
```

O catalogo guarda artistas, generos e albuns. O servico de sessoes guarda
locais, sessoes, reservas e vagas. Ele consulta o catalogo por HTTP quando
precisa validar ou mostrar o album. Os bancos sao separados; nao existe
consulta direta entre os bancos.

O codigo e organizado com controllers (recebem requisicoes), services
(aplicam regras), repositories (consultam o banco) e entities (dados).
O fluxo de reserva usa uma transacao e um bloqueio na linha da sessao.

## Como executar

Requisitos: Docker com Docker Compose para executar tudo de forma reproduzivel.
Para desenvolver fora do Docker, use Java 17 ou superior e Maven.

Na pasta principal, os dois bancos e os dois microsservicos sobem juntos:

```bash
docker compose up --build -d
docker compose ps
```

O primeiro comando constroi os arquivos jar e as imagens dos microsservicos.
O segundo mostra quais containers estao ativos e se estao saudaveis.
O inicio pode demorar porque o Maven precisa baixar as dependencias.

Para testar rapidamente:

```bash
curl http://localhost:8081/artistas
curl http://localhost:8082/sessoes
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
```

Para ver os logs:

```bash
docker compose logs --tail=60 catalogo sessoes
```

Para interromper o ambiente sem apagar os dados:

```bash
docker compose down
```

Para executar com Maven fora do Docker, primeiro inicie os bancos com
`docker compose up -d postgres postgres-sessoes`.
Para iniciar o catalogo, em um terminal:

```bash
mvn -pl catalogo-service spring-boot:run
```

Para iniciar as sessoes, em outro terminal:

```bash
mvn -pl sessoes-service spring-boot:run
```

O catalogo usa a porta `8081` e o servico de sessoes usa a `8082`.
Os bancos PostgreSQL ficam nas portas `5434` (catalogo) e `5435` (sessoes).
No Docker, os servicos se comunicam pelos nomes `catalogo`, `postgres`
e `postgres-sessoes`, sem colocar IPs fixos no Java.
Os nomes de porta publicados podem ser mudados pelas variaveis
`CATALOGO_PORT`, `SESSOES_PORT`, `POSTGRES_PORT` e `SESSOES_POSTGRES_PORT`.

## Consultas do catalogo

- `GET http://localhost:8081/artistas`
- `GET http://localhost:8081/artistas/1`
- `GET http://localhost:8081/generos`
- `GET http://localhost:8081/albuns`
- `GET http://localhost:8081/albuns/1`
- `GET http://localhost:8081/albuns?titulo=kind`

## Locais e sessoes

- `GET http://localhost:8082/locais`
- `GET http://localhost:8082/locais/1`
- `POST http://localhost:8082/locais`
- `GET http://localhost:8082/sessoes`
- `GET http://localhost:8082/sessoes/1`
- `POST http://localhost:8082/sessoes`
- `GET http://localhost:8082/sessoes/1/album`: busca o album no catalogo
- `GET http://localhost:8082/sessoes/1/detalhes`: consulta sessao e tenta trazer album

Para criar um local:

```bash
curl -X POST http://localhost:8082/locais \
  -H "Content-Type: application/json" \
  -d '{"nome":"Loja de Discos","endereco":"Rua Exemplo, 10"}'
```

Para criar uma sessao com 10 vagas no local de id 1:

```bash
curl -X POST http://localhost:8082/sessoes \
  -H "Content-Type: application/json" \
  -d '{"albumId":1,"localId":1,"data":"2026-11-28","horario":"19:00","capacidade":10}'
```

Uma sessao nova com capacidade 10 comeca com 10 vagas disponiveis.
Os locais e uma sessao de exemplo sao adicionados na primeira execucao.

## Reservas

- `POST http://localhost:8082/sessoes/1/reservas`: reserva uma vaga
- `GET http://localhost:8082/sessoes/1/reservas`: lista reservas da sessao

Exemplo de reserva:

```bash
curl -i -X POST http://localhost:8082/sessoes/1/reservas \
  -H "Content-Type: application/json" \
  -d '{"nomeCliente":"Ana"}'
```

Quando ainda existem vagas, retorna HTTP 201. Quando acabou, retorna 409.
O `ReservaService` usa uma transacao e um bloqueio na linha da sessao
(`PESSIMISTIC_WRITE`). Assim, com apenas uma vaga, duas tentativas feitas
ao mesmo tempo nao conseguem reservar a mesma ultima vaga.

Para conferir, crie uma nova sessao com capacidade 1 e use seu ID:

```bash
curl -i -X POST http://localhost:8082/sessoes/ID/reservas -H "Content-Type: application/json" -d '{"nomeCliente":"Ana"}' &
curl -i -X POST http://localhost:8082/sessoes/ID/reservas -H "Content-Type: application/json" -d '{"nomeCliente":"Bruno"}' &
wait
```

Substitua `ID` pelo identificador da sessao criada. Uma requisicao deve
retornar 201 e a outra 409. As vagas disponiveis devem ficar em zero.


O campo `albumId` identifica o album no catalogo. Ao criar uma sessao,
`CatalogoClient` consulta `GET /albuns/{id}` no outro servico. Se o album nao
existe, a API de sessoes retorna 400 e nao salva a nova sessao. Se o catalogo
estiver indisponivel, retorna 503. As consultas basicas de sessoes ja salvas
continuam funcionando, pois usam apenas o banco do proprio servico.

Para testar a comunicacao, mantenha os dois servicos ligados e execute:

```bash
curl -i http://localhost:8082/sessoes/1/album
```

Depois, desligue o catalogo. `GET /sessoes/1` deve continuar respondendo,
enquanto `/sessoes/1/album` deve retornar 503. Inicie o catalogo novamente
para continuar os testes. Se executar em outro endereco, configure
`CATALOGO_URL` antes de iniciar o servico de sessoes (sem barra final).

A chamada HTTP e sincrona, usando `RestTemplate`. O codigo Java nao possui
porta nem endereco fixo do catalogo. A configuracao local usa `localhost:8081`.
Os tempos de conexao e resposta sao limitados a 2 e 3 segundos, respectivamente.
Para falhas de conexao ou resposta do servidor, o cliente agora tenta uma
segunda chamada HTTP antes de retornar 503. Para um album inexistente (404),
nao repete a chamada, porque nao e um problema temporario.

Em `/sessoes/1/detalhes`, se o catalogo estiver fora, o sistema devolve os
dados da sessao, `album: null` e um aviso, em vez de falhar a consulta inteira.
Essa e uma resposta alternativa (fallback). O limite de tempo da chamada ja
foi configurado anteriormente no HttpConfig.

Quando uma reserva e confirmada, a aplicacao registra um aviso no log de forma
assincrona (`@Async`), depois do commit da transacao. E apenas uma simulacao
de notificacao; nao envia email nem mensagem real.

## Validacao do projeto

Os testes normais usam H2 e nao precisam iniciar o Docker:

```bash
mvn test
```

Tambem existe um teste com **PostgreSQL real** usando Testcontainers.
Ele inicia um banco temporario e verifica a gravacao da reserva e duas
reservas simultaneas para a ultima vaga. Precisa do Docker em execucao:

```bash
mvn -pl sessoes-service -am -Pintegracao verify
```

O teste de integracao se chama `ReservaPostgresIT`. Ele nao usa o PostgreSQL
do Compose: cria um banco isolado para o teste e o encerra ao terminar.
Os testes normais sao executados antes dele pelo Maven.

Para verificar a aplicacao HTTP completa, execute os containers:

```bash
docker compose up --build -d
docker compose ps
curl -i http://localhost:8081/albuns
curl -i http://localhost:8082/sessoes
curl -i http://localhost:8082/sessoes/1/detalhes
```

Teste tambem uma nova sessao com uma vaga, duas reservas e a consulta final
de vagas. A primeira reserva deve responder `201` e a seguinte `409`.
O resultado precisa ser conferido no terminal, nao apenas no codigo.

Se os testes falharem, confira os logs do Maven e do Docker antes da entrega.
Se o Docker nao estiver ativo, o teste com Testcontainers nao conseguira
iniciar o PostgreSQL.

## Configuracao e persistencia

- Os dois bancos usam volumes Docker diferentes, para manter dados apos reinicio.
- As variaveis `DB_URL`, `DB_USER` e `DB_PASSWORD` podem mudar a conexao de
  cada servico, conforme o ambiente em que ele e iniciado.
- O banco usa `ddl-auto=update` apenas para facilitar a atividade.
- As senhas no `compose.yaml` servem somente para desenvolvimento local.

## Testes

Com Java e Maven instalados:

```bash
mvn test
```

Tambem e possivel executar os testes usando a imagem do Maven:

```bash
docker run --rm -v "$PWD":/app -w /app maven:3.9.11-eclipse-temurin-17 mvn test
```

Os Dockerfiles usam `-DskipTests` apenas durante a criacao das imagens.
Isso nao substitui os testes, que precisam ser executados separadamente.

## Reinicio e saude

Os containers das aplicacoes usam `restart: unless-stopped`, o que ajuda a
reiniciar a aplicacao depois de uma falha do processo. Os endpoints
`/actuator/health` permitem verificar se uma instancia responde.
Isso nao equivale a um orquestrador que recrie containers automaticamente.

Para simular indisponibilidade do catalogo:

```bash
docker compose stop catalogo
curl -i http://localhost:8082/sessoes/1
curl -i http://localhost:8082/sessoes/1/detalhes
docker compose up -d catalogo
```

O banco de sessoes e independente do banco do catalogo, por isso a consulta
basica de sessoes deve continuar funcionando com o catalogo desligado.

O Compose padrao utiliza uma instancia do catalogo. Para demonstrar
balanceamento e escala, o projeto inclui `compose.escala.yaml` e `nginx.conf`.
Esses arquivos permitem executar varias instancias do catalogo atras do Nginx.

Os testes normais usam H2. O teste de integracao usa PostgreSQL real
em container temporario e precisa de Docker.

## Decisoes do projeto

- **Persistencia:** PostgreSQL nos dois servicos, pois os dados sao
  relacionados (artistas, albuns, sessoes, locais e reservas). Os bancos
  separados evitam que um servico acesse diretamente os dados internos do outro.
  Os volumes no Docker preservam os dados apos reinicios comuns.
- **Comunicacao:** HTTP com `RestTemplate`, pois a validacao do album precisa
  de uma resposta imediata. O endereco vem da configuracao `CATALOGO_URL`.
- **Resiliencia:** timeout para impedir espera indefinida; nova tentativa
  para uma falha transitoria; resposta alternativa em `/sessoes/{id}/detalhes`
  para consultar a sessao mesmo sem os detalhes do album.
- **Concorrencia:** o `ReservaService` utiliza `@Transactional`, e a busca da
  sessao em `SessaoRepository` usa `PESSIMISTIC_WRITE`. Assim, uma reserva
  aguarda a outra terminar antes de conferir a ultima vaga.
- **Assincrono:** o evento de reserva confirmada e tratado com `@Async`, depois
  do commit, apenas para simular um aviso no log. Nao existe envio real.
- **Execucao:** Docker Compose sobe os bancos e os dois servicos, com
  `healthcheck` e `restart: unless-stopped`. Cada aplicacao possui Dockerfile.

## Escalabilidade e balanceamento

A configuracao normal (`compose.yaml`) continua simples, com uma instancia
por microsservico. O arquivo adicional `compose.escala.yaml` remove a
publicacao direta da porta do catalogo e coloca o Nginx na porta 8081.
O Nginx encaminha chamadas para as instancias do catalogo pelo DNS interno
do Docker. O servico de sessoes continua na porta 8082.

Para demonstrar o aumento de 1 para 3 instancias:

```bash
docker compose -f compose.yaml -f compose.escala.yaml up -d --scale catalogo=3
docker compose -f compose.yaml -f compose.escala.yaml ps
```

Para testar e visualizar os enderecos que receberam as chamadas:

```bash
for i in {1..15}; do curl -s -o /dev/null -w "HTTP %{http_code}\n" http://localhost:8081/artistas; done
docker compose -f compose.yaml -f compose.escala.yaml logs --tail=30 balanceador
```

As requisicoes devem retornar HTTP 200 e os logs do Nginx devem mostrar
enderecos diferentes de instancias do catalogo. O balanceamento foi verificado
com tres destinos e as 15 chamadas respondendo HTTP 200.

Para reduzir de 3 para 1 instancia, sem alterar codigo:

```bash
docker compose -f compose.yaml -f compose.escala.yaml up -d --scale catalogo=1
docker compose -f compose.yaml -f compose.escala.yaml ps
curl -i http://localhost:8081/artistas
curl -i http://localhost:8082/sessoes/1/album
```

Importante: quando executar novamente o comando `up` com essa configuracao,
informe `--scale catalogo=3` para manter tres instancias. Sem esse argumento,
o Compose pode voltar para apenas uma instancia.

Para retornar ao ambiente original:

```bash
docker compose -f compose.yaml -f compose.escala.yaml down
docker compose up -d
```

Nao use `down -v`, pois isso apagaria os volumes dos bancos.

## Recuperacao automatica e saude

Os microsservicos possuem `restart: unless-stopped` e endpoint do Actuator
em `/actuator/health`. Foi realizado um teste encerrando o processo
principal do catalogo com `kill -TERM 1` dentro do container. O contador
`RestartCount` passou de 0 para 1, e a API voltou a responder HTTP 200.
Esse teste demonstra reinicio automatico do processo, mas nao reposicao
de containers removidos por um orquestrador.

## Testes realizados

Os testes e as verificacoes manuais foram executados localmente em 14/11/2026:

- `mvn test`: 27 testes aprovados, sem falhas.
- `mvn -pl sessoes-service -am -Pintegracao verify`: 22 testes normais
  do servico de sessoes e 2 de integracao com PostgreSQL do Testcontainers,
  todos aprovados.
- Docker Compose: dois microsservicos e dois bancos iniciados.
- Endpoints de saude e APIs: respostas HTTP 200.
- Criacao de sessao com album existente: HTTP 201; album inexistente: HTTP 400.
- Duas reservas simultaneas para a ultima vaga: uma HTTP 201 e outra HTTP 409.
- Dados preservados depois de `docker compose down` e `up`.
- Catalogo desligado: sessao basica HTTP 200, fallback com album nulo e
  consulta obrigatoria do album HTTP 503; catalogo recuperado depois.
- Escalabilidade de 1 para 3 e de volta para 1 instancia; Nginx distribuiu
  chamadas entre tres enderecos do catalogo.

## Automacao

O Maven compila, testa e gera JARs por comandos locais. Os Dockerfiles
constroem os JARs com `-DskipTests`, portanto a construcao de imagens nao
substitui a validacao dos testes. Nao existe pipeline executado
automaticamente a cada push, nem entrega automatizada de artefatos aprovados.
