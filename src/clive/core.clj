(ns clive.core
  (:import [org.apache.hadoop.hive.service HiveClient]
           [org.apache.thrift.transport TSocket]
           [org.apache.thrift.protocol TBinaryProtocol]))

(defonce _connection (atom nil))
(defonce _socket (atom nil))

(defn hive-socket
  [host port]
  (doto (TSocket. host port) .open))

(defn hive-client
  [socket]
  (HiveClient. (TBinaryProtocol. socket)))

(defn open
  [host port]
  (println "Opening connection")
  (let [sock (hive-socket host port)]
   (reset! _connection (hive-client sock))
   (reset! _socket sock)))

(defn close
  []
  (println "Closing connection")
  (.close @_socket))

(defn fetch-all
  [q]
  (.execute @_connection q)
  (.fetchAll @_connection))

(defmacro openclose
  [host port & body]
  `(open host port)
  `(do ~@body)
  `(close)
  )
  
(defn -main
  [& args]
  (openclose "b57" 10000 (println (fetch-all "describe deals_production_people"))))


;(defn -main
;  [& args]
;  (connect "b57" 10000)
;  (println (fetch-all "describe deals_production_people"))
;  (disconnect))

;(defn -main
;  [& args]
;  (with-open [sock (hive-socket "b57" 10000)]
;    (let [client (hive-client sock)
;          result (.execute client "describe deals_production_people")]
;          (.fetchAll client))))

