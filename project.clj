(defproject clive "0.1.0-SNAPSHOT"
  :description "A Clojure library for interacting with Hive via Thrift"
  :url "https://github.com/bmuller/clive"
  :license {:name "GNU General Public License Version 3"
            :url "http://www.gnu.org/licenses/gpl-3.0.txt"}
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.apache.hadoop.hive/hive-cli "0.7.1-cdh3u3"]
                 [org.apache.hadoop.hive/hive-common "0.7.1-cdh3u3"]
                 [org.apache.hadoop.hive/hive-contrib "0.7.1-cdh3u3"]
                 [org.apache.hadoop.hive/hive-exec "0.7.1-cdh3u3"]
                 [org.apache.hadoop.hive/hive-hwi "0.7.1-cdh3u3"]
                 [org.apache.hadoop.hive/hive-jdbc "0.7.1-cdh3u3"]
                 [org.apache.hadoop.hive/hive-metastore "0.7.1-cdh3u3"]
                 [org.apache.hadoop.hive/hive-serde "0.7.1-cdh3u3"]
                 [org.apache.hadoop.hive/hive-service "0.7.1-cdh3u3"]
                 [org.apache.hadoop.hive/hive-shims "0.7.1-cdh3u3"]
                 ]
  :repositories {"cloudera" "https://repository.cloudera.com/artifactory/cloudera-repos/"})
