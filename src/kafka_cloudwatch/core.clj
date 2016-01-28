(ns kafka-cloudwatch.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clj-kafka.zk :as zk])
  (:gen-class kafka-cloudwatch.core))

(defn broker-config
  [zkhosts]
  {"zookeeper.connect" (str zkhosts ":2181")
   "group.id" "clj-kafka.consumer"})

(defn get-brokers
  [zkhosts]
  (zk/brokers (broker-config zkhosts)))

(defn get-all-topics
  [zkhosts]
  (zk/topics (broker-config zkhosts)))

(defn get-partitions-for-topic
  [zkhosts topic]
  (zk/partitions (broker-config zkhosts) topic))

(defn print-all-partitions
  [zkhosts]
  (let [topics (get-all-topics zkhosts)]
    (doseq [topic topics]
      (println (str "topic: " topic ", partitions: " (get-partitions-for-topic zkhosts topic))))))

(defn -main
  "Just the basic stuff for now."
  [& args]
  (let [zkhosts (first *command-line-args*)
        chroot (second *command-line-args*)]
    (when zkhosts
      (print-all-partitions zkhosts))))