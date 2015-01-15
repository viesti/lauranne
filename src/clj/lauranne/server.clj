(ns lauranne.server
  (:require [clojure.java.io :as io]
            [lauranne.dev :refer [is-dev? inject-devmode-html browser-repl start-figwheel]]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [resources]]
            [compojure.handler :refer [api]]
            [net.cgrand.enlive-html :refer [deftemplate]]
            [ring.middleware.reload :as reload]
            [environ.core :refer [env]]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.util.response :refer [response]]
            [clj-http.client :as client]))

(deftemplate page
  (io/resource "index.html") [] [:body] (if is-dev? inject-devmode-html identity))

(defn register [visitor]
  (print "register:\n" visitor)
   {:status 200})

(defn say [body]
  (println body)
  (client/post "http://192.168.103.34:3000/say" {:content-type :json :body {:message "moi"}})
  {:status 200})

(defroutes routes
  (resources "/")
  (resources "/react" {:root "react"})
  (GET "/*" req (page))
  (POST "/register" [visitor] (register visitor))
  (-> (POST "/say" {body :body}
        (say body))
      wrap-json-response
      wrap-json-body)
  (GET "/*" req (page)))

(def http-handler
  (if is-dev?
    (reload/wrap-reload (api #'routes))
    (api routes)))

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
