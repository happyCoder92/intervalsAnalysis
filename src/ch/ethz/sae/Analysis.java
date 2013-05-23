package ch.ethz.sae;

import static ch.ethz.sae.IntervalHelper.i;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.BinopExpr;
import soot.jimple.ConditionExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.DivExpr;
import soot.jimple.EqExpr;
import soot.jimple.GeExpr;
import soot.jimple.GotoStmt;
import soot.jimple.GtExpr;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.LeExpr;
import soot.jimple.LtExpr;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.OrExpr;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.SubExpr;
import soot.jimple.UshrExpr;
import soot.jimple.XorExpr;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.toolkits.graph.LoopNestTree;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardBranchedFlowAnalysis;

// Implement your numerical analysis here.
public class Analysis extends ForwardBranchedFlowAnalysis<IntervalPerVar> {
	public Analysis(UnitGraph g) {
		super(g);
		method = g.getBody().getMethod();
		safe = true;
		LoopNestTree loopTree = new LoopNestTree(g.getBody());
		loopsExecs = new HashMap<Unit, Integer>();
		loopsBacksToFront = new HashMap<Unit, Unit>();
		for (Loop loop : loopTree) {
			debug.println("\tbegin: "+loop.getHead());
			debug.println("\tend: "+loop.getBackJumpStmt());
			loopsBacksToFront.put(loop.getBackJumpStmt(), loop.getHead());
			loopsExecs.put(loop.getHead(), 0);
		}
		// System.out.println(g.toString());
	}

	void run() {
		doAnalysis();
	}

	static void unhandled(String what) {
		System.err.println("Can't handle " + what);
		System.exit(1);
	}

	@Override
	protected void flowThrough(IntervalPerVar current, Unit op,
			List<IntervalPerVar> fallOut, List<IntervalPerVar> branchOuts) {
		// TODO: This can be optimized.
		// System.out.println("Operation: " + op + "   - " +
		// op.getClass().getName() + "\n      state: " + current);

		Stmt s = (Stmt) op;
		this.current = current;
		fallState = new IntervalPerVar();
		fallState.copyFrom(current);
		branchState = new IntervalPerVar();
		branchState.copyFrom(current);
		
		if (loopsBacksToFront.containsKey(op)) {
			Unit front = loopsBacksToFront.get(op);
			loopsExecs.put(front, loopsExecs.get(front)+1);
		}
		
		if (current.isBottom()) {
			debug.println("State is bottom at: "+s);
			fallState = IntervalPerVar.bottom();
			branchState = IntervalPerVar.bottom();
		}
		else if (s instanceof DefinitionStmt) {
			// handles also AssignStmt and IdentityStmt
			debug.println("Definition stmt: "+s);
			DefinitionStmt sd = (DefinitionStmt) s;
			Value left = sd.getLeftOp();
			Value right = sd.getRightOp();
			// System.out.println(left.getClass().getName() + " " +
			// right.getClass().getName());

			// You do not need to handle these cases:
			if ((!(left instanceof StaticFieldRef))
					&& (!(left instanceof JimpleLocal))
					&& (!(left instanceof JArrayRef))
					&& (!(left instanceof JInstanceFieldRef)))
				unhandled("1: Assignment to non-variables is not handled.");

			else if ((left instanceof JArrayRef)
					&& (!((((JArrayRef) left).getBase()) instanceof JimpleLocal)))
				unhandled("2: Assignment to a non-local array variable is not handled.");

			else if (left instanceof JimpleLocal) {
				String varName = ((JimpleLocal) left).getName();
				localDefinition(varName, right);
			} else if (left instanceof StaticFieldRef) {
					// TODO do we need it?
			} else if (left instanceof JStaticInvokeExpr) {
					// TODO do we need it?
			}
		} else if (s instanceof JInvokeStmt) {
			// A method is called. e.g. AircraftControl.adjustValue
			debug.println("Invoke stmt: "+s);
			// You need to check the parameters here.
			InvokeExpr expr = s.getInvokeExpr();
			if (expr.getMethod().getName().equals("adjustValue")) {
				// TODO: Check that is the method from the AircraftControl
				// class.
				if (expr.getMethod().getDeclaringClass().getName()
						.equals("AircraftControl")) {
					Interval a = tryGetIntervalForValue(current, expr.getArg(0));
					Interval b = tryGetIntervalForValue(current, expr.getArg(1));
					if (!i(0, 15).contains(a)) {
						safe = false;
					}
					if (!i(-999, 999).contains(b)) {
						safe = false;
					}
					// TODO: Check that the values are in the allowed range (we do
					// this while computing fixpoint).
					// System.out.println(expr.getArg(0) + " " + expr.getArg(1));
				}	
			}
		} else if (s instanceof IfStmt) {
			debug.println("If stmt: "+s);
			ifStatement((IfStmt)s);		
		} else if (s instanceof GotoStmt) {
			fallState.copyFrom(IntervalPerVar.bottom());
		}
		else {
			// NopStmt, TableSwitchStmt, LookupSwitchStmt
			// ReturnStmt (need to handle?), ReturnVoidStmt, EnterMonitorStmt,
			// ExitMonitorStmt, ThrowStmt, RetStmt
			debug.println("Unhandled stmt: "+s);
		}
		
		debug.println("\tCurrent:"+current);
		debug.println("\tFall:"+fallState);
		debug.println("\tBranch:"+branchState);

		// TODO: Maybe avoid copying objects too much. Feel free to optimize.
		for (IntervalPerVar fnext : fallOut) {
			if (fallState != null) {
				fnext.copyFrom(fallState);
			}
		}
		for (IntervalPerVar fnext : branchOuts) {
			if (branchState != null) {
				fnext.copyFrom(branchState);
			}
		}
	}
	
