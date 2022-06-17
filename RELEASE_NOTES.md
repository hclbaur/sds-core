# Release Notes

## [1.2.2] - 2022-06-17
- `Fixed` some issues with type casting.
- `Added` demo and documentation in markdown.
- `Removed` documentation in ODT format.

## Known issues / ideas

- validation of unordered groups with repeating components is not supported yet.
- option to control verbosity/abundancy of the SDA validator?

## Compatibility

- Requires at least SDA 1.6.0.
- Backwards compatible down to SDS 1.2.0.

## Previous releases

### [1.2.1] - 2021-08-01 (requires SDA v1.6.0)
- Added support for self-referencing types.
- Added validation of unordered groups.
- Fixed validation of sequence groups.

### [1.2.0] - 2021-04-27 (requires SDA v1.5.1)
- Added Parser interface with verify().
- Added Validator interface.
- Fixed interval range validation.
- Fixed choice/group validation.
- Extensive refactoring and hardening.
- Tag 'multiplicity' renamed to 'occurs'.
- Interval limiting fields renamed to min/max.

### [1.1.1] - 2021-03-24 (requires SDA v1.5.0)
- Improved validation error messages.

### [1.1.0] - 2021-03-02 (requires SDA v1.4.2)
- Added SDA Validator.
- Finalized SDS Parser.

### [1.0.0] - 2020-10-06 (requires SDA v1.4.1)
- First attempt at parsing SDS input.
