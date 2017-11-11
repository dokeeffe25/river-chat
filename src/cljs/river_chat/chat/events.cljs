(ns river-chat.chat.events
  (:require [ajax.core :as ajax]
            [cljs.spec.alpha :as s]
            [cljs.tools.reader.edn :as edn]
            [cognitect.transit :as transit]
            [day8.re-frame.http-fx]
            [re-frame.core :as rf]
            [river-chat.chat.db :as db]))
(enable-console-print!)

(defn check-and-throw [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))


(def check-spec-interceptor (rf/after (partial check-and-throw ::db/db)))


(def chat-interceptors [check-spec-interceptor
                        rf/trim-v])


(def json-reader (transit/reader :json))

(defn receive-message []
  (fn [msg]
    (rf/dispatch
      [:chat/receive-message (->> msg
                                  .-data
                                  edn/read-string)])))




(defn make-websocket! [url]
  (if-let [chan (js/WebSocket. url)]
    (do (set! (.-onmessage chan) (receive-message))
        chan)
    (do (throw (js/Error. "Websocket connection failed"))
        nil)))


(rf/reg-cofx
  :ws-chan
  (fn [cofx _]
    (assoc cofx :ws-chan (make-websocket! "ws://localhost:3000/ws"))))


(defn initialize-db [cofx _]
  {:db (assoc db/default-db :ws-chan (:ws-chan cofx))})


(rf/reg-event-fx
  :initialize-db
  [(rf/inject-cofx :ws-chan)
   chat-interceptors]
  initialize-db)


(rf/reg-event-db
  :set-active-page
  [chat-interceptors]
  (fn [db [page]]
    (assoc db :page page)))


(rf/reg-event-db
  :chat/receive-message
  [chat-interceptors]
  (fn [db [msg]]
    (update-in db [:messages] conj msg)))


(rf/reg-event-fx
  :chat/send-message
  [chat-interceptors]
  (fn [cofx [message]]
    {:http-xhrio {:method :post
                  :uri "/send-message"
                  :params {:message message}
                  :timeout 5000
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format)
                  :on-success [:chat/send-message-success]
                  :on-failure [:chat/send-message-failure]}}))

(rf/reg-event-db
  :chat/send-message-success
  [chat-interceptors]
  (fn [db _]
    db))