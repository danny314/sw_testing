package pset3;


public class MethodDeclaration {

	private boolean isStatic;
	private String name;
	private String enclosingClassName;
	private String[] parameterTypeNames;
	private String returnTypeName;

	public MethodDeclaration(boolean isStatic, String name, String clazz,
			String[] paramtypes, String rettype) {
		this.isStatic = isStatic;
		this.name = name;
		enclosingClassName = clazz;
		parameterTypeNames = paramtypes;
		returnTypeName = rettype;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public String getName() {
		return name;
	}

	public String getEnclosingClassName() {
		return enclosingClassName;
	}

	public String[] getParameterTypeNames() {
		return parameterTypeNames;
	}

	public String getReturnType() {
		return returnTypeName;
	}

}
