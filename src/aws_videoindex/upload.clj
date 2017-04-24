(ns aws-videoindex.upload
  (:require [amazonica.aws.s3 :as s3]
            [environ.core :refer [env]]))

(defn- get-file-suffix [name]
  (->> name
    (re-find #"\.[a-zA-Z0-9]+$")
    (clojure.string/lower-case)))

(defn sign-s3 [file-name file-type title date]
  (let [suffix (get-file-suffix file-name)
        new-file-name (str date "_" title suffix)
        signed-url (s3/generate-presigned-url
                      {:bucket-name (env :aws-s3-bucket-upload)
                       :key new-file-name
                       :content-type file-type
                       :expires 60
                       :method "PUT"})
        url (str "https://" (env :aws-s3-bucket-upload) ".s3.amazonaws.com/" new-file-name)]
    {:body {:signedRequest (str signed-url)
            :url url
            :filename new-file-name}}))
