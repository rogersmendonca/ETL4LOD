package br.ufrj.ppgi.greco.kettle.silk;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlAttribute;

@XmlRootElement(name = "Compare")
public class Metric {

	@XmlAttribute
	private String id;

	@XmlAttribute
	private String required;

	@XmlAttribute
	private String weight;

	@XmlAttribute
	private String metric;

	@XmlAttribute
	private String threshold;

	@XmlAttribute
	private String indexing;

	@XmlElement(name = "Input")
	private List<Input> inputs = new ArrayList<>();

	@XmlElement(name = "Param")
	private List<Param> params = new ArrayList<>();

	@XmlTransient
	public static final LinkedHashMap<String, String> metrics = new LinkedHashMap<String, String>() {
		{
			put("Jaro distance", "jaro");
			put("Jaro-Winkler distance", "jaroWinkler");
			put("Levenshtein distance", "levenshteinDistance");
			put("Normalized Levenshtein distance", "levenshtein");
			put("Substring", "substring");
			put("qGrams", "qGrams");

			put("Constant", "constant");
			put("Equality", "equality");
			put("Inequality", "inequality");
			put("Lower Than", "lowerThan");
			put("Relaxed Equality", "relaxedEquality");

			put("Date", "date");
			put("Date Time", "dateTime");
			put("Geographical Distance", "wgs84");
			put("Inside numeric interval", "insideNumericInterval");
			put("Numeric similarity", "num");

			put("Centroid distance", "CentroidDistanceMetric");
			put("Crosses", "CrossesMetric");
			put("Disjoint", "DisjointMetric");
			put("Intersects", "IntersectsMetric");
			put("Min distance", "MinDistanceMetric");
			put("Relate", "RelateMetric");
			put("Spatial Contains", "SContainsMetric");
			put("Spatial Equals", "SEqualsMetric");
			put("Touches", "TouchesMetric");
			put("Within", "WithinMetric");

			put("After", "AfterMetric");
			put("Before", "BeforeMetric");
			put("Days distance", "DaysDistanceMetric");
			put("During", "DuringMetric");
			put("Finishes", "FinishesMetric");
			put("Hours distance", "HoursDistanceMetric");
			put("is Finished By", "isFinishedByMetric");
			put("is Met By", "IsMetByMetric");
			put("is Overlapped By", "IsOverlappedByMetric");
			put("is Started By", "IsStartedByMetric");
			put("Meets", "MeetsMetric");
			put("Millisecs distance", "MillisecsDistanceMetric");
			put("Mins distance", "MinsDistanceMetric");
			put("Months distance", "MonthsDistanceMetric");
			put("Secs distance", "SecsDistanceMetric");
			put("Starts", "StartsMetric");
			put("Temporal Contains", "TContainsMetric");
			put("Temporal Equals", "TEqualsMetric");
			put("Temporal Overlaps", "TOverlapsMetric");
			put("Years distance", "YearsDistanceMetric");

			put("Cosine", "consine");
			put("Dice Coefficient", "dice");
			put("Jaccard", "jaccard");
			put("Soft Jaccard", "softjaccard");
			put("Token-wise distance", "tokenwiseDistance");
		}
	};

	public Metric() {
	}

	public Metric(int id, String metric) {
		this.id = getMetric(metric) + id;
		this.required = "false";
		this.metric = getMetric(metric);
		this.weight = "1";
		this.threshold = "0.0";
		this.indexing = "true";
	}

	public Metric(String id, String required, String weight, String metric, String threshold,
			String indexing) {
		this.id = id;
		this.required = required;
		this.metric = getMetric(metric);
		this.weight = weight;
		this.threshold = threshold;
		this.indexing = indexing;
	}

	public void addInput(Input input) {
		this.inputs.add(input);
	}

	public void addParam(Param param) {
		this.params.add(param);
	}
	
	public static String[] getMetricsNames(){
		return metrics.keySet().toArray(new String[0]);
	}
	
	public String getMetric(String metric) {
		return metrics.get(metric);
	}

}