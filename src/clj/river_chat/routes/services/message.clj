(ns river-chat.routes.services.message
  (:require [clojure.tools.logging :as log]
            [ring.util.http-response :as http-response]
            [river-chat.message :as message]))

(defn send-message! [message]
  ;; TODO Validation
  (try
    (message/send-message! message)
    (-> {:result :ok}
        http-response/ok)
    (catch Exception e
      (log/error e "Could not send message"))))
