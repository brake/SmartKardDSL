# Changelog

## [0.1.0]
### Changed
 - all UPPER_CASE factory methods (i.e. `SELECT`) moved to context of `CardChannel`. They construct APDU and transmit it immediately to the card returning `ResponseAPDU` with result of transmission. (#2) 
 - all lowerCamelCase factory methods (i.e. `readRecord`, `updateBinary`) are remaining in a global context, they just construct `CommandAPDU` object (#2)
 
## [0.0.2]
### Removed
 - `GET_RESPONSE` (#1)

## [0.0.1] - 2019-07-28
### Added
- Initial release

