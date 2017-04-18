(ns aws-videoindex.transcode
  (:require [amazonica.aws.elastictranscoder :as transcoder]
            [environ.core :refer [env]]))

(defn- get-basename [name]
  (->> name
       (re-find #"^(.+)\.")
       second))

(defn- create-transcoder-job
  [inputfile]
  (transcoder/create-job
    {:endpoint (env :aws-region)}
    {:pipeline-id (env :aws-transcoder-pipeline)
     :output-key-prefix (str (get-basename inputfile) "/")
     :input {:key inputfile}
     :outputs
        [ {:key "video.mp4" :preset-id (env :aws-transcoder-mp4) :thumbnail-pattern "thumbnail-{count}"}
          {:key "video.webm" :preset-id (env :aws-transcoder-webm) }] }))

(defn create-job
  [inputfile]
  (let [job (create-transcoder-job inputfile)
        id (-> job :job :id)]
    {:body {:id id}}))

(defn get-job-status
  [id]
  (let [status (transcoder/read-job {:endpoint (env :aws-region)} {:id id})
        status-code (-> status :job :status)]
   {:body {:status status-code}}))
