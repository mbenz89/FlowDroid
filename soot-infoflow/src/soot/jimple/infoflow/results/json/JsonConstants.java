package soot.jimple.infoflow.results.json;

/**
 * Class containing the tags for serializing data flow results to Json.
 *
 */
class JsonConstants {

	public static final String results = "Results";
	public static final String result = "ResultID";

	public static final String sink = "Sink";
	public static final String accessPath = "AccessPath";

	public static final String fields = "Fields";
	public static final String field = "Field";

	public static final String sources = "Sources";
	public static final String source = "Source number";
	public static final String path = "Taint path";

	public static final String sink_class = "Class of sink call";
	public static final String sink_stm = "Sink statement";
	public static final String sink_method = "Sink method";
	public static final String sink_line = "Sink line number";
	public static final String sink_line_java = "Sink line number (java)";
	public static final String sink_access = "Sink access path";

	public static final String source_class = "Class of source call";
	public static final String source_stm = "Source statement";
	public static final String source_method = "Source method";
	public static final String source_line = "Source line number";
	public static final String source_line_java = "Source line number (java)";
	public static final String source_access = "Source access path";

}
