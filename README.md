# NeedleDrop

Projeto da disciplina de microsservicos. A aplicacao sera uma plataforma de catalogo musical e sessoes de audicao.

## Estrutura inicial

- `catalogo-service`: consulta de artistas e futuramente de albuns.
- `sessoes-service`: gerencia sessoes e reservas (ainda em desenvolvimento).

Os dois servicos sao projetos Spring Boot separados.

## Requisitos

- Java 17 ou superior
- Maven 3.6.3 ou superior

## Como executar

Abra dois terminais na pasta do projeto.

Terminal 1:

```bash
mvn -pl catalogo-service spring-boot:run
```

Terminal 2:

```bash
mvn -pl sessoes-service spring-boot:run
```

O catalogo usa a porta 8081 e as sessoes usam a 8082.

## Endpoints desta etapa

- `GET http://localhost:8081/artistas`
- `GET http://localhost:8081/artistas/1`
- `GET http://localhost:8082/sessoes` (por enquanto retorna uma lista vazia)

## Testes

```bash
mvn test
```

## O que ainda falta

Nesta primeira etapa os artistas estao em memoria, apenas como exemplo.
Ainda nao foram feitos banco de dados, albuns, busca, sessoes reais,
reservas, comunicacao entre servicos, controle de concorrencia e Docker.
Essas funcionalidades serao adicionadas aos poucos.
