# SDS Specification

The following grammar is an attempt to describe SDS in EBNF notation, with a few typographic changes for readability. First of all I'm omitting the , for concatenation of (non-)terminals with implied optional whitespace in between:

<pre>
    <b>foo</b> <b>bar</b> == <b>foo</b>, [ ? white space characters ? ], <b>bar</b>
</pre>

Second, I am using ? and + for at most once and at least once occurrence, and * for zero or more repetitions:

<pre>
    <b>foo</b>? <b>bar</b>+ <b>baz</b>* == [ <b>foo</b> ] <b>bar</b> { <b>bar</b> } { <b>baz</b> }
</pre>

And finally, < ... > denotes a group of (non-)terminals that may appear in any order: 

<pre>
    < <b>foo</b> <b>bar</b> > == ( <b>foo</b> <b>bar</b> ) | ( <b>bar</b> <b>foo</b> )
</pre>

With that in mind, this would be it (if at all I got it right):

<pre>

<b>sds</b> = 'schema'  '{'  <b>node_type</b>+  '}' ;

<b>node_type</b> = <b>node</b>  '{'  < <b>data_type</b> | ( <b>data_type</b>? <b>component</b>+ ) >  '}' ;

<b>node_type_with_occurs</b> = <b>node</b>  '{'  < ( <b>data_type</b> <b>occurs</b> ) | ( ( <b>data_type</b> <b>occurs</b> )? <b>component</b>+ ) >  '}' ;

<b>type_reference</b> = ( 'node' | <b>node</b> )  '{'  < ( 'type'  '"', <b>tag</b>, '"' ) <b>occurs</b>? >  '}' ;

<b>any_type</b> = ( 'node' | <b>node</b> )  '{'  < ( 'type'  '"any"' ) <b>occurs</b>? >  '}' ;

<b>component</b> = <b>node_type</b> | <b>node_type_with_occurs</b> | <b>type_reference</b>  | <b>any_type</b> | <b>model_group</b> ;

<b>model_group</b> = <b>choice_group</b> | <b>sequence_group</b> | <b>unordered_group</b> ;

<b>choice_group</b> = 'choice'  '{'  < <b>occurs</b>? <b>component</b> <b>component</b>+ >  '}' ;

<b>sequence_group</b> = 'group'  '{'  < <b>occurs</b>? <b>component</b> <b>component</b>+ >  '}' ;

<b>unordered_group</b> = 'unordered'  '{'  < <b>occurs</b>? <b>component</b> <b>component</b>+ >  '}' ;

<b>node</b> = 'node'  '"', <b>tag</b>, '"' ;
<b>tag</b> = ? a valid SDA node name (refer to SDA specification) ? ;

<b>occurs</b> = 'occurs' '"', <b>cardinality</b>, '"' ;
<b>cardinality</b> = ? cardinality in natural interval notation ? ;

<b>data_type</b> = < ( <b>string_type</b> | <b>value_type</b> | <b>boolean_type</b> ) <b>pattern</b>? <b>nullable</b>? > ;

<b>string_type</b> = < ( 'type' ( '"string"' | '"binary"' ) ) <b>length_facet</b>? > ;
<b>length_facet</b> = 'length' '"', <b>length</b>, '"' ;
<b>length</b> = ? minimum and maximum length in natural interval notation ? ;

<b>value_type</b> = < ( 'type' ( '"integer"' | '"decimal"' | '"date"' | '"datetime"' ) ) <b>value_facet</b>? > ;
<b>value_facet</b> = 'value' '"', <b>value</b>, '"' ;
<b>value</b> = ? minimum and maximum value in interval notation ? ;

<b>boolean_type</b> = 'type'  '"boolean"' ;

<b>pattern</b> = 'pattern' '"', <b>regexp</b>, '"' ;
<b>regexp</b> = ? a regular expression ? ;

<b>nullable</b> = 'nullable'  ( '"true"' | '"false"' ) ;

</pre>


### In words

An SDS document consists of a single schema node, which contains (global) node types. A node type may consist of a data type and/or "components" (at least either one) where a component is another node type, a type reference, a type of any (undefined) content or a model group. A model group is a choice - or (unordered) group - of two or more components. A data type is a simple type (string, integer, date, etc) with optional facets restricting length or value, lexical representation or specifying nullability.
 


