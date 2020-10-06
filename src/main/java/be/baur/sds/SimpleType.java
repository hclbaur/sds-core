package be.baur.sds;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import be.baur.sda.ComplexNode;
import be.baur.sda.Node;
import be.baur.sda.SimpleNode;
import be.baur.sds.common.Attribute;
import be.baur.sds.common.Component;
import be.baur.sds.common.Content;
import be.baur.sds.common.Date;
import be.baur.sds.common.DateTime;
import be.baur.sds.common.Interval;
import be.baur.sds.common.NaturalInterval;
import be.baur.sds.content.AnyType;
import be.baur.sds.content.BinaryType;
import be.baur.sds.content.BooleanType;
import be.baur.sds.content.DateTimeType;
import be.baur.sds.content.DateType;
import be.baur.sds.content.DecimalType;
import be.baur.sds.content.IntegerType;
import be.baur.sds.content.RangedType;
import be.baur.sds.content.Reference;
import be.baur.sds.content.StringType;

/**
 * A <code>SimpleType</code> defines a simple SDA node, with a content type like
 * a string, integer, date, etc. As it does not need to contain other components
 * it extends {@link SimpleNode}.<br>
 * All types have a multiplicity field indicating how often the component may
 * occur within its context (default is mandatory and singular).
 */
public abstract class SimpleType extends SimpleNode implements Type {

	private NaturalInterval multiplicity = null;	// Multiplicity null means: exactly once.
	private String pattexp = null;  				// Regular expression defining the pattern.
	private Pattern pattern = null;					// Pre-compiled pattern (from expression).
	private Boolean nullable = false;				// Default null-ability is false.
	
	
	/** Creates a simple type. */
	public SimpleType(String name) {
		super(name, null); // the value field is currently not used
	}
	
	/** Returns the content type name. */
	public abstract String getType();
	
	public NaturalInterval getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(NaturalInterval multiplicity) {
		this.multiplicity = multiplicity;
	}
	
	/** Returns the (pre-compiled) pattern for this type. */
	public Pattern getPattern() {
		return pattern;
	}

	/** Returns the pattern as a regular expression. */
	public String getPatternExpr() {
		return pattexp;
	}

	/**
	 * Sets the pattern for this type from a regular expression.
	 * @throws PatternSyntaxException if the expression is invalid.
	 */
	public void setPatternExpr(String regexp) {
		if (regexp == null || regexp.isEmpty()) {
			pattexp = null; pattern = null;
		}
		else {
			pattern = Pattern.compile(regexp);
			pattexp = regexp; // set after successful compile!
		}
	}

	/** Returns the null-ability (if that is even a word). */
	public Boolean isNullable() {
		return nullable;
	}

	/** Sets the null-ability (if not equal to <code>null</null>). */
	public void setNullable(Boolean nullable) {
		if (nullable != null) this.nullable = nullable;
	}

	public ComplexNode toNode() {
		
		ComplexNode node = new ComplexNode(Component.NODE.tag);
		
		if (!(this instanceof AnyType) || ((AnyType) this).isNamed()) {
			node.add(new SimpleNode(Attribute.NAME.tag, name));
		}
		
		node.add(new SimpleNode(Attribute.TYPE.tag, getType()));
		
		if (minOccurs() != 1 || maxOccurs() != 1)
			node.add(new SimpleNode(Attribute.MULTIPLICITY.tag, multiplicity.toString()));
		
		boolean stringType = (this instanceof StringType);
		if (stringType) {
			StringType t = (StringType) this;
			if (t.minLength() != 0 || t.maxLength() != Integer.MAX_VALUE)
				node.add(new SimpleNode(Attribute.LENGTH.tag, t.getLength().toString()));
		}
		
		//boolean rangedType = (this instanceof RangedType);
		if (this instanceof RangedType) {
			RangedType<?> t = (RangedType<?>) this;
			if (t.getRange() != null)
				node.add(new SimpleNode(Attribute.VALUE.tag, t.getRange().toString()));
		}
		
		if (pattern != null)
			node.add(new SimpleNode(Attribute.PATTERN.tag, pattexp));
		
		if (stringType == !nullable)
			node.add(new SimpleNode(Attribute.NULLABLE.tag, nullable.toString()));
		
		return node;
	}

	public String toString() {
		return toNode().toString();
	}

	
	/**
	 * This is a factory method to construct a <code>SimpleType</code> from a node
	 * representing SDS content. For example: <br>
	 * <code>node{ name "firstname" type "string" }</code> defines a (simple) SDA
	 * node "firstname" with string content.<br>
	 * <br>
	 * This method should only be called by {@link Type}<code>.from()</code> after
	 * some sanity checks. Do not make it public.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static <T extends Comparable> SimpleType from(ComplexNode sds) throws SchemaException {
		/*
		 * Sanity checks: the caller has already verified that we have no complex child
		 * nodes, that all of our simple nodes (attributes) have valid tags, and that we
		 * are a 'node' component and not something else. In any case, a simple type
		 * definition must always have a (content) type attribute.
		 */
		SimpleNode type = Attribute.getNode(sds, Attribute.TYPE, true);
		Content content = Content.get(type.value); // if null, it could be a type reference.

