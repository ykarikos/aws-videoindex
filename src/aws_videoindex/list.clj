(ns aws-videoindex.list
  (:require [amazonica.aws.s3 :as s3]
            [environ.core :refer [env]]))

(defn- get-object-list []
  (let [response (s3/list-objects
                    {:bucket-name (env :aws-s3-bucket-target)
                     :delimiter "/"})]
    (:common-prefixes response)))

(defn- to-int
  [num]
  (Integer. num))

(defn- parse-object [object-name]
  (let [[_ year month day title] (re-find #"^([0-9]{4})-([0-9]{2})-([0-9]{2})_(.*)/$" object-name)
        date (str (to-int day) "." (to-int month) "." year)]
    {:prefix object-name
     :date date
     :title title}))

(defn get-videos []
  (let [objects (get-object-list)]
    (->> objects
      (map parse-object)
      (sort #(compare (:prefix %1) (:prefix %2))))))
