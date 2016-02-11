(ns kafka-cloudwatch.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clj-kafka.zk :as zk]
            [clj-kafka.offset :as offset]
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
  {"zookeeper.connect" zkhosts
   "group.id" "clj-kafka.consumer"})

(defn get-brokers
  [zkhosts]
  (zk/brokers (broker-config zkhosts)))

(defn get-all-topics
  ([zkhosts]
    (zk/topics (broker-config zkhosts)))
  ([zkhosts option]
    (when (= option "show")
      (doseq [topic (get-all-topics zkhosts)]
        (println topic)))))

(defn get-partitions-for-topic
  [zkhosts topic]
  (zk/partitions (broker-config zkhosts) topic))

(defn get-offsets-for-topic
  [zkhosts topic]
  (offset/fetch-consumer-offsets zkhosts (broker-config zkhosts) topic topic))

(defn print-all-partitions
  [zkhosts]
  (let [topics (get-all-topics zkhosts)]
    (doseq [topic topics]
      (println (str "topic: " topic ", partitions: " (get-partitions-for-topic zkhosts topic))))))

(defn print-all-offsets
  [zkhosts]
  (let [topics (get-all-topics zkhosts)]
    (doseq [topic topics]
      (println (str "topic: " topic ", partitions: " (get-offsets-for-topic zkhosts topic))))))

(defn -main
  "I am the main."
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond (:list options)
      (case (:list options)
        "brokers" (println (get-brokers (:zookeeper options)))
        "topics" (get-all-topics (:zookeeper options) "show")
        "partitions" (println (get-brokers (:zookeeper options)))
        "offsets" (print-all-offsets (:zookeeper options))
        (print_usage summary)))))