(ns river-chat.chat.subscriptions
  (:require [re-frame.core :as rf]))


(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))


(rf/reg-sub
  :messages
  (fn [db _]
    (:messages db)))