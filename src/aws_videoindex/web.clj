(ns aws-videoindex.web
  (:gen-class)
  (:require [compojure.core :refer :all]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.json :refer [wrap-json-response]]
            [aws-videoindex.transcode :as transcode]
            [aws-videoindex.upload :as upload]))

(defroutes app-routes
  (GET "/upload" []
       (slurp (io/resource "upload.html")))

; API
  (wrap-json-response
    (GET "/api/sign-s3" [file-name file-type title date]
        (upload/sign-s3 file-name file-type title date)))

  (wrap-json-response
    (GET "/api/create-job" [file-name]
        (transcode/create-job file-name)))
  (wrap-json-response
    (GET "/api/get-job-status" [id]
        (transcode/get-job-status id)))

  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(def app (wrap-params app-routes))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 3000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))