	private static abstract class ConditionStrategy {
		protected abstract Interval transformFirst(Interval a, Interval b);
		protected abstract Interval transformFirstNeg(Interval a, Interval b);
		protected abstract Interval transformSecond(Interval a, Interval b);
		protected abstract Interval transformSecondNeg(Interval a, Interval b);
	}
	
	private void localDefinition(String varName, Value right) {
		if (right instanceof IntConstant) {
			IntConstant c = ((IntConstant) right);
			fallState.putIntervalForVar(varName, new Interval(c.value,
					c.value));
		} else if (right instanceof JimpleLocal) {
			JimpleLocal l = ((JimpleLocal) right);
			fallState.putIntervalForVar(varName,
					current.getIntervalForVar(l.getName()));
		} else if (right instanceof BinopExpr) {
			fallState.putIntervalForVar(varName, binaryExpr((BinopExpr)right));
		} else if (right instanceof StaticInvokeExpr) {
			if (((StaticInvokeExpr) right).getMethod().getName().equals("readSensor")) {
				// check param
				if (((StaticInvokeExpr) right).getMethod().getDeclaringClass().getName().equals("AircraftControl")) {
					Interval a = tryGetIntervalForValue(current, ((StaticInvokeExpr) right).getArg(0));
					if (!i(0, 15).contains(a)) {
						safe = false;
					}
					fallState.putIntervalForVar(varName, i(-999, 999));
				} else {
					fallState.putIntervalForVar(varName, Interval.top());
				}
			} else {
				fallState.putIntervalForVar(varName, Interval.top());
			}
		} else {
			fallState.putIntervalForVar(varName, Interval.top());
		}
	}
	
	private Interval binaryExpr(BinopExpr expr) {
		Value r1 = expr.getOp1();
		Value r2 = expr.getOp2();

		Interval i1 = tryGetIntervalForValue(current, r1);
		Interval i2 = tryGetIntervalForValue(current, r2);

		if (i1 != null && i2 != null) {
			// Implement transformers.
			if (expr instanceof AddExpr) {
				return Interval.plus(i1, i2);
			} else if (expr instanceof SubExpr) {
				return Interval.minus(i1, i2);
			} else if (expr instanceof MulExpr) {
				return Interval.multiply(i1, i2);
			} else if (expr instanceof DivExpr) {
				return Interval.divide(i1, i2);
			} else if (expr instanceof AndExpr) {
				return Interval.and(i1, i2);
			} else if (expr instanceof OrExpr) {
				return Interval.or(i1, i2);
			} else if (expr instanceof XorExpr) {
				return Interval.xor(i1, i2);
			} else if (expr instanceof ShlExpr) {
				return Interval.shl(i1, i2);
			} else if (expr instanceof ShrExpr) {
				return Interval.shr(i1, i2);
			} else if (expr instanceof UshrExpr) {
				return Interval.slr(i1, i2);
			}
		}
		return Interval.top();
	}
		
