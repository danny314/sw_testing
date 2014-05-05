package pset3;


import java.util.ArrayList;
import java.util.List;

/*
 * @author Puneet Bansal
 */
public class TestGenerator {
	
	private long testCaseNumber = 0L;
	
	private static final String NEWLINE = "\n";
	
	public void generateAllCombinationsTests(MethodDeclaration decl,ValueDomains ids) {
		
		int combinationCount =  1;
				
		String[] paramTypes = decl.getParameterTypeNames();
		int methodArgsCount = paramTypes.length;
		
		for (String argTypeName : paramTypes) {
			List<Object> domain = ids.getDomain(argTypeName);
			combinationCount = combinationCount * domain.size();
		}
		
		String testCase = null;
		
		List<Object> methodArgs = new ArrayList<Object>(); 
		
			for (int i=0; i < combinationCount; i++) {
				methodArgs.clear();
				for (int k=0; k < methodArgsCount; k++) {
					List<Object> domain = ids.getDomain(paramTypes[k]);
					
					int index = 0;
					
					if (k == 0) {
						int cycle = combinationCount / domain.size();
						index = i / cycle;
					} 
					else if (k == (paramTypes.length - 1)) {
						index = i % domain.size();
					} else {
						int nextDomainSize = ids.getDomain(paramTypes[k+1]).size();
						index = (i / nextDomainSize) % domain.size();
					}
					
					Object argument = domain.get(index);
					methodArgs.add(argument);
				}
				testCase = generateTestCase(decl,methodArgs);	
				System.out.println(testCase);
			}
	}
	
	private String generateTestCase(MethodDeclaration decl,	List<Object> methodArgs) {
		
		StringBuilder testCase = new StringBuilder(NEWLINE).append("@Test").append(" public void testCase").
				append(testCaseNumber).append(" {").append(NEWLINE).append("\t");
		
		if (decl.isStatic()) {
			testCase.append(decl.getEnclosingClassName()).append(".");
		} else {
			testCase.append("new ").append(decl.getEnclosingClassName()).append("().");
		}
		
		testCaseNumber++;
		
		testCase.append(decl.getName()).append("(");
		
		for (Object arg : methodArgs) {
			testCase.append(arg).append(",");
		}
		
		int commaIndex = testCase.lastIndexOf(",");
		testCase.deleteCharAt(commaIndex);
		testCase.append(");").append(NEWLINE).append("}");
		
		return testCase.toString();
	}

	public static void main(String[] args) {
		new TestGenerator().generateAllCombinationsTests(
			new MethodDeclaration(true, "myMethod", "MyClass", new String[]{ "boolean", "int", "String"}, "void"),new ValueDomains());

		}
}
