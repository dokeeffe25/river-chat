(ns river-chat.chat.subscriptions
  (:require [re-frame.core :as rf]))


(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))
