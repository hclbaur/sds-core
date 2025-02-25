# Release Notes

## [2.5.0] - 2025-??-??

In development. This is a compatibility release for sda-core 2.3.0 with internal 
renaming and refactoring. However, unless you are writing schema parsers, this is
still likely to be a drop-in replacement for the previous release.

- `Renamed` Type to AbstractNodeType, AnyType to AnyNodeType, DataType to ValueNodeType.
- `Renamed` CharacterType to CharacterNodeType, ComparableType to ComparableNodeType.
- `Renamed` StringType to StringNodeType, IntegerType to IntegerNodeType, etc.
- `Renamed` Schema methods that support data type registration.
- `Removed` Validator.setTypeName() - use validate(node,type) instead.
- `Changed` Validator.validate(node) - no longer uses a default or set type.
- Added Validator.validate(node,type) and validateType(node,type) methods.


## Compatibility

- Requires Java 8 and sda-core 2.3.0.

## Previous releases

### [2.4.0] - 2024-05-10

This release add support for user defined data types. The internals 
of the library have been changed quite a bit but the impact on most
(if not all) application code should be limited.

- `Removed` be.baur.sds.common.Content.
- `Removed` be.baur.sds.common.Date and DateTime.
- `Changed` DataType.getContentType() to getType().
- `Changed` Interval.from(String, Class) to from(String, Function).
- `Changed` AbstractStringType to generic CharacterType<T>.
- `Changed` RangedType to generic ComparableType<T>.
- `Changed` Package .sds.content to .sds.types.
- Added Schema.registerDataType(), and get/isDataType().
- Added abstract Type, which AnyType and NodeType extend.
- Added static valueOf() methods for native RangedTypes.
- Added GMonthDay custom type example and tests.
- Added IBAN custom type example and tests.

### [2.3.0] - 2024-03-23 (requires at least SDA v2.2.0)

This started out as a compatibility release for sda-core 2.2.x, 
until I decided to change the SDS syntax and disallow a type 
attribute in the main schema section. So, as of this release, 
a schema no longer has a "default type", whatever that was.

- `Removed` Interface be.baur.sds.serialization.Parser.
- `Removed` SDS.validator() and Validator interface.
- `Removed` Error and ErrorList classes (use Errors).
- `Removed` Schema.set - and getDefaultTypeName().
- `Changed` SDAValidator to abstract Validator class.
- `Changed` Validator.validate(node,type) to validate(node).
- `Changed` NodeType.getName() to getTypeName().
- `Changed` SDS.parser() to SDS.parse().
- `Renamed` Schema.getDefaultType() to getDefaultTypeName().
- `Renamed` SchemaException to SDSParseException.
- `Renamed` MixedType to DataType.
- Added Schema.newValidator() and getGlobalType().
- Added Validator.setTypeName().
- Added docs/SPECIFICATION.

### [2.2.0] - 2023-10-04 (requires at least SDA v2.1.0)

This is a compatibility release for sda-core 2.1.x with minor changes.

- `Removed` MixedType.getPatternExpr().
- `Changed` MixedType.setPatternExpr() to setPattern().

### [2.1.0] - 2022-12-01

This release introduces a few refactoring changes with limited impact.

- Added abstract superclass MixedType for types with simple content.
- `Changed` Content Enum moved to package be.baur.sds.common.
- `Removed` (Natural)Interval constructors, added factory methods.
- `Removed` AbstractStringType.minLength() and maxLength().

### [2.0.0] - 2022-10-09 (requires at least SDA v2.0.0)

This release includes full SDS 2 schema support, for specification and 
validation of SDA documents, syntax version 2. You will have to update 
existing schema files, as SDS 2 is not backwards compatible with SDS 1.
Also, the library itself has changed quite a bit, but unless you have
been writing an SDS parser, there will be little impact when upgrading.

- Added NodeType to replace SimpleType and ComplexType.
- `Changed` ComponentType (interface) to Component (abstract class).
- `Changed` AbstractGroup to extend ComponentType (was ComplexType).
- `Changed` Parser.parse() to throw ParseException (was SyntaxException).
- `Changed` Syntax Enums moved to package be.baur.sds.serialization.
- `Renamed` AbstractGroup to ModelGroup, Group to SequenceGroup.
- `Renamed` Schema.getRootType() to Schema.getDefaultType().
- `Renamed` SchemaException.getNode() to SchemaException.getErrorNode().
- `Renamed` Component (Enum) to Components to prevent name clash.
- `Removed` SimpleType and ComplexType (replaced with NodeType).
- `Removed` Attribute.NAME (there is no *name* attribute in SDS 2).

### [1.2.4] - 2022-08-12 (requires at least SDA v1.6.2)
- `Removed` references to SimpleNode/ComplexNode classes.
- `Changed` Parser.parse() to throw IOException, SyntaxException, 
SchemaException (was Exception).

### [1.2.3] - 2022-06-28
- Fixed bug in validation of unordered groups.
- Added support for repeating components in unordered groups.

### [1.2.2] - 2022-06-17
- Fixed some issues with type casting.
- Added demo and documentation in markdown.
- `Removed` documentation in ODT format.

### [1.2.1] - 2021-08-01 (requires at least SDA v1.6.0)
- Added support for self-referencing types.
- Added validation of unordered groups.
- Fixed validation of sequence groups.

### [1.2.0] - 2021-04-27 (requires at least SDA v1.5.1)
- Added Parser interface with verify().
- Added Validator interface.
- Fixed interval range validation.
- Fixed choice/group validation.
- Extensive refactoring and hardening.
- Tag 'multiplicity' renamed to 'occurs'.
- Interval limiting fields renamed to min/max.

### [1.1.1] - 2021-03-24 (requires at least SDA v1.5.0)
- Improved validation error messages.

### [1.1.0] - 2021-03-02 (requires at least SDA v1.4.2)
- Added SDA Validator.
- Finalized SDS Parser.

### [1.0.0] - 2020-10-06 (requires at least SDA v1.4.1)
- First attempt at parsing SDS input.
