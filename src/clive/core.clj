(ns clive.core
  (:import [org.apache.hadoop.hive.service HiveClient]
           [org.apache.thrift.transport TSocket]
           [org.apache.thrift.protocol TBinaryProtocol]
           [org.apache.hadoop.hive.metastore.api Schema]))

;Your import process might do other wierd things that need to be parsed.

(defn- lng-parser [^String i]
  (try (Long/parseLong i)
            (catch java.lang.NumberFormatException e nil)))

(defn- dbl-parser [^String i]
  (try (Double/parseDouble i)
            (catch java.lang.NumberFormatException e nil)))

(defn- bool-parser [i]
  (case i
    "" nil
    "True" true "true" true
    "T" true "t" true
    false))

(defn- str-parser [i]
  (case i "null" nil "Null" nil "" nil i))

(defn- date-parser [^String i]
  (case i
    "null" nil "Null" nil "" nil     
    (try (.parse (java.text.SimpleDateFormat. "yyyy-MM-dd hh:mm:ss.S") i)
      (catch java.text.ParseException e i))))

(defn- parse-field [field [field-name field-type]]
  {(keyword field-name)
   ((cond 
      (re-find #"_at$" field-name) date-parser
      ;I'm almost certainly missing some types here.
      true (case field-type 
             "i32" lng-parser
             "int" lng-parser
             "bool" bool-parser
             "float" dbl-parser
             "double" dbl-parser
             str-parser)) field)})

(defn- parse-fields
  [^Schema schema ^String line]
  (let [line-parsed (.split line "\\t")
        schema-parsed (for [t (.getFieldSchemas schema)]
                        [(.getName t) (.getType t)])]
    (if (not= (count line-parsed) (count schema-parsed))
      (throw (Exception. "Field count mismatch, probably due to embedded tab"))
      (->> (interleave line-parsed schema-parsed)
        (partition 2)
        (map #(apply parse-field %))
        (apply merge)))))

(defn hive-socket [{:keys [host port]}]
  (doto (TSocket. host port) .open))

(defn hive-client
  [socket]
  (HiveClient. (TBinaryProtocol. socket)))

(defn send-query
  [^HiveClient client q]
  (.execute client q)
  client)

(defn query-results
  "Return a lazy batched sequence of results from a client that has already
   accepted a query"
  ([^Schema schema ^HiveClient client]
    (let [r (.fetchN client 10000)]
      (if (pos? (count r))
        (lazy-cat (map #(parse-fields schema %) r) 
                  (query-results schema client)))))
  ([^HiveClient client]
    (let [schema (.getSchema client)]
      (query-results schema client))))

(defn raw-query [hive-credentials q]
  "Create a socket/client, submit the query, and return all the results (non-lazy).
   Because of the latency, you'll likely want to wrap this in a future or use
   it to populate a promise.
   If you want to do something else with the lazy sequence prior to closing the socket,
   adapt this code, replacing doall with your dosync or the like."
  (with-open [s (hive-socket hive-credentials)]
    (let [client (send-query (hive-client s) q)]
      (doall (query-results client)))))
