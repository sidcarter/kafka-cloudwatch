(ns kafka-cloudwatch.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clj-kafka.zk :as zk])
  (:gen-class kafka-cloudwatch.core))

(defn broker-config
  [hosts]
  {"zookeeper.connect" (str hosts ":2181")
   "group.id" "clj-kafka.consumer"})

(defn get-brokers
  [hosts]
  (zk/brokers (broker-config hosts)))

(defn get-all-topics
  [hosts]
  (zk/topics (broker-config hosts)))

(defn get-partitions-for-topic
  [hosts topic]
  (zk/partitions (broker-config hosts) topic))

(defn print-all-partitions
  [hosts]
  (let [topics (get-all-topics hosts)]
    (doseq [topic topics]
      (println (str "topic: " topic ", partitions: " (get-partitions-for-topic hosts topic))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [hosts (first *command-line-args*)
        chroot (second *command-line-args*)]
    (when hosts
      (print-all-partitions hosts))))