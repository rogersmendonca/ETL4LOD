# Como criar plugins do Kettle

Este é um passo a passo não extensivo sobre como criar um plugin do Kettle. Ele foi feito para direcionar quem deseja criar um plugin pro ETL4LOD+. 

## Passo a Passo

**Passo 1:** Criar um projeto maven dentro da pasta ``plugins`` onde ficará o código do seu plugin. Esse projeto pode ser criado de diversas formas: manualmente, usando o eclipse, usando a linha de comando... Segue um exemplo de como criar esse projeto na linha de comando.

```
$ cd ETL4LODPlus/plugins
$ mvn archetype:generate -DgroupId=br.ufrj.ppgi.greco.kettle.NomeDoPlugin -DartifactId=NomeDoPlugin -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```

**Passo Opcional:** Importar seu projeto maven recém criado para o Eclipse.

**Passo 2:** Criar uma pasta ``etc`` dentro da pasta do seu plugin. Nessa pasta ficarão os arquivos ``plugin.xml`` e ``icon.png``, que respectivamente são o arquivo de configuração de um plugin do Kettle e o ícone do plugin que será mostrado no Kettle.

O arquivo ``plugin.xml`` pode ser criado usando qualquer um dos arquivos dos plugins já existentes como template. Um template completo seria o do plugin **SparqlEndpoint**.

O arquivo ``icon.png`` pode ser criado usando qualquer editor de imagens que você possua. Em geral, um ícone para o ETL4LOD+ pode ser um simples círculo azul com a primeira letra em branco do seu plugin centralizada.

**Passo 3:** Criar os arquivos .java necessários para os plugins. Todo plugin kettle precisa de 4 arquivos dentro de sua pasta ``src/main/br/ufrj/ppgi/greco/kettle``:

```
$ touch NomeDoPluginStep.java
$ touch NomeDoPluginStepDialog.java
$ touch NomeDoPluginStepMeta.java
$ touch NomeDoPluginStepData.java
```

**Passo 4:** Modificar o pom.xml do copiar todas as dependências desse plugin quando ``mvn clean install`` for executado.  Todo pom.xml de um plugin possui um código de build para copiar os arquivos da pasta do plugin para sua instalação do Kettle. Esse código geralmente é parecido com:

```
<build>
	<plugins>
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-antrun-plugin</artifactId>
			<version>1.8</version>
			<executions>
				<execution>
					<id>copy-files-to-kettle</id>
					<phase>install</phase>
					<configuration>
						<target name="copy-files-to-kettle">
							<echo
								message="Copying ${basedir}\etc\*.[png,xml,properties] to ${pdi.home}/${pdi.plugin.dir}" />
							<copy todir="${pdi.home}/${pdi.plugin.dir}" overwrite="true">
								<fileset dir="${basedir}/etc" includes="**/*.png,**/*.xml,**/*.properties" />
							</copy>
							<echo
								message="Copying ${basedir}\libs\*.jar to ${pdi.home}/${pdi.plugin.dir}" />
							<copy todir="${pdi.home}/${pdi.plugin.dir}/lib" overwrite="true">
								<fileset dir="${project.build.directory}/lib" includes="**/*.jar" />
							</copy>

							<echo
								message="Copying ${project.build.directory}\${project.build.finalName}.${project.packaging} to ${pdi.home}/${pdi.plugin.dir}" />
							<copy
								file="${project.build.directory}/${project.build.finalName}.${project.packaging}"
								tofile="${pdi.home}/${pdi.plugin.dir}/${pdi.plugin.lib_name}.${project.packaging}"
								overwrite="true" />
						</target>
					</configuration>
					<goals>
						<goal>run</goal>
					</goals>
				</execution>
			</executions>
		</plugin>
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-dependency-plugin</artifactId>
			<version>2.8</version>
			<executions>
				<execution>
					<id>copy</id>
					<phase>prepare-package</phase>
					<goals>
						<goal>copy</goal>
					</goals>
					<configuration>
						<artifactItems>

							<artifactItem>
								<groupId>br.ufrj.ppgi.greco.kettle</groupId>
								<artifactId>KettlePluginTools</artifactId>
								<version>1.0</version>
								<type>jar</type>
								<overWrite>true</overWrite>
								<outputDirectory>${project.build.directory}/lib</outputDirectory>
							</artifactItem>

						</artifactItems>
					</configuration>
				</execution>
			</executions>
		</plugin>
	</plugins>

	<resources>
		<resource>
			<directory>src/main/resources</directory>
			<excludes>
				<exclude>plugin/*.*</exclude>
			</excludes>
		</resource>
		<resource>
			<directory>src/main/java</directory>
			<includes>
				<include>**/*.properties</include>
			</includes>
		</resource>
	</resources>
</build>
```

Um exemplo mais completo de ``pom.xml`` pode ser encontrado no plugin **SparqlEndpoint**. 

**Passo 5:** Desenvolver o código. Dá uma lida nos plugins já existentes do ETL4LOD para você ter uma ideia de como desenvolver um plugin pro Kettle. O Ntriple Generator e o Annotator são simples e podem ser considerados exemplos iniciais.

Geralmente os arquivos ``NomeDoPluginStepDialog.java`` e ``NomeDoPluginStepMeta.java`` são os primeiros a serem criados, porque eles tratam de como o frontend vai ser exibido pro usuário e que configurações são necessárias.

**Passo 6:** Adicione i18n caso seja necessário. Em CONTRIBUTING.md tem um tutorial de como adicionar i18n.

**Passo 7:** Teste o seu plugin com ``mvn clean install``. Esse comando deveria mandar seu novo plugin para o Kettle e assim que o Kettle for reiniciado, ele estará pronto para ser usado.

Os testes integrados com o Kettle podem ser lentos, porque é necessário mandar o plugin pro Kettle e reiniciar o Kettle para poder testar o plugin toda vez que tiver alguma mudança no código. 


