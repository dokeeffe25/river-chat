(ns river-chat.message
  (:require [clojure.tools.logging :as log]
            [grete.consumer :as consumer]
            [grete.producer :as producer]
            [mount.core :as mount]
            [river-chat.config :as config]
            [taoensso.nippy :as nippy])
  (:import org.apache.kafka.common.errors.WakeupException))

(mount/defstate producer
  :start (producer/producer (config/env :producer))
  :stop (producer/close! producer))


(defn send-message! [message]
  (producer/send! producer "messages" (nippy/freeze message)))


(defn process-message [message]
  (log/info "MESSAGE" (-> message
                          (.value)
                          nippy/thaw)))


(defn stop-consumer! [consumer-state]
  (reset! (:continue? consumer-state) false)
  (consumer/wakeup! (:consumer consumer-state)))


(mount/defstate consumer
  :start (let [consumer (consumer/consumer (config/env :consumer))
               continue? (atom true)]
           (consumer/subscribe! consumer ["messages"])
           (future
             (try
               (while @continue?
                 (try
                   (let [messages (consumer/poll! consumer 1000)]
                     (doseq [message messages]
                       (process-message message)))
                   (catch WakeupException _)))
               (finally
                 (consumer/close! consumer))))
           {:consumer consumer
            :continue? continue?})
  :stop (stop-consumer! consumer))
