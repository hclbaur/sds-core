# Release Notes

This release introducdes a few refactoring changes with limited impact.

## [2.1.0] - 2022-??-??
- `Added` abstract superclass MixedType for types with simple content.
- `Changed` Content Enum moved to package be.baur.sds.content.

## Compatibility
- Requires at least Java 8 and SDA 2.0.0.

## Previous releases

### [2.0.0] - 2022-10-09 (requires at least SDA v2.0.0)

This release includes full SDS 2 schema support, for specification and 
validation of SDA documents, syntax version 2. You will have to update 
existing schema files, as SDS 2 is not backwards compatible with SDS 1.
Also, the library itself has changed quite a bit, but unless you have
been writing an SDS parser, there will be little impact when upgrading.

- `Added` NodeType to replace SimpleType and ComplexType.
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
- `Fixed` bug in validation of unordered groups.
- `Added` support for repeating components in unordered groups.

### [1.2.2] - 2022-06-17
- `Fixed` some issues with type casting.
- `Added` demo and documentation in markdown.
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
