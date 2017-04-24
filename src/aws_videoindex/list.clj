(ns aws-videoindex.list
  (:require [amazonica.aws.s3 :as s3]
            [environ.core :refer [env]]))

(defn- get-object-list []
  (let [response (s3/list-objects
                    {:bucket-name (env :aws-s3-bucket-target)
                     :delimiter "/"})]
    (:common-prefixes response)))

(defn- get-thumbnail
  [prefix]
  (let [thumbnail "thumbnail-00002.jpg"
        thumbnail-path (str prefix thumbnail)
        response (s3/list-objects (env :aws-s3-bucket-target) thumbnail-path)
        object-count (-> response :object-summaries count)]
    (if (= 1 object-count)
      thumbnail
      "thumbnail-00001.jpg")))

(defn- to-int
  [num]
  (Integer. num))

(defn- parse-object [object-name]
  (let [[_ year month day title] (re-find #"^([0-9]{4})-([0-9]{2})-([0-9]{2})_(.*)/$" object-name)
        date (str (to-int day) "." (to-int month) "." year)]
    {:prefix object-name
     :date date
     :title title
     :thumbnail (get-thumbnail object-name)}))

(defn get-videos []
  (let [objects (get-object-list)]
    (->> objects
      (map parse-object)
      (sort #(compare (:prefix %1) (:prefix %2))))))
