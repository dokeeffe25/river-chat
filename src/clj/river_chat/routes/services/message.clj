(ns river-chat.routes.services.message
  (:require [clojure.tools.logging :as log]
            [immutant.web.async :as async]
            [ring.util.http-response :as http-response]
            [river-chat.message.producer :as producer]))

(defn send-message! [message]
  ;; TODO Validation
  (try
    (producer/send-message! message)
    (-> {:result :ok}
        http-response/ok)
    (catch Exception e
      (log/error e "Could not send message"))))
