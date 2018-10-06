package br.ufrj.ppgi.greco.kettle;

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.listeners.StatementListener;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

/**
 * 
 * Precisei usar a classe Model do Jena para ler e escrever NTriplas
 * 
 * @author Expedito
 * 
 */
class StatementValidor {
	private Statement lastStatement;
	private Model model;
	private ValidatorListener listener;

	private class ValidatorListener extends StatementListener {

		@Override
		public void addedStatement(Statement s) {
			super.addedStatement(s);
			lastStatement = s;
		}
	}

	public StatementValidor() {
		this.model = ModelFactory.createDefaultModel();
		this.listener = this.new ValidatorListener();
		this.model.register(this.listener);
	}

	public String parseAndGetTriple(String ntriple) {
		try {
			model.read(new StringReader(ntriple), null, "N-TRIPLE");

			validateStatement();

			StringWriter sw = new StringWriter();
			model.write(sw, "N-TRIPLE");

			model.remove(lastStatement);

			return sw.toString();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Valida ultimo statement inserido, basicamente faz trim() nas string de
	 * RDFNodes nao literais
	 * 
	 * @return
	 */
	private void validateStatement() {
		String subject = lastStatement.getSubject().toString().trim();
		String predicate = lastStatement.getPredicate().toString().trim();
		RDFNode object = lastStatement.getObject();

		if (object.isResource()) {
			Resource resource = object.asResource();
			String uri = resource.getURI().trim();
			object = model.createResource(uri);
		} else if (object.isLiteral()) {
			Literal literal = object.asLiteral();

			RDFDatatype datatype = literal.getDatatype();
			String language = literal.getLanguage().trim();

			if (datatype != null)
				object = model.createTypedLiteral(literal.getValue(), datatype);
			else if (!"".equals(language))
				object = model.createLiteral(literal.getString().replaceAll("\"", "").replaceAll("''", ""), language);
			else
				object = model.createLiteral(literal.getLexicalForm().replaceAll("\"", "").replaceAll("''", ""));
		}

		Statement newStmt = model.createStatement(model.createResource(subject), model.createProperty(predicate),
				object);

		model.remove(lastStatement);
		model.add(newStmt);

		lastStatement = newStmt;
	}

	public Statement getLastStatement() {
		return lastStatement;
	}
}