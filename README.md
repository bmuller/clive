# clive

Clive is a clojure library for interacting with Hive via Thrift.

## Usage
Add the following to your project.clj:

    [clive "0.1.0-SNAPSHOT"]

Example usage:

    (open "host" 10000)
    (fetchall "describe some_table")
    (close)

Or, to do it all in one line:

    (openclose "host" 10000 (fetchall "describe some_table"))
