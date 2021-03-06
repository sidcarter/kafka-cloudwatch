(defproject kafka-cloudwatch "0.1.0"
  :description "An app to push Kafka lag alerts to AWS Cloudwatch"
  :url "http://sidcarter.com"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :jvm-opts ["-Dlog4j.configuration=file:log4j.properties"]

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-kafka "0.3.4"]
                 [zookeeper-clj "0.9.1"]
                 [org.clojure/tools.cli "0.3.3"]]

  :main ^:skip-aot kafka-cloudwatch.core

  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}})
