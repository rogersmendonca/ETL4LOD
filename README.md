![](img/logo_big.png)

[![GitHub tag](https://img.shields.io/github/tag/expressjs/express.svg)](https://github.com/johncurcio/ETL4LOD-2.0/tags)

ETL4LOD+ é uma extensão do Kettle baseada no [ETL4LOD](https://github.com/rogersmendonca/ETL4LOD) para trabalhar com Linked Open Data. 

## Usando o Projeto

As versões do projeto estão todas disponíveis em [releases](https://github.com/johncurcio/ETL4LOD-2.0/releases) neste repositório. Para usar uma das releases, basta baixar o ``.tar.gz`` e extrair na pasta ``plugins/`` da sua instalação do Kettle 8.1.

Para mais detalhes em relação a como os plugins neste repositório funcionam, por favor veja a nossa documentação no gitbook. 

### Pré-requisitos

Este projeto tem as seguintes dependências:

* [Java 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) para desenvolvimento.
* [Maven](https://maven.apache.org/) para gestão das dependências.
* [Kettle 8.1](https://sourceforge.net/projects/pentaho/) para testes e deploy.

Este projeto foi desenvolvido na IDE Eclipse, porém é agnóstico em relação a IDEs - não é necessário o uso do Eclipse para desenvolver neste projeto.

### Instalando

Para rodar o projeto em sua máquina, mude a variável ``pdi.home`` no pom do projeto pai ``plugins`` para a sua instalação do Kettle 8.1 e rode ``mvn clean install`` projeto pai. Isso instalará os plugins no Kettle especificado em ``pdi.home``.

Abra o Kettle 8.1 e uma pasta chamada LinkedDataBR deve aparecer com os plugins deste projeto. 

### Deployment

Para fazer deploy, rode na pasta do projeto pai:

``mvn clean install``

Tendo certeza de apontar a variável ``pdi.home`` para a sua instalação do Kettle 8.1.

## Contribuição

Por favor, leia [CONTRIBUTING.md](CONTRIBUTING.md) para detalhes de como contribuir com o projeto.

## Versionamento

Usamos [SemVer](http://semver.org/) para versionamento do ETL4LOD+. Para ver as versões liberadas, entre nas [tags neste repositorio](https://github.com/johncurcio/ETL4LOD-2.0/tags).

## Licença

Este projeto usa a licença do MIT, veja [LICENSE.md](LICENSE) para mais detalhes.

## Inspirado em

* [ETL4LOD](https://github.com/rogersmendonca/ETL4LOD) e [ETL4LOD-graph](https://github.com/rogersmendonca/ETL4LOD-Graph)