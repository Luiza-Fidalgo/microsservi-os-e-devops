# NeedleDrop

Projeto de microsservicos para uma plataforma de catalogo musical e sessoes de audicao.

## Servicos

- `catalogo-service`: consulta artistas, generos e albuns.
- `sessoes-service`: cria e consulta locais e sessoes de audicao.

Cada servico tem seu proprio banco PostgreSQL. A comunicacao entre servicos e
as reservas ainda serao adicionadas nas proximas etapas.

## Como executar

Requisitos: Java 17 ou superior, Maven e Docker com Compose.

Na pasta principal do projeto:

```bash
docker compose up -d
mvn test
```

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

O campo `albumId` e somente o identificador do album no catalogo. A validacao
se o album realmente existe sera feita quando adicionarmos comunicacao entre
os dois servicos. Ainda nao existe reserva nem desconto de vagas.

## Configuracao e persistencia

- Os dois bancos usam volumes Docker diferentes, para manter dados apos reinicio.
- As variaveis `DB_URL`, `DB_USER` e `DB_PASSWORD` podem mudar a conexao de
  cada servico, conforme o ambiente em que ele e iniciado.
- O banco usa `ddl-auto=update` apenas para facilitar a atividade.
- As senhas no `compose.yaml` servem somente para desenvolvimento local.

## Testes

```bash
mvn test
```

Os testes automatizados usam H2. Mais adiante sera incluido um teste com
PostgreSQL real, pois o enunciado pede uma dependencia real isolada.

## O que falta

Reservas, controle de concorrencia, comunicacao entre servicos,
tratamento de falhas, processamento assincrono e empacotamento em Docker.
