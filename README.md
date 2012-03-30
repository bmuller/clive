# clive

Clive is a clojure library for interacting with Hive via Thrift.

## Usage
Add the following to your project.clj:

    [clive "0.1.0-SNAPSHOT"]

Example usage:

    (raw-query {:host "ahost" :port 10000} "select * from atable limit 1")
