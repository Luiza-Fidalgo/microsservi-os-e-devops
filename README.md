# NeedleDrop

Projeto de microsservicos para uma plataforma de catalogo musical e sessoes de audicao.

## Servicos

- `catalogo-service`: consulta artistas, generos e albuns. Usa PostgreSQL.
- `sessoes-service`: estrutura inicial, sem reservas por enquanto.

O projeto usa Spring Boot, Java 17, Maven e PostgreSQL. Ainda nao existe comunicacao entre os servicos.

## Como executar

Requisitos: Java 17 ou superior, Maven e Docker com Compose.

Na pasta principal, iniciar o banco de dados:

```bash
docker compose up -d
```

O PostgreSQL usa a porta 5434 do computador, para diminuir conflitos com outros projetos.

Em um terminal, iniciar o catalogo:

```bash
mvn -pl catalogo-service spring-boot:run
```

Em outro terminal, iniciar as sessoes:

```bash
mvn -pl sessoes-service spring-boot:run
```

## Consultas do catalogo

- `GET http://localhost:8081/artistas`
- `GET http://localhost:8081/artistas/1`
- `GET http://localhost:8081/generos`
- `GET http://localhost:8081/albuns`
- `GET http://localhost:8081/albuns/1`
- `GET http://localhost:8081/albuns?titulo=kind`
- `GET http://localhost:8082/sessoes` (retorna lista vazia nesta etapa)

A primeira execucao do catalogo cria exemplos de artistas, generos e albuns.
Nas execucoes seguintes, o banco preserva os dados gravados. O Docker usa
um volume para manter os dados mesmo quando o container e reiniciado.

Para mudar conexao, usuario e senha do banco use as variaveis de ambiente
`DB_URL`, `DB_USER` e `DB_PASSWORD`. A porta do catalogo pode ser alterada
com `PORT`. As credenciais usadas no arquivo Compose sao apenas locais.

## Testes

```bash
mvn test
```

Os testes do catalogo usam H2, um banco em memoria. O PostgreSQL real sera
usado nos testes de integracao de uma etapa futura.

## Limitacoes atuais

- Apenas consultas HTTP, com dados de exemplo; sem cadastro pela API.
- Configuracao JPA `ddl-auto=update` usada para simplificar o trabalho escolar;
  uma aplicacao em producao normalmente precisaria de migrations.
- As entidades sao retornadas diretamente nas respostas da API.
- Faltam sessoes reais, reservas, concorrencia, comunicacao, resiliência,
  empacotamento dos microsservicos em Docker e testes com PostgreSQL real.
