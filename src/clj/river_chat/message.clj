(ns river-chat.message
  (:require [grete.producer :as producer]
            [mount.core :as mount]
            [river-chat.config :as config]
            [taoensso.nippy :as nippy]))


(mount/defstate producer
  :start (producer/producer (config/env :producer))
  :stop (producer/close! producer))


(defn send-message! [message]
  (producer/send! producer "messages" (nippy/freeze message)))
