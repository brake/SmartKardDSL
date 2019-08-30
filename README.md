# Smart Card interacting DSL for Kotlin

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt) [![Maven Central](https://img.shields.io/maven-central/v/com.github.brake.smart_card/smartKardDSL.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.brake.smart_card%22%20AND%20a:%22smartKardDSL%22)

An attempt to create Kotlin DSL around `javax.smartcardio` [abstractions](https://docs.oracle.com/javase/7/docs/jre/api/security/smartcardio/spec/javax/smartcardio/package-summary.html) 

This version is an early prototype of DSL.

[![Changelog](https://img.shields.io/badge/CHANGELOG-Click%20Here-green.svg?longCache=true&style=for-the-badge)](CHANGELOG.md)

## Table of Contents

* [Dependency Configuration](#dependency-configuration)
* [Examples](#examples)
* [TODO](#todo)

## Dependency Configuration

#### Gradle

##### Groovy DSL
```groovy
implementation 'com.github.brake.smart_card:smartKardDSL:0.1.0'
```

##### Kotlin DSL
```kotlin
implementation("com.github.brake.smart_card:smartKardDSL:0.1.0")
```

#### Maven

```xml
<dependency>
  <groupId>com.github.brake.smart_card</groupId>
  <artifactId>smartKardDSL</artifactId>
  <version>0.0.1</version>
</dependency>
```

## Examples

### Select application directory (for example `ADF USIM`)

```kotlin
fun test() {
    readers().let {
        if (it.isEmpty())  return

        with(it[0]) {
            if (waitForCardPresent(1000)) {
                try {
                    connectAuto { // CardChannel
                        APDU {  // creates APDU and transmits it immediately returning ResponseAPDU
                            ins { Instructions.Select }
                            p1 { 0 }
                            p2 { 0 }
                            dataHex { "3F00" }
                        }.assert("Invalid SW") {
                            sw1 == 0x9F
                        }
                        
                        SELECT {
                            file(MF.EF_DIR)
                            requestFCP()    
                        }.withResult {
                        // analyze FCP template and retrieve record length and number of records
                        }
                        
                        READ_RECORD {
                            recordNum(1)
                        }.withResult {  // ResponseAPDU
                            // read AID
                            val aid = data // really a part of data
                            SELECT {
                                application(aid)
                            }
                        }
                    
                        // continue in context of selected applications' DF
                    }
                } catch (e: Exception) {
                    // deal with exceptions
                }
            }
        }
    }
}

```

### TODO

- [ ] functions for transparent files which able to read/write full contents of the file at once, not 255 bytes chunks as `READ BINARY` and `WRITE BINARY` are doing
- [ ] function for record based files which able to (optionally lazily) read all the records of this file and represent them as sequence of byte buffers, like `useLines()` function 
- [ ] convenience method `Card.iccid` returning card's [ICCID](https://en.wikipedia.org/wiki/SIM_card#ICCID) 
- [ ] add more known files to enums
- [ ] add `assertSW` methods to `ResponseAPDU` to control contents of the error message when `SW` doesn't equal with expected value or not in a list of expected values
- [ ] ~~add version of `CardChannel.transmit` which able to receive a sequence of `APDU` to be transmitted one by one with optional configurable error check for each `APDU`~~
- [X] ~~or~~ reduce the number of curly braces on `transmit` by moving `apdu` and subclasses in context of `CardChannel` to be able to hide the call of `transmit` under the hood
- [X] remove `GET RESPONSE` command because it doesn't make sense - JRE detects APDU where `GET RESPONSE` is required and issues this command under the hood
- [ ] add simple setter methods without of lambdas to `CommandAPDUBuilder`
