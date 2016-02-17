(ns kafka-cloudwatch.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clj-kafka.offset :as offset]
            [zookeeper :as zk]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string])
  (:gen-class kafka-cloudwatch.core))

(def cli-options
  [
    ;; list all the instances or security groups
    ["-l" "--list ENTITY" "list entity - either brokers, topics, partitions, or offsets."]
    ["-z" "--zookeeper HOSTS" "zookeeper hosts and the namespace if any e.g. localhost:2181/kafka"]
    ["-h" "--help"]
    ])

(defn print_usage
  [summary]
  (let [usage (->> ["A little app to peek at kafka clusters"
                    ""
                    "Usage: kafka-cloudwatch [options] arguments"
                    ""
                    "Options:"
                    summary
                    ""] (string/join \newline))]
    (println usage)))

(defn broker-config
  [zkhosts]
  {"zookeeper.connect" zkhosts})

(defn zk-client
  [zkhosts]
  (zk/connect zkhosts))

(defn brokers
  [zkhosts]
  (zk/children (zk-client zkhosts) "/brokers"))

(defn consumers
  [zkhosts]
  (zk/children (zk-client zkhosts) "/consumers"))

(defn topics
  ([zkhosts]
    (zk/children (zk-client zkhosts) "/brokers/topics"))
  ([zkhosts option]
    (when (= option "show")
      (doseq [topic (topics zkhosts)]
        (println topic)))))

(defn partitions-for-topic
  [zkhosts topic]
  (zk/children (zk-client zkhosts) (str "/brokers/topics/" topic "/partitions")))

(defn print-all-partitions
  [zkhosts]
  (let [topics (topics zkhosts)]
    (doseq [topic topics]
      (doseq [partition (partitions-for-topic zkhosts topic)]
               (println (str "topic: " topic ", partitions: " partition))))))

(defn -main
  "I am the main."
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond (:list options)
      (case (:list options)
        "brokers" (println (brokers (:zookeeper options)))
        "topics" (topics (:zookeeper options) "show")
        "partitions" (println (print-all-partitions (:zookeeper options)))
        (print_usage summary)))))