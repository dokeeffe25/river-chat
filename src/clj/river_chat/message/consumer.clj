(ns river-chat.message.consumer
  (:require [clojure.tools.logging :as log]
            [grete.consumer :as consumer]
            [mount.core :as mount]
            [river-chat.config :as config]
            [river-chat.message.websocket :as ws]
            [taoensso.nippy :as nippy])
  (:import (org.apache.kafka.common.errors WakeupException)))


(defn process-message! [message]
  (-> message
      (.value)
      nippy/thaw
      ws/send-message!))


(defn stop-consumer! [consumer-state]
  (reset! (:continue? consumer-state) false)
  (consumer/wakeup! (:consumer consumer-state)))


(mount/defstate consumer
  :start (let [consumer  (consumer/consumer (config/env :consumer))
               continue? (atom true)]
           (consumer/subscribe! consumer ["messages"])
           (future
             (try
               (while @continue?
                 (try
                   (let [messages (consumer/poll! consumer 1000)]
                     (doseq [message messages]
                       (process-message! message)))
                   (catch WakeupException _)))
               (finally
                 (consumer/close! consumer))))
           {:consumer  consumer
            :continue? continue?})
  :stop (stop-consumer! consumer))
