(ns aws-videoindex.indexgenerator
  (:require [amazonica.aws.s3 :as s3]
            [environ.core :refer [env]]
            [clojure.java.io :as io]
            [hiccup.core :as h]))

(defn- generate-url
  [path object]
  (let [signed-url (s3/generate-presigned-url
                     {:bucket-name (env :aws-s3-bucket-target)
                      :key (str path object)
                      :expires 60
                      :method "GET"})]
    (str signed-url)))


(def video-formats
  [{:type "video/webm"
    :ext "video.webm"}
   {:type "video/mp4"
    :ext "video.mp4"}])

(defn- video-to-item
  [{:keys [prefix date title thumbnail]}]
  (h/html
    [:li
      [:div
        [:video {:controls true
                 :poster (generate-url prefix thumbnail)
                 :preload "none"}
          (for [format video-formats]
            [:source {:src (generate-url prefix (:ext format))
                      :type (:type format)}])]]
      [:div date]
      [:div title]]))

(defn- get-year [video]
  (-> video :prefix (subs 0 4)))

(defn- generate-body [videos years]
  (h/html
    (for [year years]
      (list
        [:h1 year]
        [:ul.video
          (map video-to-item (filter #(= year (get-year %1)) videos))]))))

(defn generate-page [videos]
  (let [years (->> videos (map get-year) distinct)]
    (str (slurp (io/resource "index-frontmatter.html"))
      (->> (generate-body videos years)
        (apply str))
      (slurp (io/resource "index-backmatter.html")))))
