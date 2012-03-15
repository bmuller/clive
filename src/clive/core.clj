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

(defn fetch-all*
  [q]
  (.execute @_connection q)
  (.fetchAll @_connection))

(defn fetch-all
  [q]
  (map #(-> % (.split "\\t") seq) (fetch-all* q)))

(defmacro openclose
  [ host port & body ]
  `(do (open ~host ~port)
       (let [result# ~@body] close result#)))


(defn tab-sep-hash
  [ group ]
  (reduce merge
          (map #(let [pair (-> % .trim (.split "\\t"))]
                  (assoc {} (first pair) (second pair)))
               group)))
          
(defn describe
  [tablename]
  (-> (str "describe " tablename) fetch-all tab-sep-hash))

