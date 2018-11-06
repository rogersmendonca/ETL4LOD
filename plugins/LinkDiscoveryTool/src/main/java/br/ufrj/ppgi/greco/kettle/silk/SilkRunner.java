package br.ufrj.ppgi.greco.kettle.silk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.pentaho.di.core.exception.KettleException;
import br.ufrj.ppgi.greco.kettle.LinkDiscoveryToolStepMeta;

public class SilkRunner {

	private LinkDiscoveryToolStepMeta input;
	private File configFile;
	private File log;

	public SilkRunner(LinkDiscoveryToolStepMeta input) {
		this.input = input;
	}

	/**
	 * Saves the SLS-XML file using the params on the view.
	 * 
	 * @throws KettleException
	 */
	public void saveXML() throws KettleException {
		Silk silk = new Silk(input);
		try {
			this.configFile = File.createTempFile("sls", ".xml");
			JAXBContext context = JAXBContext.newInstance(Silk.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(silk, this.configFile);
		} catch (Exception e) {
			throw new KettleException("Saving config file error: " + e.getMessage());
		}
	}

	/**
	 * Runs a SLS-XML file using Silk Single Machine.
	 * 
	 * @param slsFilePath
	 *            SLS file path
	 * @throws IOException
	 *             if SLS file not found
	 * @throws KettleException
	 *             if Silk.jar not found
	 * @throws InterruptedException
	 *             silk.jar was interrupted
	 */
	public void run(String slsFilePath) throws IOException, KettleException, URISyntaxException, InterruptedException {
		String silkSingleMachine = "";
		silkSingleMachine = Paths
				.get(new File(SilkRunner.class.getProtectionDomain().getCodeSource().getLocation().toURI())
						.getParentFile().getPath(), "silk.jar")
				.toString();
		if (new File(silkSingleMachine).exists()) {
			ProcessBuilder pb = new ProcessBuilder("java", "-DconfigFile=" + slsFilePath, "-jar", silkSingleMachine);
			if (!input.isSparqlOutput()){
				File result = new File(input.getFilePath());
				pb.redirectOutput(result);
			}
			log = File.createTempFile("status", ".log");
			pb.redirectError(log);
			Process p = pb.start();
			p.waitFor();
			p.destroy();
			this.configFile.deleteOnExit();
			this.log.deleteOnExit();
		} else {
			throw new KettleException(silkSingleMachine
					+ " Silk single machine was not found! Make sure you have silk.jar as a dependency.");
		}
	}

	public String getConfigFilename() {
		return configFile.getAbsolutePath();
	}

	private String getContents(File file) throws IOException {
		BufferedReader buffer = new BufferedReader(new FileReader(file));
		try {
			StringBuilder builder = new StringBuilder();
			String line = buffer.readLine();
			while (line != null) {
				builder.append(line).append("\n");
				line = buffer.readLine();
			}
			return builder.toString();
		} finally {
			buffer.close();
		}
	}

	public String getLogs() throws IOException {
		return getContents(log);
	}

}
