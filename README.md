![](img/logo_big.png)

[![latest release](https://img.shields.io/github/release/johncurcio/ETL4LODPlus.svg?style=for-the-badge)](https://github.com/johncurcio/ETL4LODPlus/releases) [![scrutinizer code quality](https://img.shields.io/scrutinizer/g/johncurcio/ETL4LODPlus.svg?style=for-the-badge)](https://scrutinizer-ci.com/g/johncurcio/ETL4LODPlus/) [![Build Status](https://img.shields.io/scrutinizer/build/g/johncurcio/ETL4LODPlus.svg?style=for-the-badge)](https://scrutinizer-ci.com/g/johncurcio/ETL4LODPlus/build-status/master) [![PRs Welcome](https://img.shields.io/badge/prs-welcome-f23c50.svg?longCache=true&style=for-the-badge)](http://makeapullrequest.com) [![issues open](https://img.shields.io/github/issues/johncurcio/ETL4LODPlus.svg?style=for-the-badge)](https://github.com/johncurcio/ETL4LODPlus/issues) [![MIT License](https://img.shields.io/badge/license-MIT-FF8B0D.svg?longCache=true&style=for-the-badge)](LICENSE) 

ETL4LOD+ é uma extensão do Kettle baseada no [ETL4LOD](https://github.com/rogersmendonca/ETL4LOD) para trabalhar com Linked Open Data. 

## Usando o Projeto

Usar o projeto é tão simples quanto [baixar a versão mais recente dos plugins](https://github.com/johncurcio/ETL4LODPlus/releases) e extrair o ``.tar.gz`` na pasta ``plugins/`` da sua instalação do Kettle 8.1+.

## Desenvolvimento

### Pré-requisitos

Este projeto tem as seguintes dependências:

* [Java 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) para desenvolvimento.
* [Maven](https://maven.apache.org/) para gestão das dependências.
* [Kettle 8.1+](https://sourceforge.net/projects/pentaho/) para testes e deploy.

Este projeto foi desenvolvido na IDE Eclipse, porém é agnóstico em relação a IDEs - não é necessário o uso do Eclipse para desenvolver neste projeto.

### Instalando

Para rodar o projeto em sua máquina, mude a variável ``pdi.home`` no pom do projeto pai ``plugins`` para a sua instalação do Kettle 8.1 e rode ``mvn clean install`` projeto pai. Isso instalará os plugins no Kettle especificado em ``pdi.home``.

Abra o Kettle 8.1 e uma pasta chamada LinkedDataBR deve aparecer com os plugins deste projeto. 

### Deployment

Para fazer deploy, rode na pasta do projeto pai:

``mvn clean install``

Tendo certeza de apontar a variável ``pdi.home`` para a sua instalação do Kettle 8.1.

## Contribuição

Para mais informações sobre o desenvolvimento, por favor, leia [CONTRIBUTING.md](CONTRIBUTING.md).

## Versionamento

Usamos [SemVer](http://semver.org/) para versionamento do ETL4LOD+. Para ver as versões liberadas, entre nas [tags deste repositorio](https://github.com/johncurcio/ETL4LODPlus/tags).

## Licença

Este projeto usa a licença do MIT, veja [LICENSE.md](LICENSE) para mais detalhes.

## Inspirado em

* [ETL4LOD](https://github.com/rogersmendonca/ETL4LOD) e [ETL4LOD-graph](https://github.com/rogersmendonca/ETL4LOD-Graph)
