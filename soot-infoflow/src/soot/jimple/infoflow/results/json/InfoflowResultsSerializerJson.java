package soot.jimple.infoflow.results.json;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import soot.SootMethod;
import soot.jimple.Stmt;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.results.ResultSinkInfo;
import soot.jimple.infoflow.results.ResultSourceInfo;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
import soot.util.MultiMap;

/**
 * Class for serializing FlowDroid results to Json
 *
 */

public class InfoflowResultsSerializerJson {

	protected boolean serializeTaintPath = true;
	protected IInfoflowCFG icfg;
	protected InfoflowConfiguration config;
	protected long startTime = 0;

	/**
	 * Creates a new instance of the InfoflowResultsSerializer class
	 */
	public InfoflowResultsSerializerJson() {
		this(null, null);
	}

	/**
	 * Creates a new instance of the InfoflowResultsSerializer class
	 * 
	 * @param cfg The control flow graph to be used for obtaining additional
	 *            information such as the methods containing source or sink
	 *            statements
	 */
	public InfoflowResultsSerializerJson(IInfoflowCFG cfg, InfoflowConfiguration config) {
		this.icfg = cfg;
		this.config = config;
	}

	/**
	 * Serializes the given FlowDroid result object into the given file
	 * 
	 * @param results  The result object to serialize
	 * @param fileName The target file name
	 * @throws FileNotFoundException Thrown if target file cannot be used
	 */
	public void serialize(InfoflowResults results, String fileName) throws FileNotFoundException, IOException {
		this.startTime = System.currentTimeMillis();

		BufferedWriter wr = null;
		wr = new BufferedWriter(new FileWriter(fileName));

		JSONArray arr = new JSONArray();

		int i = 1;

		MultiMap<ResultSinkInfo, ResultSourceInfo> map = results.getResults();
		if (map != null)
			for (ResultSinkInfo res : map.keySet()) {
				JSONObject sink_result = new JSONObject();
				Stmt sink = res.getStmt();
				SootMethod sinkMethod = icfg.getMethodOf(sink);
				sink_result.put(JsonConstants.result, i);
				sink_result.put(JsonConstants.sink_stm, sink.toString());
				String classN = icfg.getMethodOf(sink).getDeclaringClass().getName();
				sink_result.put(JsonConstants.sink_class, classN);
				sink_result.put(JsonConstants.sink_line, sink.getJavaSourceStartLineNumber());
				if (sinkMethod != null)
					sink_result.put(JsonConstants.sink_method, (sinkMethod.getSignature()));
				else
					sink_result.put(JsonConstants.source_method, "");
				sink_result.put(JsonConstants.sink_access, "Type: " + res.getAccessPath().getBaseType().toString()
						+ " + Value: " + res.getAccessPath().getPlainValue().toString());

				JSONArray sources = new JSONArray();
				int i2 = 1;
				for (ResultSourceInfo res2 : map.get(res)) {
					JSONObject source_result = new JSONObject();
					Stmt source = res2.getStmt();
					SootMethod sourceMethod = icfg.getMethodOf(source);
					source_result.put(JsonConstants.source, i2);
					source_result.put(JsonConstants.source_stm, source.toString());
					String classN2 = icfg.getMethodOf(source).getDeclaringClass().getName();
					source_result.put(JsonConstants.source_class, classN2);
					if (sourceMethod != null)
						source_result.put(JsonConstants.source_method, (sourceMethod.getSignature()));
					else
						source_result.put(JsonConstants.source_method, "");
					source_result.put(JsonConstants.source_access, res2.getAccessPath().toString());
					source_result.put(JsonConstants.source_line, source.getJavaSourceStartLineNumber());

					JSONArray path = new JSONArray();
					int order = 1;
					for (Stmt s : res2.getPath()) {
						JSONArray pathTaint = new JSONArray();
						JSONObject stm_path = new JSONObject();
						stm_path.put(order,
								"Statement: " + s.toString() + " + Line: " + s.getJavaSourceStartLineNumber()
										+ " + Method: " + this.icfg.getMethodOf(s).toString());
						order++;
						path.add(stm_path);
					}
					JSONObject sinkAgain = new JSONObject();
					sinkAgain.put(order, res.getStmt().toString());
					if (!path.isEmpty())
						source_result.put(JsonConstants.path, path);
					source_result.put(JsonConstants.source_access,
							"Type: " + res2.getAccessPath().getBaseType().toString() + " + Value: "
									+ res2.getAccessPath().getPlainValue().toString());
					i2++;
					sources.add(source_result);
				}

				sink_result.put(JsonConstants.sources, sources);
				arr.add(sink_result);
				i++;
			}

		JSONObject parent = new JSONObject();
		parent.put(JsonConstants.results, arr);
		wr.write(parent.toJSONString());
		wr.flush();
		wr.close();
	}

}
