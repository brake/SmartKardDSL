# Smart Card interacting DSL for Kotlin

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)

An attempt to create Kotlin DSL around `javax.smartcardio` [abstractions](https://docs.oracle.com/javase/7/docs/jre/api/security/smartcardio/spec/javax/smartcardio/package-summary.html) 

This version is an early prototype of DSL.

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
                        transmit {
                            apdu {
                                ins { Instructions.Select }
                                p1 { 0 }
                                p2 { 0 }
                                dataHex { "3F00" }
                            }
                        }.assert("Invalid SW") {
                            sw1 == 0x9F
                        }
                        
                        transmit {
                            SELECT {
                                file(MF.EF_DIR)    
                            }
                        }
                        
                        transmit {
                            GET_RESPONSE
                        }
                        // analyze File Control Parameters
                        
                        transmit {
                            READ_RECORD {
                                recordNum(1)
                            }.withResult {  // ResponseAPDU
                                // read AID
                                val aid = data // really a part of data
                                transmit {
                                    SELECT {
                                        application(aid)
                                    }
                                }
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

- [ ] functions for transparent files which able to read/write full contents of the file at once, not 255 bytes chunks as `READ BINARY` and `WRITE BINARY` are doing.
