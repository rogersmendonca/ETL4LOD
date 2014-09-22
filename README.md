ETL4LOD
=======

ETL4LOD - Steps do Pentaho Data Integration (Kettle) relacionados a Linked Data.

* Data Property Mapping: 

O step Data Property Mapping oferece a capacidade de mapear, a partir da linhas do fluxo de entrada, os componentes de uma tripla RDF (sujeito, predicado e objeto) nas linhas do fluxo de saída, sendo o objeto um valor literal.

* Object Property Mapping: 

O step Object Property Mapping é similar ao step Data Property Mapping, com a diferença de que o valor do objeto enviado no fluxo de saída é uma URI de um recurso.

* Sparql Endpoint: 

O step Sparql Endpoint oferece a capacidade de extrair dados de um SPARQL Endpoint, a partir da especificação da URL relacionada ao SPARQL Endpoint e da definição de uma consulta SPARQL.

* Sparql Update Output: 

O step Sparql Update Output oferece a capacidade de carregar triplas RDF em um banco de triplas (exemplo: Virtuoso).

* NTriple Generator: 

O step NTriple Generator oferece a facilidade de geração de setenças RDF no formato NTriple. É um step útil, por exemplo, para receber as linhas de dados enviadas por um step Data Property Mapping ou Object Property Mapping e gerar, no fluxo de saída, as linhas de dados com as triplas RDF a serem inseridas em um banco de triplas, por meio do step Sparql Update Output.

* Annotator: 

O step Annotator anota uma tripla com termos de vocabulários e ontologias, de acordo com um mapeamento "de-para" definido em um arquivo XML.

* SparqlRunQuery: 

O step SparqlRunQuery recebe um campo com uma query SPARQL e executa esta query em um SPARQL Endpoint.

Observação: Os steps "Data Property Mapping", "Object Property Mapping", "Sparql Endpoint" e "Sparql Update Output" disponibilizados são extensões dos steps produzidos originalmente pelo projeto LinkedDataBR (https://www.rnp.br/pd/gts2010-2011/gt_linkeddatabr.html). As versões iniciais destes 4 steps possibilitavam o armazenamento dos metadados de composição dos steps somente em um repositório Kettle do tipo sistema de arquivos. As versões disponibilizadas aqui possibilitam o armazenamento dos metadados de composição dos steps também em um repositório Kettle do tipo banco de dados. Além disso, os códigos fonte foram adequados à estrutura do maven (http://maven.apache.org). 
