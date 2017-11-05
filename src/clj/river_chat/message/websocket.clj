(ns river-chat.message.websocket
  (:require [clojure.tools.logging :as log]
            [immutant.web.async :as async]
            [mount.core :as mount]))


(defonce channels (atom #{}))


(defn connect! [channel]
  (log/info "channel open")
  (swap! channels conj channel))


(defn disconnect! [channel {:keys [code reason]}]
  (log/info "close code:" code "reason:" reason)
  (swap! channels #(remove #{channel} %)))


(def websocket-callbacks
  "WebSocket callback functions"
  {:on-open connect!
   :on-close disconnect!
   :on-message #()})


(defn send-message! [message]
  (doseq [channel @channels]
    (async/send! channel message)))


(defn ws-handler [request]
  (async/as-channel request websocket-callbacks))