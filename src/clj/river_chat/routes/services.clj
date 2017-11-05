(ns river-chat.routes.services
  (:require [buddy.auth :refer [authenticated?]]
            [buddy.auth.accessrules :refer [restrict]]
            [compojure.api.meta :refer [restructure-param]]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [river-chat.message.websocket :as ws]
            [river-chat.routes.services.message :as services.message]
            [schema.core :as s]))


(s/defschema Result
  {:result                   s/Keyword
   (s/optional-key :message) String})


(s/defschema Message
  {:message String})


(defn access-error [_ _]
  (unauthorized {:error "unauthorized"}))


(defn wrap-restricted [handler rule]
  (restrict handler {:handler  rule
                     :on-error access-error}))


(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [wrap-restricted rule]))


(defmethod restructure-param :current-user
  [_ binding acc]
  (update-in acc [:letks] into [binding `(:identity ~'+compojure-api-request+)]))


(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "River Chat API"
                           :description "Services"}}}}
  (POST "/send-message" req
    :return Result
    :body [message Message]
    :summary "send a message"
    (services.message/send-message! message)))


(defroutes websocket-routes
  (GET "/ws" [] ws/ws-handler))