	private void ifStatement(IfStmt si) {
		Value condition = si.getCondition();
		if (condition instanceof EqExpr) {
			handleConditon((ConditionExpr) condition, new ConditionStrategy() {
				protected Interval transformFirst(Interval a, Interval b) {
					return Interval.intersect(a, b);
				}
				protected Interval transformFirstNeg(Interval a, Interval b) {
					if (b.size()<2) {
						return Interval.minus(a,b);
					} 
					return a;
				}
				protected Interval transformSecond(Interval a, Interval b) {
					return Interval.intersect(a, b);
				}
				protected Interval transformSecondNeg(Interval a, Interval b) {
					if (a.size()<2) {
						return Interval.minus(b,a);
					} 
					return b;
				}
			});
		} else if (condition instanceof GeExpr) {
			handleConditon((ConditionExpr) condition, new ConditionStrategy() {
				protected Interval transformFirst(Interval a, Interval b) {
					return Interval.singleGreaterEqual(a,b);
				}
				protected Interval transformFirstNeg(Interval a, Interval b) {
					return Interval.singleLower(a,b);
				}
				protected Interval transformSecond(Interval a, Interval b) {
					return Interval.singleLowerEqual(b,a);
				}
				protected Interval transformSecondNeg(Interval a, Interval b) {
					return Interval.singleGreater(b,a);
				}
			});
		} else if (condition instanceof GtExpr) {
			handleConditon((ConditionExpr) condition, new ConditionStrategy() {
				protected Interval transformFirst(Interval a, Interval b) {
					return Interval.singleGreater(a,b);
				}
				protected Interval transformFirstNeg(Interval a, Interval b) {
					return Interval.singleLowerEqual(a,b);
				}
				protected Interval transformSecond(Interval a, Interval b) {
					return Interval.singleLower(b,a);
				}
				protected Interval transformSecondNeg(Interval a, Interval b) {
					return Interval.singleGreaterEqual(b,a);
				}
				
			});
		} else if (condition instanceof LeExpr) {
			handleConditon((ConditionExpr) condition, new ConditionStrategy() {
				protected Interval transformFirst(Interval a, Interval b) {
					return Interval.singleLowerEqual(a,b);
				}
				protected Interval transformFirstNeg(Interval a, Interval b) {
					return Interval.singleGreater(a,b);
				}
				protected Interval transformSecond(Interval a, Interval b) {
					return Interval.singleGreaterEqual(b,a);
				}
				protected Interval transformSecondNeg(Interval a, Interval b) {
					return Interval.singleLower(b,a);
				}
			});
			
		} else if (condition instanceof LtExpr) {
			handleConditon((ConditionExpr) condition, new ConditionStrategy() {
				protected Interval transformFirst(Interval a, Interval b) {
					return Interval.singleLower(a,b);
				}
				protected Interval transformFirstNeg(Interval a, Interval b) {
					return Interval.singleGreaterEqual(a,b);
				}
				protected Interval transformSecond(Interval a, Interval b) {
					return Interval.singleGreater(b,a);
				}
				protected Interval transformSecondNeg(Interval a, Interval b) {
					return Interval.singleLowerEqual(b,a);
				}
			});
		} else if (condition instanceof NeExpr) {
			handleConditon((ConditionExpr) condition, new ConditionStrategy() {
				protected Interval transformFirst(Interval a, Interval b) {
					if (b.size()<2) {
						return Interval.minus(a,b);
					} 
					return a;
				}
				protected Interval transformFirstNeg(Interval a, Interval b) {
					return Interval.intersect(a, b);
				}
				protected Interval transformSecond(Interval a, Interval b) {
					if (a.size()<2) {
						return Interval.minus(b,a);
					} 
					return b;
				}
				protected Interval transformSecondNeg(Interval a, Interval b) {
					return Interval.intersect(a, b);
				}	
			});
		}
	}
	
