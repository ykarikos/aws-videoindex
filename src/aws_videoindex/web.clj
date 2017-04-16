(ns aws-videoindex.web
  (:require [compojure.core :refer :all]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.json :refer [wrap-json-response]]
            [aws-videoindex.upload :as upload]))

(defroutes app-routes
  (GET "/upload" []
       (slurp (io/resource "upload.html")))
  (wrap-json-response
    (GET "/sign-s3" [file-name file-type title date]
        (upload/sign-s3 file-name file-type title date)))
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(def app (wrap-params app-routes))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 3000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))
