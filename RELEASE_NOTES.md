# Release Notes

This release adds full support for SDA 2 and implements SDS 2. 

## [2.0.0] - 2022-xx-xx
- `Removed` the *name* attribute from the SDS syntax.

## Compatibility

- Requires at least Java 8 and SDA 2.0.0.

## Previous releases

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
