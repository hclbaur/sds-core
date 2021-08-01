# SDS Release Notes

ISSUES/IDEAS
- validation of unordered groups with repeating components is not supported yet.
- option to control verbosity/abundancy of the SDA validator?

2021-08-01 v1.2.1 (requires SDA v1.6.0):
- Added: support for self-referencing types.
- Added: validation of unordered groups.
- Fixed: validation of sequence groups.

2021-04-27 v1.2.0 (requires SDA v1.5.1):
- Added: Parser interface with verify().
- Added: Validator interface.
- Fixed: interval range validation.
- Fixed: choice/group validation.
- Extensive refactoring and hardening.
- Tag 'multiplicity' renamed to 'occurs'.
- Interval limiting fields renamed to min/max.

2021-03-24 v1.1.1 (requires SDA v1.5.0):
- Improved validation error messages.

2021-03-02 v1.1.0 (requires SDA v1.4.2):
- Added: SDA Validator.
- Finalized SDS Parser.

2020-10-06 v1.0.0 (requires SDA v1.4.1):
- First attempt at parsing SDS input.
