(ns river-chat.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [river-chat.layout :refer [error-page]]
            [river-chat.routes.home :refer [home-routes]]
            [river-chat.routes.services :refer [service-routes]]
            [compojure.route :as route]
            [river-chat.env :refer [defaults]]
            [mount.core :as mount]
            [river-chat.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    (-> #'home-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    #'service-routes
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