	private void handleConditon(ConditionExpr ce, ConditionStrategy strategy) {
		Value left = ce.getOp1();
		Value right = ce.getOp2();
		
		debug.println("\thandle condition");
		debug.println("\t\tCondition: "+ce);
		debug.println("\t\tLeft: "+left);
		debug.println("\t\tRight: "+right);
		
		Interval i1 = tryGetIntervalForValue(current, left);
		Interval i2 = tryGetIntervalForValue(current, right);
		
		if ((!(left instanceof StaticFieldRef))
				&& (!(left instanceof JimpleLocal))
				&& (!(left instanceof JArrayRef))
				&& (!(left instanceof JInstanceFieldRef)))
			unhandled("3: Conditionals with non-variables is not handled.");

		else if ((left instanceof JArrayRef)
				&& (!((((JArrayRef) left).getBase()) instanceof JimpleLocal)))
			unhandled("4: Conditionals with non-local array variable is not handled.");

		else if (left instanceof JimpleLocal) {
			String varName = ((JimpleLocal) left).getName();

			if (right instanceof IntConstant) {
				Interval r1 = strategy.transformFirst(i1, i2);
				Interval r2 = strategy.transformFirstNeg(i1, i2);
				Interval l1 = strategy.transformSecond(i1, i2);
				Interval l2 = strategy.transformSecondNeg(i1, i2);
				if (l1.isBottom()) {
					branchState.copyFrom(IntervalPerVar.bottom());
				} else {
					branchState.putIntervalForVar(varName, r1);
				}
				if (l2.isBottom()) {
					fallState.copyFrom(IntervalPerVar.bottom());
				} else {
					fallState.putIntervalForVar(varName, r2);
				}
			} else if (right instanceof JimpleLocal) {
				String varName2 = ((JimpleLocal) right).getName();
				Interval r1 = strategy.transformFirst(i1, i2);
				Interval r2 = strategy.transformFirstNeg(i1, i2);
				Interval l1 = strategy.transformSecond(i1, i2);
				Interval l2 = strategy.transformSecondNeg(i1, i2);
				if (l1.isBottom()) {
					branchState.copyFrom(IntervalPerVar.bottom());
				} else {
					branchState.putIntervalForVar(varName, r1);
					branchState.putIntervalForVar(varName2, l1);
				}
				if (l2.isBottom()) {
					fallState.copyFrom(IntervalPerVar.bottom());
				} else {
					fallState.putIntervalForVar(varName, r2);
					fallState.putIntervalForVar(varName2, l2);
				}
			} else if (right instanceof BinopExpr || right instanceof StaticInvokeExpr) {
				unhandled("5: Exprs in conditionals");
			}

		} else if (left instanceof StaticFieldRef) {
				// TODO do we need it?
		} else if (left instanceof JStaticInvokeExpr) {
			// TODO do we need it?
		}
	}

	Interval tryGetIntervalForValue(IntervalPerVar currentState, Value v) {
		if (v instanceof IntConstant) {
			IntConstant c = ((IntConstant) v);
			return new Interval(c.value, c.value);
		} else if (v instanceof JimpleLocal) {
			JimpleLocal l = ((JimpleLocal) v);
			return currentState.getIntervalForVar(l.getName());
		}
		return null;
	}

	@Override
	protected void copy(IntervalPerVar source, IntervalPerVar dest) {
		dest.copyFrom(source);
	}

	@Override
	protected IntervalPerVar entryInitialFlow() {
		// TODO: How do you model the entry point?
		return new IntervalPerVar();
	}
	
	@Override
	protected void merge(IntervalPerVar src1, IntervalPerVar src2,
			IntervalPerVar trg) {
		// TODO: Fix this: 
		debug.println("Merge simple:");
		debug.println("\ta: "+src1);
		debug.println("\tb: "+src2);
		trg.merge(src1, src2);
		debug.println("\ttrg: "+trg);
		// System.out.printf("Merge:\n    %s\n    %s\n    ============\n    %s\n",
		// src1.toString(), src2.toString(), trg.toString());
	}

	/*@Override
	protected void merge(Unit succ, IntervalPerVar src1, IntervalPerVar src2,
			IntervalPerVar trg) {
		// TODO: Fix this:
		debug.println("Merge:" + succ);
		debug.println("\ta: "+src1);
		debug.println("\tb: "+src2);
		if (loopsExecs.containsKey(succ)) {
			if (loopsExecs.get(succ) > 5) {
				//System.exit(0);
				trg.widen(src1, src2);
				return;
			}
		}
		trg.merge(src1, src2);
		debug.println("\ttrg: "+trg);
		// System.out.printf("Merge:\n    %s\n    %s\n    ============\n    %s\n",
		// src1.toString(), src2.toString(), trg.toString());
	}*/

	@Override
	protected IntervalPerVar newInitialFlow() {
		return new IntervalPerVar();
	}

	public boolean provedMethodSafe() {
		return safe;
	}
	
	private final SootMethod method;
	private boolean safe;
	private IntervalPerVar current;
	private IntervalPerVar fallState;
	private IntervalPerVar branchState;
	private Map<Unit, Integer> loopsExecs;
	private Map<Unit, Unit> loopsBacksToFront;
	private static PrintStream debugPrt = System.out;
	private static PrintStream debugNoPrt = new PrintStream(new OutputStream() {
		@Override
		public void write(int b) throws IOException {
		}
	});
	private static PrintStream debug = debugPrt;
}
