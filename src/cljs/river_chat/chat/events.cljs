(ns river-chat.chat.events
  (:require [day8.re-frame.http-fx]
            [river-chat.chat.db :as db]
            [re-frame.core :as rf]
            [cljs.spec.alpha :as s]
            [ajax.core :as ajax]))


(defn check-and-throw [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))


(def check-spec-interceptor (rf/after (partial check-and-throw ::db/db)))


(def chat-interceptors [check-spec-interceptor
                        rf/trim-v])


(rf/reg-event-db
  :initialize-db
  [chat-interceptors]
  (fn [_ _]
    db/default-db))


(rf/reg-event-db
  :set-active-page
  [chat-interceptors]
  (fn [db [page]]
    (assoc db :page page)))


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