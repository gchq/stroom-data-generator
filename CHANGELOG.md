# Change Log

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/) 
and this project adheres to [Semantic Versioning](http://semver.org/).

i.e. `<Major version>.<Minor version>.<Patch version>`

The namespace of the schema includes the major version, e.g. _event-logging:3_. This is to reflect the fact that a change to the major version number is a breaking change and thus a different namespace should be used. Similarly the filename of the schema as a release artifact will include the major version number to reflect a breaking change.

Minor and patch versions will be backwards compatible with other versions at the same major version number. The minor and patch version numbers are included in the _version_ and _id_ attributes of the _xs:schema_ element.

Minor version changes may included new optional elements or attributes. They may also include changes to such things as enumerations or patterns that are additive in nature.

Patch version changes will typically include cosmetic changes (e.g. _xs:documentation_ changes).


## [Unreleased]

## [v1.2.1]

### Changed

* Changed "Feed Name" to "Feed" in search templates, required by updated Stroom versions.


## [v1.2.0]

### Changed

* Changed "Feed Name" to "Feed" in Stroom config, required by updated Stroom versions.

## [v1.1.2]

### Changed

* Fixed bug in findErrorStreams.sh script when supplying alternative API URL
* Added option to allow runTestSearch.sh script to be supplied an alternative URL
* Changed expected output from runTestSearch.sh to not be sensitive to API changes

## [v1.1.1]

### Added

* CHANGELOG.md

### Changed

* Various scripts to display version number on starting.
* Moved markdown into docs directory (README.md or CHANGELOG.md remain in root)

## [v1.1.0] 

### Changed

* Send reference data to stroom using EffectiveDate header parameter.

## [v1.0.0]

### Changed 

* Minor bug fixes and initial release.

## [v0.13]

* Under development.


[Unreleased]: https://github.com/gchq/stroom-data-generator/compare/v1.1.1...HEAD
[v1.1.1]: https://github.com/gchq/stroom-data-generator/compare/v1.1.1...v1.1.o
[v1.1.0]: https://github.com/gchq/event-logging-schema/compare/v1.1.0...v1.0.0
[v1.0.0]: https://github.com/gchq/event-logging-schema/compare/v1.0.0...v0.13
[v0.13]: https://github.com/gchq/event-logging-schema/compare/v0.13...v0.13

