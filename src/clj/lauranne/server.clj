2(ns lauranne.server
  (:require [clojure.java.io :as io]
            [lauranne.dev :refer [is-dev? inject-devmode-html browser-repl start-figwheel]]
            [compojure.core :refer [GET POST defroutes routes]]
            [compojure.route :refer [resources]]
            [compojure.handler :refer [api]]
            [net.cgrand.enlive-html :refer [deftemplate]]
            [ring.middleware.reload :as reload]
            [environ.core :refer [env]]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.util.response :refer [response]]
            [clj-http.client :as client]
            [cheshire.core :as json]
            [monger.core :as mg]
            [monger.collection :as mc]))

(defonce conn (mg/connect {:host "lauranne.cloudapp.net"}))
(deftemplate page
  (io/resource "index.html") [] [:body] (if is-dev? inject-devmode-html identity))

(defn register [{:keys [body] :as req}]
  (let [db (mg/get-db conn "test")
        visitor (json/parse-stream (clojure.java.io/reader body))]
    (println "registering:" visitor)
    (mc/insert db "visitors" visitor))
   {:status 201})

(defn say [{:keys [body]}]
  (println (get body "message"))
  (client/post "http://192.168.103.34:3000/say" {:content-type :json :body (json/generate-string {:message (get body "message")})})
  {:status 200})

(defroutes app
  (resources "/")
  (resources "/react" {:root "react"})
  (GET "/*" req (page))
  (-> (routes (POST "/say" req (say req))
              (POST "/register" req (register req)))
        wrap-json-response
        wrap-json-body)
  (GET "/*" req (page)))

(def http-handler
  (if is-dev?
    (reload/wrap-reload (api #'app))
    (api app)))

(defn run [& [port]]
  (defonce ^:private server
    (do
      (if is-dev? (start-figwheel))
      (let [port (Integer. (or port (env :port) 10555))]
        (print "Starting web server on port" port ".\n")
        (run-server http-handler {:port port
                          :join? false}))))
  server)

(defn -main [& [port]]
  (run port))
