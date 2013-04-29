package ch.ethz.sae;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.toolkits.graph.BriefUnitGraph;


public class Verifier {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java -classpath soot-2.5.0.jar:./bin ch.ethz.sae.Verifier <class to test>");
			System.exit(-1);			
		}
		String analyzedClass = args[0];
		SootClass c = loadClass(analyzedClass);
		for (SootMethod method : c.getMethods()) {
			Analysis analysis = new Analysis(new BriefUnitGraph(method.retrieveActiveBody()));
			analysis.run();
			if (analysis.provedMethodSafe()) {
				System.out.println("Method " + method.getName() + " is SAFE");
			} else {
				System.out.println("Method " + method.getName() + " may be UNSAFE");
			}
		}
	}
	
	private static SootClass loadClass(String name) {
		SootClass c = Scene.v().loadClassAndSupport(name);
		c.setApplicationClass();
		return c;
	}	
}
