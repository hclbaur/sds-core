package be.baur.sds.content;

/**
 * A <code>Reference</code> refers to a previously defined type.
 * If the name is unspecified (null or empty), it is equal to the name of the referenced type.
 * In terms of SDS, the difference is this:<br><br>
 * <code>node{ name "mobile" type "phone" }</code> (explicitly named mobile) versus<br>
 * <code>node{ type "phone" }</code> (name will be phone as well)<br><br>
 * assuming that <code>phone</code> was defined as a global type, for example:<br><br>
 * <code>node{ name "phone" type "string" }</code><br><br>
 */
public class Reference extends AnyType {

	private String reftype;	// the type this reference... refers to :)
	
	/** Create a type reference. The name may be null or empty. */
	public Reference(String name, String type) {
		super(name); reftype = type;
	}
	
//	/** Returns the referenced type. */
//	public SimpleType getReferencedType() {
//		return reftype;
//	}
//
//	/** Sets the referenced type. This cannot be a reference itself, or any type. 
//	 * @throws IllegalArgumentException
//	 * */
//	public void setReferencedType(SimpleType type) {
//		if (reftype instanceof AnyType) 
//			throw new IllegalArgumentException("referenced type cannot be a reference or any type");
//		reftype = type;
//	}

	@Override
	public String getType() {
		return reftype;
	}

}
