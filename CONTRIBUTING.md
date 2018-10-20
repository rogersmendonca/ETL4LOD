# Contribuindo

Para contribuir com este repositório, por favor primeiro crie uma issue e discuta se essa mudança é necessária antes de fazer a mudança. 

Por favor, note que temos um código de conduta, por favor siga esse código no nosso projeto.

## Pull Request

1. Instale todas as dependências do projeto.
2. Atualize o CHANGELOG.md com as mudanças feitas. Caso mudanças de build ou em como o projeto funciona sejam feitas, adicione essas mudanças no README.md.
3. Acrescente o número da versão sempre que algum problema for resolvido usando a [SemVer](http://semver.org/) como guia.
4. Use o [guia de commits semânticos](https://seesparkbox.com/foundry/semantic_commit_messages) para criar os seus commits no repositório.

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
