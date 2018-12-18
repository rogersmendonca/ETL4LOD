# Contribuindo

Para contribuir com este repositório, por favor primeiro crie uma issue e discuta se essa mudança é necessária antes de fazer a mudança. 

Por favor, note que temos um código de conduta, por favor siga esse código no nosso projeto.

## Pull Request

1. Instale todas as dependências do projeto.
2. Atualize o CHANGELOG.md com as mudanças feitas. Caso mudanças de build ou em como o projeto funciona sejam feitas, adicione essas mudanças no README.md.
3. Acrescente o número da versão sempre que algum problema for resolvido usando a [SemVer](http://semver.org/) como guia.
4. Use o [guia de commits semânticos](https://seesparkbox.com/foundry/semantic_commit_messages) para criar os seus commits no repositório.

## Criando um plugin

É completamente novo a criação de plugins pro Kettle? Existem vários tutoriais sobre como criar um plugin pro Kettle. O oficial é este aqui https://help.pentaho.com/Documentation/8.2/Developer_Center/PDI/Extend/000. Para criar um plugin especificamente pro ETL4LOD+, por favor, verifique o passo a passo disponível em [criando plugins para o Kettle usando Eclipse + Maven](docs/PLUGINS.md)

## Dependências de Runtime

Alguns plugins possuem dependências de runtime (que são carregadas somente quando o plugin é executado). Esse tipo de dependência precisa ir para a pasta lib/ junto com o .jar do plugin!

As dependências de runtime do projeto foram criadas para serem copiadas automaticamente do ambiente de desenvolvimento para a pasta de plugins no Kettle instalado em ``pdi.home`` quando ``mvn clean install`` é executado. A adição de novas dependências ao projeto precisam continuar sendo automatizadas.

### Atualizando uma dependência de runtime

Para atualizar a versão de alguma dependência do projeto, basta mudar essa versão na variável contida em ``<properties>`` no pom do projeto pai. Caso essa seja uma atualização de versão ``MAJOR`` pode ser necessário ajustar o código e adicionar/remover dependências do plugin. 

### Adicionando uma nova dependência runtime

Para adicionar uma dependência para um plugin é necessário criar uma variável ``${dependency.version}`` que deve ser adicionada ao ``pom.xml`` do projeto pai. Uma nova ``<library>`` tem que ser criada no ``plugin.xml`` do plugin usando a sintaxe:

```
<library name="lib/dependency-{dependency.version}.jar"/>
```

O ``pom.xml`` do filho também precisa ser atualizado para copiar essa dependência nova automaticamente e substituir a variável no ``plugin.xml``. Três partes do pom podem precisar ser modificadas.

1. Adicionar uma nova dependência ao pom do plugin em ``<dependency>``.
2. Adicionar um ``<artifactItem>`` ao goal ``copy``. Isso vai garantir que a dependência adicionada seja copiada para a pasta lib do plugin. 
2. Adicionar um novo ``<replace>`` ao goal ``copy-files-to-kettle``. O replace vai substituir no ``plugin.xml`` o valor da variável ``{dependency.version}`` com o valor que foi colocado no pom do projeto pai em ``${dependency.version}``. Mais informações em [maven-ant-run replace task](https://ant.apache.org/manual/Tasks/replace.html).

## i18n

O Kettle 6+ aparentemente tem um bug no qual a i18n não funciona para múltiplos plugins da forma que o Kettle ensina a fazer a i18n. Para resolver esse problema, existe um hack no qual todos os arquivos properties ``messages_xx_XX.properties`` precisam ser iguais e conter a tradução de TODOS os plugins!

Esse hack foi implementado neste repositório para permitir o uso da i18n da seguinte forma:

1. Ao invés de existir arquivos properties na pasta messages de cada plugin, existe apenas uma pasta messages no parent dos plugins com arquivos properties que contém a tradução de TODOS os plugins;
2. Foi adicionado um ``copy`` no pom do parent para cada plugin que copia essa pasta messages para os filhos:

```
<copy todir="${basedir}/%nome_plugin%/src/main/java" overwrite="true">
   <fileset dir="${basedir}/src/main/java" includes="**/*.properties" />
</copy>
```

3. Foi adicionado uma linha no ``.gitignore`` para ignorar a pasta messages dos filhos.

Dessa forma, quando ``mvn clean install`` é executado no parent, o parent copia a pasta messages para todos os filhos antes de buildar os filhos. Assim a i18n funciona para todos os plugins. 

### Adiconando uma nova tradução

Para adicionar uma nova tradução é necessário:

1. Adicionar essas traduções nos arquivos .properties no parent;
2. Adicionar um ``copy`` no pom do parent para o seu novo plugin. 