		// The name is mandatory except for a type reference (content is null) or the "any" type.
		boolean isAnyOrReference = (content == null || content == Content.ANY);
		SimpleNode name = Attribute.getNode(sds, Attribute.NAME, !isAnyOrReference);
		
		SimpleType sType;	// the simple type that will be returned at the end of this method.
		
		// If the content type is unknown (null) we must assume we are dealing with a reference,
		// and we will check if the referred type has been globally defined (in the schema root).
		// Later, we will probably move this to a second pass validation.
		if (content == null) {
			
			ComplexNode root = (ComplexNode) sds.getRoot();
			if (root.equals(sds)) // if we are the root ourself, we bail out right away.
				throw new SchemaException(type, SchemaException.ContentTypeUnknown, type.value);
			
			// We now search all node declarations in the root for the referenced type.
			Node refNode = null;
			for (Node cnode : root.get().get(ComplexNode.class).get(Component.NODE.tag)) {
				for (Node snode : ((ComplexNode) cnode).get().get(SimpleNode.class).get(Attribute.NAME.tag)) {
					if ( ((SimpleNode) snode).value.equals(type.value) ) refNode = cnode; break;
				}
				if (refNode != null) break;
			}
			if (refNode == null || refNode.equals(sds)) // if we found nothing or ourself, we raise an error.
				throw new SchemaException(type, SchemaException.ContentTypeUnknown, type.value);
			
			// if we get here, we can create the reference
			sType = new Reference(name == null ? null : name.value, type.value);
		}
		else switch (content) {
			case STRING   : sType = new StringType(name.value); break;
			case BINARY   : sType = new BinaryType(name.value); break;
			case BOOLEAN  : sType = new BooleanType(name.value); break;
			case INTEGER  : sType = new IntegerType(name.value); break;
			case DECIMAL  : sType = new DecimalType(name.value); break;
			case DATETIME : sType = new DateTimeType(name.value); break;
			case DATE     : sType = new DateType(name.value); break;
			case ANY      : sType = new AnyType(name == null ? null : name.value); break;
			default: // will never get here, unless we forgot to implement something...
				throw new RuntimeException("SDS type '" + content + "' not implemented!");
		}
		
		/* In the remainder of this method we will handle all remaining attributes.
		 * Note that most attributes are forbidden on a type reference or "any" type.
		 */
		
		// Set the multiplicity (if the attribute is present).
		SimpleNode multiplicity = Attribute.getNode(sds, Attribute.MULTIPLICITY, false);
		try {
			if (multiplicity != null) {
				NaturalInterval interval = NaturalInterval.from(multiplicity.value);
				sType.setMultiplicity(interval);
			}
		} catch (Exception e) {
			throw new SchemaException(multiplicity, 
				SchemaException.AttributeInvalid, multiplicity.name, multiplicity.value, e.getMessage());
		}
		
		// Set the null-ability (if the attribute is present and allowed).
		SimpleNode nullable = Attribute.getNode(sds, Attribute.NULLABLE, isAnyOrReference? null : false);
		if (nullable != null) switch(nullable.value) {
			case "true"  : sType.setNullable(true); break;
			case "false" : sType.setNullable(false); break;
			default : throw new SchemaException(nullable, 
				SchemaException.AttributeInvalid, nullable.name, nullable.value, "must be 'true' or 'false'");
		}
		
		// Set the pattern (if the attribute is present and allowed).
		SimpleNode pattern = Attribute.getNode(sds, Attribute.PATTERN, isAnyOrReference? null : false);
		try { 
			if (pattern != null) sType.setPatternExpr(pattern.value); 
		} catch (Exception e) {
			throw new SchemaException(pattern, 
				SchemaException.AttributeInvalid, pattern.name, pattern.value, e.getMessage());
		}
		
		// Set the length (this attribute is only allowed on string and binary types).
		SimpleNode length = Attribute.getNode(sds, 
				Attribute.LENGTH, (sType instanceof StringType) ? false : null);
		if (length != null) {
			try {
				NaturalInterval interval = NaturalInterval.from(length.value);
				((StringType) sType).setLength(interval);
			} catch (Exception e) {
				throw new SchemaException(length, SchemaException.AttributeInvalid, length.name, length.value, e.getMessage());
			}
		}
		
		// Set the value range (this attribute is only allowed on ranged types).
		SimpleNode range = Attribute.getNode(sds, 
				Attribute.VALUE, (sType instanceof RangedType) ? false : null);
		if (range != null) {
			try {	
				Interval<T> interval;
				switch (content) {
					case INTEGER  : interval = (Interval<T>) Interval.from(range.value, Integer.class); break;
					case DECIMAL  : interval = (Interval<T>) Interval.from(range.value, Double.class); break;
					case DATETIME : interval = (Interval<T>) Interval.from(range.value, DateTime.class); break;
					case DATE     : interval = (Interval<T>) Interval.from(range.value, Date.class); break;
					default: // we will never get here, unless we forgot to implement something
						throw new RuntimeException("SDS type '" + content + "' not implemented!");
				}
				((RangedType<T>) sType).setRange((Interval<T>) interval);
			} catch (Exception e) {
				throw new SchemaException(range, 
					SchemaException.AttributeInvalid, range.name, range.value, e.getMessage());
			}
		}
		
		return sType;
	}

}
